package org.openelis.bean;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.remote.ClientNotificationReceivedReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.JasperUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class ClientNotificationReceivedReportBean implements ClientNotificationReceivedReportRemote {
    @EJB
    private SessionCacheLocal        session;

    @EJB
    private SampleLocal              sampleBean;

    @EJB
    private SystemVariableLocal      sysvarBean;

    @EJB
    private AnalysisReportFlagsLocal anaReportFlagsBean;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;
        try {
            p = new ArrayList<Prompt>();
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and email its output to specified addresses
     */
    public ReportStatus runReport() throws Exception {
        ReportStatus status;
        Object[] result;
        ArrayList<Object[]> resultList;
        Integer samId, qaId, lastAccNum, currentQaId;
        String email;
        Date rc_stDate, rc_endDate;
        SystemVariableDO data;
        Calendar cal;
        DateFormat df;
        HashMap<String, ArrayList<Object[]>> map;
        HashMap<Integer, Integer> sMap;
        ArrayList<Object[]> l;

        samId = null;
        resultList = null;

        map = new HashMap<String, ArrayList<Object[]>>();
        sMap = new HashMap<Integer, Integer>();
        lastAccNum = -1;
        status = new ReportStatus();
        session.setAttribute("ClientNotificationReceivedReport", status);

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal = Calendar.getInstance();
        /*
         * subtracting 1 minute from the current time for end date
         */
        cal.add(Calendar.MINUTE, -1);
        rc_endDate = cal.getTime();

        try {
            data = sysvarBean.fetchByName("last_receivable_report_run");
            rc_stDate = df.parse(data.getValue());
        } catch (Exception e) {
            throw e;
        }

        if (rc_stDate.compareTo(rc_endDate) < 0)
            resultList = sampleBean.fetchForClientEmailReceivedReport(rc_stDate, rc_endDate);
        else
            throw new NotFoundException("Start Date should be earlier than End Date");
        for (int i = 0; i < resultList.size(); i++ ) {
            result = resultList.get(i);
            email = (String)result[6];
            samId = (Integer)result[2];
            qaId = (Integer)result[7];
            if (map.get(email) == null) {
                l = new ArrayList<Object[]>();
                l.add(result);
                map.put(email, l);
                sMap.put(samId, qaId);
                lastAccNum = samId;
            } else {
                if (samId > lastAccNum) {
                    l = map.get(email);
                    l.add(result);
                    map.put(email, l);
                    sMap.put(samId, qaId);
                    lastAccNum = samId;
                } else {
                    currentQaId = sMap.get(samId);
                    if (currentQaId != null)
                        continue;
                    else if (qaId != null)
                        sMap.put(samId, qaId);
                }
            }
        }
        return generateEmailsFromList(map, sMap);
    }

    protected ReportStatus generateEmailsFromList(HashMap<String, ArrayList<Object[]>> map,
                                                  HashMap<Integer, Integer> sMap) throws Exception {

        int i, j;
        ReportStatus status;
        Object[] result;
        Integer samId, qaId;
        String email, rcvd_email, rcvd_date, col_dt, domain, ref_f1, ref_f2, ref_f3, ref, qaOverride;
        String text;
        StringBuilder contents;
        Timestamp rc_date, col_date_time, col_date, col_time;
        Time cl_time;
        SystemVariableDO data;
        DateFormat df, df1;
        ArrayList<Object[]> l;
        ArrayList<Integer> sampleId;
        ArrayList<AnalysisReportFlagsDO> anaList;
        AnalysisReportFlagsDO anaData;

        rcvd_date = col_dt = ref = qaOverride = null;
        samId = null;
        contents = null;
        rcvd_email = null;
        text = null;
        sampleId = new ArrayList<Integer>();
        status = new ReportStatus();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df1 = new SimpleDateFormat("yyyy-MM-dd");
        
        for (i = 0; i < map.size(); i++ ) {
            email = (String) (map.keySet().toArray())[i];
            contents = new StringBuilder();
            printHeader(contents);
            l = map.get(email);
            for (j = 0; j < l.size(); j++ ) {
                result = (Object[])l.get(j);
                samId = (Integer)result[2];
                col_date = (Timestamp)result[3];
                cl_time = (Time)result[4];
                rc_date = (Timestamp)result[5];
                rcvd_email = (String)result[6];
                domain = Character.toString((Character)result[8]);
                rcvd_date = df.format(rc_date);
                ref_f1 = (String)result[9];
                ref_f2 = (String)result[10];
                ref_f3 = (String)result[11];

                if (col_date != null) {
                    if (cl_time != null) {
                        col_time = new Timestamp(cl_time.getTime());
                        col_date_time = JasperUtil.concatDateAndTime(col_date, col_time);
                        col_dt = df.format(col_date_time);
                    } else {
                        col_date_time = col_date;
                        col_dt = df1.format(col_date_time);
                    }
                }

                ref = getReferenceFields(domain, ref_f1, ref_f2, ref_f3);
                qaId = sMap.get(samId);
                if (qaId != null)
                    qaOverride = "YES";
                else
                    qaOverride = null;
                printBody(contents, samId, rcvd_date, col_dt, ref, qaOverride);

                col_dt = null;
                rcvd_date = null;
                sampleId.add(samId);
            }
            printFooter(contents);
            text = contents.toString();
            try {
                sendemail(rcvd_email, text);
                anaList = anaReportFlagsBean.fetchForUpdateBySampleAccessionNumbers(sampleId);
                for (int k = 0; k < anaList.size(); k++ ) {
                    anaData = anaList.get(k);
                    anaData.setNotifiedReceived("Y");
                    anaReportFlagsBean.update(anaData);
                    anaReportFlagsBean.abortUpdate(anaData.getAnalysisId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            contents = null;
        }
        status.setMessage("emailed").setStatus(ReportStatus.Status.PRINTED);
        try {
            data = sysvarBean.fetchForUpdateByName("last_receivable_report_run");
            data.setValue(df.format(new Date()));
            sysvarBean.updateAsSystem(data);
        } catch (Exception e) {
            throw e;
        }
        status.setStatus(ReportStatus.Status.SAVED);
        return status;
    }

    protected void printHeader(StringBuilder body) {
        body.append("\r\n")
            .append("We would like to thank you for using our services. The following sample(s) have been received:\r\n")
            .append("<br><br>\r\n")
            .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
            .append("    <tr><td>SHL Accession No.</td>")
            .append("<td>Date Collected</td>")
            .append("<td>Date Received</td>")
            .append("<td>Reference Information</td>")
            .append("<td>Sample Receipt Issues</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String dateReceived,
                             String dateCollected, String refInfo, String qaOverride) {
        body.append("    <tr><td>").append(accNum).append("</td>").append("<td>");

        if (dateCollected != null)
            body.append(dateCollected);

        body.append("</td>").append("<td>");

        if (dateReceived != null)
            body.append(dateReceived);

        body.append("</td>").append("<td>");

        if (refInfo != null && refInfo.length() > 0 && !"null".equals(refInfo))
            body.append(refInfo);

        body.append("</td>").append("<td>");

        if (qaOverride != null && qaOverride.length() > 0 && !"null".equals(qaOverride))
            body.append(qaOverride);

        body.append("</td></tr>\r\n");
    }

    protected void printFooter(StringBuilder body) {
        body.append("</table>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>This report is intended to provide sample information status at the time of generation. If you have web access, please check for the most current sample results at: <a href='http://www.shl.uiowa.edu/results/index.html'>OpenElis Web Access</a></li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>For questions, please contact us at <a href='mailto:ask-shl@uiowa.edu'>ask-shl@uiowa.edu</a> or call 319-335-4500/1-800-421-IOWA (4692).</li></ul>\r\n")
            .append("<br>\r\n")
            .append("<b>Additional Information:</b>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>This mail was sent from an automated e-mail server. Please do not reply to this message.</li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>You are receiving this courtesy e-mail because you are a valued SHL customer. If you prefer not to receive this e-mail, you can unsubscribe by updating your e-mail preferences at <a href='http://www.shl.uiowa.edu/results/index.html'>OpenElis Web Access</a> or call 319-335-4358.</li>\r\n")
            .append("    <br>\r\n")
            .append("    <li><div style='color:#006400'>Save the Environment, Go GREEN. For Paperless result delivery, please call 319-335-4358.</div></li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>For information on available clinical, environmental, newborn screening, research and training services offered by the State Hygienic Laboratory, please visit our website at <a href='http://www.shl.uiowa.edu'>www.shl.uiowa.edu</a>.</li></ul>\r\n")
            .append("<br>\r\n")
            .append("<b>Disclaimer:</b>\r\n")
            .append("<br><br>\r\n")
            .append("<i>This email and any files transmitted with it are confidential and intended solely for the use of the individual or entity to whom they are addressed. If you have received this email in error please notify the system manager. This message contains confidential information and is intended only for the individual named. If you are not the named addressee you should not disseminate, distribute or copy this e-mail. Please notify <a href='mailto:ask-shl@uiowa.edu'>ask-shl@uiowa.edu</a> immediately by e-mail if you have received this e-mail by mistake and delete this e-mail from your system. If you are not the intended recipient you are notified that disclosing, copying, distributing or taking any action in reliance on the contents of this information is strictly prohibited.</i>");
    }

    protected void sendemail(String to_email, String body) throws AddressException,
                    MessagingException {
        Message msg;
        Properties props;
        Session session;

        props = new Properties();
        props.put("mail.smtp.host", "mailserver.uhl.uiowa.edu");
        props.put("mail.smtp.port", "25");
        session = Session.getDefaultInstance(props, null);

        msg = new MimeMessage(session);
        msg.setContent(body, "text/html; charset=ISO-8859-1");
        msg.setFrom(new InternetAddress("do-not-reply@shl.uiowa.edu"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
        msg.setSubject("Samples Received by the State Hygienic Laboratory");
        msg.saveChanges();

        try {
            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    protected String getReferenceFields(String domain, String field1, String field2, String field3) {
        String project, cl_ref, col_site, owner, collector, ref;
        ref = null;
        if (domain.equals("E")) {
            cl_ref = field1;
            col_site = field2;
            project = field3;
            if ( (project == null) && (cl_ref == null) && (col_site == null))
                ref = null;
            else if ( (project != null) && (cl_ref != null) && (col_site != null))
                ref = project + ", " + cl_ref + ", " + col_site;
            else if ( (project != null) && (cl_ref != null) && (col_site == null))
                ref = project + ", " + cl_ref;
            else if ( (project != null) && (cl_ref == null) && (col_site == null))
                ref = project;
            else if ( (project == null) && (cl_ref != null) && (col_site != null))
                ref = cl_ref + ", " + col_site;
            else if ( (project == null) && (cl_ref != null) && (col_site == null))
                ref = cl_ref;
            else if ( (project == null) && (cl_ref == null) && (col_site != null))
                ref = col_site;
            else if ( (project == null) && (cl_ref == null) && (col_site != null))
                ref = project + ", " + col_site;

        } else {
            owner = field1;
            col_site = field2;
            collector = field3;
            if ( (collector == null) && (owner == null) && (col_site == null))
                ref = null;
            else if ( (collector != null) && (owner != null) && (col_site != null))
                ref = collector + ", " + owner + ", " + col_site;
            else if ( (collector != null) && (owner != null) && (col_site == null))
                ref = collector + ", " + owner;
            else if ( (collector != null) && (owner == null) && (col_site == null))
                ref = collector;
            else if ( (collector == null) && (owner != null) && (col_site == null))
                ref = owner + ", " + col_site;
            else if ( (collector == null) && (owner != null) && (col_site == null))
                ref = owner;
            else if ( (collector == null) && (owner == null) && (col_site != null))
                ref = col_site;
            else if ( (collector != null) && (owner == null) && (col_site != null))
                ref = collector + ", " + col_site;
        }
        return ref;
    }
}
