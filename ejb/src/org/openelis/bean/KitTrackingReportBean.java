/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.meta.OrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.Prompt.Case;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class KitTrackingReportBean {

    @Resource
    private SessionContext    ctx;

    @EJB
    private SessionCacheBean  session;

    @EJB
    private SectionBean       section;

    @EJB
    private CategoryCacheBean categoryCache;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> orderBy;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM_DATE", Prompt.Type.DATETIME).setPrompt("Starting Ordered Date:")
                                                               .setWidth(150)
                                                               .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                               .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                               .setRequired(true)
                                                               .setDefaultValue(null));

            p.add(new Prompt("TO_DATE", Prompt.Type.DATETIME).setPrompt("Ending Sent Date:")
                                                             .setWidth(150)
                                                             .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                             .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                             .setRequired(true)
                                                             .setDefaultValue(null));

            p.add(new Prompt(OrderMeta.getShipFromId(), Prompt.Type.ARRAY).setPrompt("Ship From:")
                                                                          .setWidth(150)
                                                                          .setOptionList(getDictionaryList("laboratory_location"))
                                                                          .setMutiSelect(true));

            p.add(new Prompt("SECTION_ID", Prompt.Type.ARRAY).setPrompt("Section:")
                                                             .setWidth(150)
                                                             .setOptionList(getSections())
                                                             .setMutiSelect(true));

            p.add(new Prompt("SHIP_TO", Prompt.Type.STRING).setPrompt("Ship To:")
                                                           .setWidth(150)
                                                           .setCase(Case.UPPER));

            p.add(new Prompt("REPORT_TO", Prompt.Type.STRING).setPrompt("Report To:")
                                                             .setWidth(150)
                                                             .setCase(Case.UPPER));

            p.add(new Prompt(OrderMeta.getDescription(), Prompt.Type.STRING).setPrompt("Description:")
                                                                            .setWidth(150)
                                                                            .setCase(Case.LOWER));

            p.add(new Prompt(OrderMeta.getStatusId(), Prompt.Type.ARRAY).setPrompt("Status:")
                                                                        .setWidth(150)
                                                                        .setOptionList(getDictionaryList("order_status"))
                                                                        .setMutiSelect(false));

            orderBy = new ArrayList<OptionListItem>();
            orderBy.add(0, new OptionListItem("o_id", "Order#"));
            orderBy.add(1, new OptionListItem("status", "Order status"));
            orderBy.add(2, new OptionListItem("o.ordered_date", "Order date"));
            orderBy.add(3, new OptionListItem("ship_from_name", "Ship from"));
            orderBy.add(4, new OptionListItem("ship_to_name", "Ship to"));
            orderBy.add(5, new OptionListItem("report_to_name", "Report to"));
            orderBy.add(6, new OptionListItem("requested_by", "Requested by"));
            orderBy.add(7, new OptionListItem("cost_center", "Cost center"));

            p.add(new Prompt("SORT_BY", Prompt.Type.ARRAY).setPrompt("Sort By:")
                                                          .setWidth(150)
                                                          .setOptionList(orderBy)
                                                          .setMutiSelect(false));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        URL url;
        Path path;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        String dir, frDate, tDate, section, shipFrom, shipTo, reportTo, description, orderStatus, sortBy, userName;
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("KitTrackingReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        userName = User.getName(ctx);

        frDate = ReportUtil.getSingleParameter(param, "FROM_DATE");
        tDate = ReportUtil.getSingleParameter(param, "TO_DATE");
        section = ReportUtil.getListParameter(param, "SECTION_ID");
        shipFrom = ReportUtil.getListParameter(param, OrderMeta.getShipFromId());
        shipTo = ReportUtil.getSingleParameter(param, "SHIP_TO");
        reportTo = ReportUtil.getSingleParameter(param, "REPORT_TO");
        description = ReportUtil.getSingleParameter(param, OrderMeta.getDescription());
        orderStatus = ReportUtil.getSingleParameter(param, OrderMeta.getStatusId());
        sortBy = ReportUtil.getSingleParameter(param, "SORT_BY");

        if (DataBaseUtil.isEmpty(frDate) || DataBaseUtil.isEmpty(tDate))
            throw new InconsistencyException("You must specify From Date and To Date for this report");

        if ( !DataBaseUtil.isEmpty(section))
            section = " and o.id in (select ot.order_id from order_test ot, test t," +
                      " test_section ts where ot.test_id = t.id and ts.test_id = t.id" +
                      " and ot.order_id = o.id and ts.section_id " + section + ")";
        else
            section = "";

        if ( !DataBaseUtil.isEmpty(shipFrom))
            shipFrom = " and o.ship_from_id " + shipFrom;
        else
            shipFrom = "";

        if ( !DataBaseUtil.isEmpty(shipTo)) {
            shipTo = shipTo.replaceAll("\\*", "%");
            shipTo = " and ship_to.name like '" + shipTo + "'";
        } else {
            shipTo = "";
        }

        if ( !DataBaseUtil.isEmpty(reportTo)) {
            reportTo = reportTo.replaceAll("\\*", "%");
            reportTo = " and o.id in (select oo2.order_id from order_organization oo2," +
                       " organization o2 where oo2.order_id = o.id and oo2.organization_id = o2.id" +
                       " and o2.name like '" + reportTo + "' and oo2.type_id = " +
                       Constants.dictionary().ORG_REPORT_TO + ")";
        } else {
            reportTo = "";
        }

        if ( !DataBaseUtil.isEmpty(description)) {
            description = description.replaceAll("\\*", "%");
            description = " and o.description like '" + description + "'";
        } else {
            description = "";
        }

        if ( !DataBaseUtil.isEmpty(orderStatus))
            orderStatus = " and o.status_id = " + orderStatus;
        else
            orderStatus = "";

        if ( !DataBaseUtil.isEmpty(sortBy))
            sortBy = " order by " + sortBy;
        else
            sortBy = "";
        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/kitTracking/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("FROM_DATE", frDate);
            jparam.put("TO_DATE", tDate);
            jparam.put("USER_NAME", userName);
            jparam.put("SECTION", section);
            jparam.put("SHIP_FROM", shipFrom);
            jparam.put("SHIP_TO", shipTo);
            jparam.put("REPORT_TO", reportTo);
            jparam.put("DESCRIPTION", description);
            jparam.put("STATUS", orderStatus);
            jparam.put("SORT_BY", sortBy);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Outputing report").setPercentComplete(20);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);

            jexport = new JRXlsExporter();
            path = ReportUtil.createTempFile("kitTracking", ".xls", "upload_stream_directory");
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, Files.newOutputStream(path));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            jexport.exportReport();

            status.setPercentComplete(100);

            status.setMessage(path.getFileName().toString())
                  .setPath(path.toString())
                  .setStatus(ReportStatus.Status.SAVED);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                // ignore
            }
        }

        return status;
    }

    private ArrayList<OptionListItem> getSections() {
        ArrayList<OptionListItem> l;
        ArrayList<SectionViewDO> s;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            s = section.fetchList();
            for (SectionViewDO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }

    private ArrayList<OptionListItem> getDictionaryList(String systemName) {
        ArrayList<OptionListItem> l;
        ArrayList<DictionaryDO> d;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            d = categoryCache.getBySystemName(systemName).getDictionaryList();
            for (DictionaryDO data : d)
                l.add(new OptionListItem(data.getId().toString(), data.getEntry()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }
}
