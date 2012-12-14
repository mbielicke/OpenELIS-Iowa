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
import org.openelis.domain.OptionListItem;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.Prompt;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.OrderLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.RequestformReportLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.remote.RequestformReportRemote;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class RequestformReportBean implements RequestformReportRemote, RequestformReportLocal {

    @Resource
    private SessionContext    ctx;

    @EJB
    private SessionCacheLocal session;

    @EJB
    private OrderLocal        order;
    
    @EJB
    private PrinterCacheLocal  printers;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("ORDERID", Prompt.Type.INTEGER).setPrompt("Order #:")
                                                          .setWidth(100)
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
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int copies;
        String orderId, printer, dir, printstat, useNumForms;
        URL url;
        File tempFile;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;        
        OrderViewDO data;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("OrderReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        orderId = ReportUtil.getSingleParameter(param, "ORDERID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
        useNumForms = ReportUtil.getSingleParameter(param, "USE_NUM_FORMS");
        if (DataBaseUtil.isEmpty(orderId) || DataBaseUtil.isEmpty(printer)) {
            throw new InconsistencyException("You must specify the order # and printer for this report");
        } else {
            try {
                data = order.fetchById(Integer.parseInt(orderId));
                if (!"S".equals(data.getType()))
                    throw new InconsistencyException("You must specify a valid Send-out order #");
            } catch (NumberFormatException  e) {
                throw new InconsistencyException("You must specify a valid Send-out order #");
            } catch (NotFoundException  e) {
                throw new InconsistencyException("You must specify a valid Send-out order #");
            }
        }
        
        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/requestform/order/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            tempFile = File.createTempFile("order", ".pdf", new File("/tmp"));

            jparam = new HashMap<String, Object>();
            jparam.put("ORDER_ID", orderId);
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
                copies = 1;
                if (useNumForms != null) 
                    copies = data.getNumberOfForms();     
                if (copies > 0) {
                    printstat = ReportUtil.print(tempFile, printer, copies);                 
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                }
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
                e.printStackTrace();
            }
        }

        return status;
    }
}