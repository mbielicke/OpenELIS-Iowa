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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.ClientNotificationReleasedReportLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")

public class ClientNotificationReleasedReportBean implements ClientNotificationReleasedReportLocal {

    @EJB
    private SessionCacheLocal        session;

    @EJB
    private SampleLocal              sample;

    @EJB
    private SystemVariableLocal      systemVariable;

    @EJB
    private AnalysisReportFlagsLocal analysisReportFlags;
    
    private static final Logger log = Logger.getLogger(ClientNotificationReleasedReportBean.class);

    /*
     * Execute the report and email its output to specified addresses
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        int i;
        ReportStatus status;
        Object[] result;
        ArrayList<Object[]> resultList;
        String email;
        Date rc_stDate, rc_endDate;
        SystemVariableDO data;
        Calendar cal;
        DateFormat df;
        HashMap<String, ArrayList<Object[]>> map;
        ArrayList<Object[]> l;

        resultList = null;
        map = new HashMap<String, ArrayList<Object[]>>();

        status = new ReportStatus();
        session.setAttribute("ClientNotificationReleasedReport", status);

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        rc_endDate = cal.getTime();

        try {
            data = systemVariable.fetchByName("last_released_report_run");
            rc_stDate = df.parse(data.getValue());
        } catch (Exception e) {
            log.error("System variable 'last_released_report_run' not present or invalid date-time", e);
            return;
        }

        if (rc_stDate.compareTo(rc_endDate) >= 0) {
            log.error("Start Date should be earlier than End Date");
            return;
        }
        
        resultList = sample.fetchForClientEmailReleasedReport(rc_stDate, rc_endDate);
        
        log.debug("Considering "+ resultList.size()+ " cases to run");        
        if (resultList.size() == 0)
            return;

        for (i = 0; i < resultList.size(); i++ ) {
            result = resultList.get(i);
            email = (String)result[5];
            l = map.get(email);
            if (l == null)
                l = new ArrayList<Object[]>();
            l.add(result);
            map.put(email, l);
        }
        generateEmailsFromList(map);
    }
    
    protected ReportStatus generateEmailsFromList(HashMap<String, ArrayList<Object[]>> map) throws Exception {
        int i,j;
        ReportStatus status;
        Object[] result;
        Integer samId;
        String email,from, to, rcvdDate, col_dt, ref, proj;
        String text;
        StringBuilder contents;
        Timestamp rc_date, col_date_time, col_date, col_time;
        Time cl_time;
        SystemVariableDO data;
        DateFormat df, df1;
        ArrayList<Object[]> l;
        ArrayList<Integer> sampleIds;
        ArrayList<AnalysisReportFlagsDO> anaList;
        AnalysisReportFlagsDO anaData;

        rcvdDate = col_dt = null;
        contents = null;
        to = null;
        text = null;
        sampleIds = new ArrayList<Integer>();
        
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
                samId = (Integer)result[1];
                col_date = (Timestamp)result[2];
                cl_time = (Time)result[3];
                rc_date = (Timestamp)result[4];
                to = (String)result[5];                
                rcvdDate = df.format(rc_date);
               
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
                if (result[10] != null)
                    proj = result[10].toString();               
                ref = getReferenceFields((String)result[7], (String)result[8], (String)result[9], proj);               
                
                printBody(contents, samId, rcvdDate, col_dt, ref);

                col_dt = null;
                rcvdDate = null;
                sampleIds.add(samId);
            }
            printFooter(contents);
            text = contents.toString();
            
            from = ReportUtil.getSystemVariableValue("do_not_reply_email_address");                
            ReportUtil.sendEmail(from, to, 
                                     "Your Results are available from the State Hygienic Laboratory at the University of Iowa", text);
            //sendemail(rcvd_email, text);                           
            contents = null;
        }
        
        anaList = analysisReportFlags.fetchBySampleAccessionNumbers(sampleIds);
        for (int k = 0; k < anaList.size(); k++ ) {
            anaData = anaList.get(k);
            analysisReportFlags.fetchForUpdateByAnalysisId(anaData.getAnalysisId());
            anaData.setNotifiedReleased("Y");
            analysisReportFlags.update(anaData);
        }

        status.setMessage("emailed").setStatus(ReportStatus.Status.PRINTED);
        /*
         * update system variable last_released_report_run with current date
         * and time.
         */
        data = systemVariable.fetchForUpdateByName("last_released_report_run");
        data.setValue(df.format(new Date()));
        systemVariable.updateAsSystem(data);
        
        status.setStatus(ReportStatus.Status.SAVED);
        return status;
    
    }

    protected void printHeader(StringBuilder con) {
        con.append("\n")
           .append("We would like to thank you for using our services. The following sample(s) reports or new results are available:")
           .append("<br><br>\r\n")
           .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
           .append("    <tr><td>SHL Accession No.</td>")
           .append("<td>Date Collected</td>")
           .append("<td>Date Received</td>")
           .append("<td>Reference Information</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String dateReceived,
                             String dateCollected, String refInfo) {
        body.append("    <tr><td>").append(accNum).append("</td>").append("<td>");

        if (dateCollected != null)
            body.append(dateCollected);

        body.append("</td>").append("<td>");

        if (dateReceived != null)
            body.append(dateReceived);

        body.append("</td>").append("<td>");

        if (refInfo != null && refInfo.length() > 0 && !"null".equals(refInfo))
            body.append(refInfo);

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