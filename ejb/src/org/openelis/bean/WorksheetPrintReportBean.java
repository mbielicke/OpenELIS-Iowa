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

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.report.worksheetPrint.DiagramDataSource;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class WorksheetPrintReportBean {

    @Resource
    private SessionContext                     ctx;

    @EJB
    private SessionCacheBean                   session;

    @EJB
    private PrinterCacheBean                   printers;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, format;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("WORKSHEET_ID", Prompt.Type.INTEGER).setPrompt("Worksheet #:")
                                                                 .setWidth(130)
                                                                 .setHidden(true)
                                                                 .setRequired(true));

            format = new ArrayList<OptionListItem>();
            format.add(new OptionListItem("4x12B", "4 Slides 12 Blank Wells"));
            format.add(new OptionListItem("6x10", "6 Slides 10 Wells"));
            format.add(new OptionListItem("96H", "96 Well Plate Horizontal"));
            format.add(new OptionListItem("96V", "96 Well Plate Vertical"));
            format.add(new OptionListItem("LLS", "Line List Single Line"));
            format.add(new OptionListItem("LLM", "Line List Multi Line"));

            p.add(new Prompt("FORMAT", Prompt.Type.ARRAY).setPrompt("Format:")
                                                         .setWidth(200)
                                                         .setOptionList(format)
                                                         .setMultiSelect(false)
                                                         .setRequired(true));

            prn = printers.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
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
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        Connection con;
        HashMap<String, Object> jparam;
        HashMap<String, QueryData> param;
        JasperPrint jprint;
        JasperReport jreport;
        Path path;
        DiagramDataSource dDS;
        ReportStatus status;
        String format, dir, printer, printstat, userName, worksheetId;
        URL url;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("WorksheetPrintReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        worksheetId = ReportUtil.getSingleParameter(param, "WORKSHEET_ID");
        format = ReportUtil.getSingleParameter(param, "FORMAT");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(worksheetId) || DataBaseUtil.isEmpty(format) ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the worksheet id, format and printer for this report");

        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");
            
            con = ReportUtil.getConnection(ctx);
            
            userName = User.getName(ctx);

            jparam = new HashMap<String, Object>();
            jparam.put("USER_NAME", userName);

            jprint = null;
            switch (format) {
                case "4x12B":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide4x12Blank.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;
            
                case "6x10":
                    dDS = DiagramDataSource.getInstance(Integer.parseInt(worksheetId), 60);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/slide6x10.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;
            
                case "96H":
                    dDS = DiagramDataSource.getInstance(Integer.parseInt(worksheetId), 96);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plate96Horizontal.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;
            
                case "96V":
                    dDS = DiagramDataSource.getInstance(Integer.parseInt(worksheetId), 96);
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/plate96Vertical.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, dDS);
                    break;
            
                case "LLS":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/lineListSingleLine.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    jparam.put("SUBREPORT_DIR", dir);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;
            
                case "LLM":
                    url = ReportUtil.getResourceURL("org/openelis/report/worksheetPrint/lineListMultiLine.jasper");
                    dir = ReportUtil.getResourcePath(url);
                    jparam.put("WORKSHEET_ID", worksheetId);
                    jparam.put("SUBREPORT_DIR", dir);
                    status.setMessage("Outputing report").setPercentComplete(20);
                    jreport = (JasperReport)JRLoader.loadObject(url);
                    jprint = JasperFillManager.fillReport(jreport, jparam, con);
                    break;
                    
                default:
                    throw new InconsistencyException("An invalid format was specified for this report");
            }
            
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
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return status;
    }

    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;

        jexport = new JRPdfExporter();
        path = ReportUtil.createTempFile("worksheetPrint", ".pdf", systemVariableDirectory);
        jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, Files.newOutputStream(path));
        jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
        jexport.exportReport();

        return path;
    }
}