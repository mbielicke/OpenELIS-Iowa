/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class QASummaryReportBean {

    @EJB
    private SessionCacheBean session;
    
    @Resource
    private SessionContext  ctx;

    @EJB
    private SectionBean     section;

    @EJB
    private TestBean       test;
    
    @EJB
    private PrinterCacheBean printers;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, detail;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM_ENTERED", Prompt.Type.DATETIME).setPrompt("Starting Entered Date:")
                                                                  .setWidth(150)
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                  .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                                       Messages.get()
                                                                                                               .dateTimePattern()))
                                                                  .setRequired(true));

            p.add(new Prompt("TO_ENTERED", Prompt.Type.DATETIME).setPrompt("Ending Entered Date:")
                                                                .setWidth(150)
                                                                .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                                     Messages.get()
                                                                                                             .dateTimePattern()))
                                                                .setRequired(true));

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(200)
                                                          .setOptionList(getSections())
                                                          .setMutiSelect(true));

            p.add(new Prompt("TEST", Prompt.Type.ARRAY).setPrompt("Test Name:")
                                                       .setWidth(300)
                                                       .setOptionList(getTests())
                                                       .setMutiSelect(true));
            
            detail = new ArrayList<OptionListItem>();
            detail.add(new OptionListItem("Details", "Full Details"));
            detail.add(new OptionListItem("Summaries", "Client Summaries"));
            detail.add(new OptionListItem("Totals", "Grand Totals Only"));

            p.add(new Prompt("DETAIL", Prompt.Type.ARRAY).setPrompt("Detail:")
                                                         .setWidth(200)
                                                         .setOptionList(detail)
                                                         .setMutiSelect(false)
                                                         .setRequired(true));
            
            prn = printers.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    @TransactionTimeout(600)
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
        String fromDate, toDate, section, test, detail, userName, printer, dir, printstat;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("QASummaryReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        fromDate = ReportUtil.getSingleParameter(param, "FROM_ENTERED");
        toDate = ReportUtil.getSingleParameter(param, "TO_ENTERED");
        section = ReportUtil.getListParameter(param, "SECTION");
        test = ReportUtil.getListParameter(param, "TEST");
        detail = ReportUtil.getSingleParameter(param, "DETAIL");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(fromDate) || DataBaseUtil.isEmpty(toDate) ||
           DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify From Date, To Date, Status and printer for this report");

        if (fromDate != null && fromDate.length() > 0)
            fromDate += ":00";

        if (toDate != null && toDate.length() > 0)
            toDate += ":59";

        if ( !DataBaseUtil.isEmpty(section))
            section = " and se.id " + section;
        else
            section = "";

        if ( !DataBaseUtil.isEmpty(test))
            test = " and t.id " + test;
        else
            test = "";        

        userName = User.getName(ctx);
        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/qasummary/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            path = ReportUtil.createTempFile("qasummary", ".pdf", null);

            jparam = new HashMap<String, Object>();
            jparam.put("BEGIN_ENTERED", fromDate);
            jparam.put("END_ENTERED", toDate);
            jparam.put("SECTION", section);
            jparam.put("TEST", test);
            jparam.put("STYLE", detail);
            jparam.put("SUBREPORT_DIR", dir);
            jparam.put("USER_NAME", userName);

            status.setMessage("Outputing report").setPercentComplete(20);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            if (ReportUtil.isPrinter(printer))
                path = export(jprint, null);
            else 
                path = export(jprint, "upload_stream_directory");

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(path, userName, printer, 1, true);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                status.setMessage(path.getFileName().toString())
                      .setPath(path.toString())
                      .setStatus(ReportStatus.Status.SAVED);
            }
        } catch (Exception e) {
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
        ArrayList<SectionViewDO> s;
        ArrayList<OptionListItem> l;

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

    private ArrayList<OptionListItem> getTests() {
        ArrayList<TestMethodVO> t;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            t = test.fetchList();
            for (TestMethodVO n : t)
                if ("N".equals(n.getIsActive()))
                    l.add(new OptionListItem(n.getTestId().toString(), n.getTestName() + ", " +
                                             n.getMethodName() + 
                                             " ["+n.getActiveBegin()+".."+n.getActiveEnd()+"]"));
                else
                    l.add(new OptionListItem(n.getTestId().toString(), n.getTestName() + ", " +
                                                                       n.getMethodName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }
    
    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;

        jexport = new JRPdfExporter();
        path = ReportUtil.createTempFile("qasummary", ".pdf", systemVariableDirectory);
        jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, Files.newOutputStream(path));
        jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
        jexport.exportReport();

        return path;
    }
}