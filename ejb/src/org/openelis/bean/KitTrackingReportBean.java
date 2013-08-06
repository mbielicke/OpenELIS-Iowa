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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
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
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.meta.OrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class KitTrackingReportBean {

    @Resource
    private SessionContext  ctx;
    
    @EJB
    private SessionCacheBean session;
    
    @EJB
    private UserCacheBean   userCache;
    
	/*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        URL url;
        File tempFile;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        String dir, frDate, tDate, section, shipFrom, shipTo, description, orderStatus, sortBy, userName, printer, printstat;
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

        userName = userCache.getName();

        frDate = ReportUtil.getSingleParameter(param, "FROM_DATE");
        tDate = ReportUtil.getSingleParameter(param, "TO_DATE");
        section = ReportUtil.getListParameter(param, "SECTION_ID");
        shipFrom = ReportUtil.getListParameter(param, OrderMeta.getShipFromId());
        shipTo = ReportUtil.getSingleParameter(param, OrderMeta.getOrganizationId());
        description = ReportUtil.getSingleParameter(param, OrderMeta.getDescription());
        orderStatus = ReportUtil.getSingleParameter(param, OrderMeta.getStatusId());
        sortBy = ReportUtil.getSingleParameter(param, "SORT_BY");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(frDate) || DataBaseUtil.isEmpty(tDate))
            throw new InconsistencyException("You must specify From Date and To Date for this report");

        if ( !DataBaseUtil.isEmpty(section))
            section = " and o.id in (select ot.order_id from order_test ot, test t, test_section ts where ot.test_id = t.id and" +
            		" ts.test_id = t.id and ot.order_id = o.id and ts.section_id " + section + ")";
        else
            section = "";

        if ( !DataBaseUtil.isEmpty(shipFrom))
            shipFrom = " and o.ship_from_id " + shipFrom;
        else
            shipFrom = "";
        
        if ( !DataBaseUtil.isEmpty(shipTo))
            shipTo = " and org.id = " + shipTo;
        else
            shipTo = "";
        
        if ( !DataBaseUtil.isEmpty(description))
            description = " and o.description = '" + description + "'";
        else
            description = "";
        
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

            tempFile = File.createTempFile("kitTracking", ".pdf", new File("/tmp"));

            jparam = new HashMap<String, Object>();
            jparam.put("FROM_DATE", frDate);
            jparam.put("TO_DATE", tDate);
            jparam.put("USER_NAME", userName);
            jparam.put("SECTION", section);
            jparam.put("SHIP_FROM", shipFrom);
            jparam.put("SHIP_TO", shipTo);
            jparam.put("DESCRIPTION", description);
            jparam.put("STATUS", orderStatus);
            jparam.put("SORT_BY", sortBy);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Loading report");

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);

            jexport = new JRPdfExporter();
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setMessage("Outputing report").setPercentComplete(20);

            jexport.exportReport();

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(tempFile, printer, 1);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
            }
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
}
