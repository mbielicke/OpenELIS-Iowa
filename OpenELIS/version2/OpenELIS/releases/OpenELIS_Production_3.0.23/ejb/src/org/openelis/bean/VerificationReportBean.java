package org.openelis.bean;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
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
import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class VerificationReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private PrinterCacheBean    printers;

    @EJB
    private UserCacheBean       userCache;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("USER_LIST", Prompt.Type.ARRAY).setPrompt("User ID:")
                                                            .setWidth(200)
                                                            .setOptionList(getUsers())
                                                            .setMultiSelect(true));

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
        URL url;
        Path path;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        String userIds, userWhere, userNames, printer, dir, printstat, userName, token;
        StringTokenizer tokenizer;
        SystemUserVO sysUserVO;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("VerificationReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        userName = User.getName(ctx);
        userWhere = ReportUtil.getListParameter(param, "USER_LIST");
        printer = ReportUtil.getStringParameter(param, "PRINTER");

        if (!DataBaseUtil.isEmpty(userWhere)) {
            userNames = "";
            if (userWhere.startsWith("in (")) {
                userIds = userWhere.substring(4, userWhere.length() - 1);
                tokenizer = new StringTokenizer(userIds, ",");
                try {
                    while (tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        sysUserVO = userCache.getSystemUser(Integer.valueOf(token));
                        if (userNames.length() > 0)
                            userNames += ", ";
                        userNames += sysUserVO.getLoginName();
                    }
                } catch (Exception e) {
                    userNames = "ERROR LOADING NAMES";
                    e.printStackTrace();
                }
            } else if (userWhere.startsWith("= ")) {
                userIds = userWhere.substring(2);
                try {
                    sysUserVO = userCache.getSystemUser(Integer.valueOf(userIds));
                    userNames = sysUserVO.getLoginName();
                } catch (Exception e) {
                    userNames = "ERROR LOADING NAMES";
                    e.printStackTrace();
                }
            }
            userWhere = " and h.system_user_id " + userWhere;
        } else {
            userNames = userName;
            userWhere = " and h.system_user_id = " + userCache.getId();
        }
        
        /*
         * If the user does not have permission to view patient date, exclude samples
         * from the Clinical and Neonatal domains.
         */
        try {
            userCache.applyPermission("patient", ModuleFlags.SELECT);
        } catch (PermissionException permE) {
            userWhere += " and s.domain not in ('" + Constants.domain().CLINICAL +
                         "','" + Constants.domain().NEONATAL + "')";
        }

        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/verification/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("USER_WHERE", userWhere);
            jparam.put("USER_NAMES", userNames);
            jparam.put("USER_NAME", userName);
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
                printstat = ReportUtil.print(path,
                                             userName,
                                             printer,
                                             1,
                                             true,
                                             "Duplex=DuplexNoTumble");
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

    private ArrayList<OptionListItem> getUsers() {
        ArrayList<SystemUserVO> s;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            s = userCache.getEmployees("%", 500);
            for (SystemUserVO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getLoginName()));
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
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("verification", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for verification report");
            }
        }
        return path;
    }
}