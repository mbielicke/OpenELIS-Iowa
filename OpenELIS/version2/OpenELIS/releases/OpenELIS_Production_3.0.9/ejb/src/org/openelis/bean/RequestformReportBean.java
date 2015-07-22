package org.openelis.bean;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

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
import org.openelis.domain.OrderViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
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
public class RequestformReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private OrderBean           order;

    @EJB
    private PrinterCacheBean    printers;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("ORDER_ID", Prompt.Type.INTEGER).setPrompt("Order #:")
                                                            .setWidth(100)
                                                            .setRequired(true));

            p.add(new Prompt("USE_NUM_FORMS", Prompt.Type.CHECK).setPrompt("Use # of forms:")
                                                                .setDefaultValue("N")
                                                                .setHidden(true));

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
        int copies;
        boolean useNumForms;
        Integer orderId;
        String printer, dir, printstat;
        URL url;
        Path path;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
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

        orderId = ReportUtil.getIntegerParameter(param, "ORDER_ID");
        printer = ReportUtil.getStringParameter(param, "PRINTER");
        useNumForms = ReportUtil.getBooleanParameter(param, "USE_NUM_FORMS");
        if (orderId == null || DataBaseUtil.isEmpty(printer)) {
            throw new InconsistencyException("You must specify the order # and printer for this report");
        } else {
            try {
                data = order.fetchById(orderId);
                if ( !"S".equals(data.getType()))
                    throw new InconsistencyException("You must specify a valid Send-out order #");
            } catch (NumberFormatException e) {
                throw new InconsistencyException("You must specify a valid Send-out order #");
            } catch (NotFoundException e) {
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

            jparam = new HashMap<String, Object>();
            jparam.put("ORDER_ID", orderId);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Outputing report").setPercentComplete(20);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            if (ReportUtil.isPrinter(printer))
                path = export(jprint, null);
            else
                path = export(jprint, "upload_stream_directory");

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                copies = 1;
                if (useNumForms)
                    copies = data.getNumberOfForms();
                if (copies > 0) {
                    printstat = ReportUtil.print(path, User.getName(ctx), printer, copies, true);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                }
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
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("order", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close outout stream for order request form report");
            }
        }

        return path;
    }
}