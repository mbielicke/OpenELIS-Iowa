package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.remote.VerificationReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class VerificationReportBean implements VerificationReportRemote {

    @Resource
    private SessionContext  ctx;

    @EJB
    private SessionCacheLocal session;    

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

//            p.add(new Prompt("BEGIN_ENTERED", Prompt.Type.DATETIME)
//                    .setPrompt("Begin Entered:")
//                    .setWidth(120)
//                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
//                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
//                    .setDefaultValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE).toString())
//                    .setRequired(true));
//
//            p.add(new Prompt("END_ENTERED", Prompt.Type.DATETIME)
//                    .setPrompt("End Entered:")
//                    .setWidth(120)
//                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
//                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
//                    .setDefaultValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE).toString())
//                    .setRequired(true));

            p.add(new Prompt("USER_LIST", Prompt.Type.ARRAY)
                    .setPrompt("User ID:")
                    .setWidth(200)
                    .setOptionList(getUsers())
                    .setMutiSelect(true));

            prn = PrinterList.getInstance().getListByType("pdf");
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
        URL url;
        File tempFile;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        String /*beginEntered, endEntered,*/ userIds, userWhere, userNames, printer,
               dir, printstat, loginName, token;
        StringTokenizer tokenizer;
        SystemUserVO  sysUserVO;
        UserCacheLocal ucl;

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
        ucl = EJBFactory.getUserCache();
        loginName = ucl.getName();
        
//        beginEntered = ReportUtil.getSingleParameter(param, "BEGIN_ENTERED");
//        if (beginEntered != null && beginEntered.length() > 0)
//            beginEntered += ":00";
//        endEntered = ReportUtil.getSingleParameter(param, "END_ENTERED");
//        if (endEntered != null && endEntered.length() > 0)
//            endEntered += ":59";
        userWhere = ReportUtil.getListParameter(param, "USER_LIST");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
        
        if (!DataBaseUtil.isEmpty(userWhere)) {
            userNames = "";
            if (userWhere.startsWith("in (")) {
                userIds = userWhere.substring(4, userWhere.length() - 2);
                tokenizer = new StringTokenizer(userIds, ",");
                try {
                    while (tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        sysUserVO = ucl.getSystemUser(Integer.valueOf(token));
                        if (userNames.length() > 0)
                            userNames += ", ";
                        userNames += sysUserVO.getLoginName();
                    }
                } catch (Exception e) {
                    userNames = "ERROR LOADING NAMES";
                    e.printStackTrace();
                }
            } else if (userWhere.startsWith(" = ")) {
                userIds = userWhere.substring(3);
                try {
                    sysUserVO = ucl.getSystemUser(Integer.valueOf(userIds));
                    userNames = sysUserVO.getLoginName();
                } catch (Exception e) {
                    userNames = "ERROR LOADING NAMES";
                    e.printStackTrace();
                }
            }
            userWhere = " and h.system_user_id " + userWhere;
        } else {
            userNames = loginName;
            userWhere = " and h.system_user_id = " + ucl.getId();
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

            tempFile = File.createTempFile("verification", ".pdf", new File("/tmp"));

            jparam = new HashMap<String, Object>();
//            jparam.put("BEGIN_ENTERED", beginEntered);
//            jparam.put("END_ENTERED", endEntered);
            jparam.put("USER_WHERE", userWhere);
            jparam.put("USER_NAMES", userNames);
            jparam.put("LOGIN_NAME", loginName);
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
            s = EJBFactory.getUserCache().getSystemUsers("%", 500);
            for (SystemUserVO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getLoginName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }
}
