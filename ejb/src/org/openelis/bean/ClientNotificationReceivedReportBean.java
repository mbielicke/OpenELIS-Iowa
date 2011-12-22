package org.openelis.bean;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.remote.ClientNotificationReceivedReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")

public class ClientNotificationReceivedReportBean implements ClientNotificationReceivedReportRemote {
    @EJB
    private SessionCacheLocal        session;

    @EJB
    private SampleLocal              sample;

    @EJB
    private SystemVariableLocal      systemVariable;

    @EJB
    private AnalysisReportFlagsLocal analysisReportFlags;

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
        Integer accNum, qaId, lastAccNum;
        String email;
        Date rc_stDate, rc_endDate;
        SystemVariableDO data;
        Calendar cal;
        DateFormat df;
        HashMap<String, ArrayList<Object[]>> map;
        HashMap<Integer, Boolean> sMap;
        ArrayList<Object[]> l;

        accNum = null;
        resultList = null;

        map = new HashMap<String, ArrayList<Object[]>>();
        sMap = new HashMap<Integer, Boolean>();
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
            data = systemVariable.fetchByName("last_receivable_report_run");
            rc_stDate = df.parse(data.getValue());
        } catch (Exception e) {
            throw e;
        }

        if (rc_stDate.compareTo(rc_endDate) < 0)
            resultList = sample.fetchForClientEmailReceivedReport(rc_stDate, rc_endDate);
        else
            throw new NotFoundException("Start Date should be earlier than End Date");
        
        lastAccNum = -1;
        for (int i = 0; i < resultList.size(); i++ ) {
            result = resultList.get(i);
            email = (String)result[6];
            accNum = (Integer)result[2];
            qaId = (Integer)result[7];
            l = map.get(email);
            if (l == null) {
                l = new ArrayList<Object[]>();
                map.put(email, l);
                lastAccNum = -1;
            }
            if (accNum > lastAccNum)
                l.add(result);
            lastAccNum = accNum;

            if (qaId != null) 
                sMap.put(accNum, Boolean.TRUE);  
            else 
                sMap.put(accNum, Boolean.FALSE);  

        }
        return generateEmailsFromList(map, sMap);
    }

    protected ReportStatus generateEmailsFromList(HashMap<String, ArrayList<Object[]>> map,
                                                  HashMap<Integer, Boolean> sMap) throws Exception {

        int i, j;
        ReportStatus status;
        Object[] result;
        Integer samId;
        Boolean hasOverride;
        String email, to, rcvd_date, col_dt, ref, qaOverride, proj, text;
        StringBuilder contents;
        Timestamp rc_date, col_date_time, col_date, col_time;
        Time cl_time;
        SystemVariableDO data;
        DateFormat df, df1;
        ArrayList<Object[]> l;
        ArrayList<Integer> sampleId;
        ArrayList<AnalysisReportFlagsDO> anaList;
        AnalysisReportFlagsDO anaData;

        col_dt = null;
        to = null;
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
                to = (String)result[6];
                rcvd_date = df.format(rc_date);

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
                proj = null;
                if (result[12] != null)
                    proj = result[12].toString();
                ref = getReferenceFields((String)result[9], (String)result[10], (String)result[11], proj);
                
                hasOverride = sMap.get(samId);
                qaOverride = hasOverride ? "YES" : null;
                printBody(contents, samId, rcvd_date, col_dt, ref, qaOverride);

                col_dt = null;
                rcvd_date = null;
                sampleId.add(samId);
            }
            printFooter(contents);
            text = contents.toString();
            try {
                ReportUtil.sendEmail("do-not-reply@shl.uiowa.edu", to, 
                                     "Samples Received by the State Hygienic Laboratory", text);
                //sendemail(rcvd_email, text);
                anaList = analysisReportFlags.fetchForUpdateBySampleAccessionNumbers(sampleId);
                for (int k = 0; k < anaList.size(); k++ ) {
                    anaData = anaList.get(k);
                    anaData.setNotifiedReceived("Y");
                    analysisReportFlags.update(anaData);
                    analysisReportFlags.abortUpdate(anaData.getAnalysisId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            contents = null;
        }
        status.setMessage("emailed").setStatus(ReportStatus.Status.PRINTED);
        try {
            data = systemVariable.fetchForUpdateByName("last_receivable_report_run");
            data.setValue(df.format(new Date()));
            systemVariable.updateAsSystem(data);
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
    
    protected String getReferenceFields(String... f) {
        StringBuffer b;

        b = new StringBuffer();
        for (int i = 0; i < f.length; i++ ) {
            if (!DataBaseUtil.isEmpty(f[i])) {
                if (b.length() > 0) 
                    b.append(", ");
                b.append(f[i].trim());
            }                                       
        }

        return b.toString();
    }
}