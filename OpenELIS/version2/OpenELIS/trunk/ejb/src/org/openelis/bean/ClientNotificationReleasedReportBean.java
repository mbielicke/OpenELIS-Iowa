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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.ClientNotificationVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.ClientNotificationReleasedReportLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class ClientNotificationReleasedReportBean implements ClientNotificationReleasedReportLocal {
    @EJB
    private SampleLocal              sample;

    @EJB
    private SystemVariableLocal      systemVariable;

    @EJB
    private AnalysisReportFlagsLocal analysisReportFlags;

    private static final Logger      log = Logger.getLogger(ClientNotificationReleasedReportBean.class);

    /*
     * Execute the report and email its output to specified addresses
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        String from;
        Date lastRunDate, currentRunDate, now;
        SystemVariableDO lastRun;
        Calendar cal;
        DateFormat df;
        ArrayList<ClientNotificationVO> resultList;

        /*
         * subtracting 1 minute from the current time for end date
         */
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal = Calendar.getInstance();
        now = cal.getTime();
        cal.add(Calendar.MINUTE, -1);
        currentRunDate = cal.getTime();

        lastRun = null;
        try {
            lastRun = systemVariable.fetchForUpdateByName("last_released_report_run");
            log.debug("last_receivable_report_run = " + lastRun);
            lastRunDate = df.parse(lastRun.getValue());
            if (lastRunDate.compareTo(currentRunDate) >= 0)
                throw new InconsistencyException("Start Date should be earlier than End Date");

            from = ReportUtil.getSystemVariableValue("do_not_reply_email_address");
            if (from == null)
                throw new InconsistencyException("System variable 'do_not_reply_email_address' not present.");
            
            resultList = sample.fetchForClientEmailReleasedReport(lastRunDate, currentRunDate);
            log.debug("Considering " + resultList.size() + " cases to run");

            if (resultList.size() > 0)
                generateEmail(resultList, from);

            lastRun.setValue(df.format(now));
            systemVariable.updateAsSystem(lastRun);
        } catch (Exception e) {
            log.error(e);
            if (lastRun != null)
                systemVariable.abortUpdate(lastRun.getId());
            return;
        }
    }

    protected void generateEmail(ArrayList<ClientNotificationVO> resultList, String from) throws Exception {
        Integer accession, lastAccession, sampleId;
        String email, lastEmail, to, collectedDt, ref;
        StringBuilder contents;
        ArrayList<ClientNotificationVO> l;
        ArrayList<Integer> sampleIds;
        ArrayList<AnalysisReportFlagsDO> anaList;
        HashMap<String, ArrayList<ClientNotificationVO>> emails;
        
        emails = new HashMap<String, ArrayList<ClientNotificationVO>>();

        /*
         * Group unique samples for each email address.
         */
        lastAccession = -1;
        lastEmail = "";
        for (ClientNotificationVO data : resultList) {
            accession = data.getAccessionNumber();
            email = data.getEmail();
            l = emails.get(email);
            if (l == null) {
                l = new ArrayList<ClientNotificationVO>();
                l.add(data);
                emails.put(email, l);
            } else if (accession > lastAccession || !lastEmail.equals(email)) {
                l.add(data);
            }
            lastAccession = accession;
            lastEmail = email;
        }

        collectedDt = null;
        contents = new StringBuilder();
        sampleIds = new ArrayList<Integer>();

        /*
         * Generate emails for each email address from the emails map.
         */

        for (Entry<String, ArrayList<ClientNotificationVO>> entry : emails.entrySet()) {
            contents.setLength(0);

            printHeader(contents);

            to = entry.getKey();
            l = entry.getValue();
            for (ClientNotificationVO data : l) {
                if (data.getCollectionDate() != null)
                    collectedDt = JasperUtil.concatWithSeparator(data.getCollectionDate(),
                                                                 " ",
                                                                 data.getCollectionTime());
                ref = getReferenceFields(data.getReferenceField1(),
                                         data.getReferenceField2(),
                                         data.getReferenceField3(),
                                         data.getProjectName());
                sampleId = data.getAccessionNumber();
                printBody(contents, sampleId, DataBaseUtil.toString(data.getReceivedDate()),
                                                              collectedDt, ref);

                sampleIds.add(sampleId);
            }
            printFooter(contents);
            try {
                ReportUtil.sendEmail(from,
                                     to,
                                     "Your Results are available from the State Hygienic Laboratory at the University of Iowa",
                                     contents.toString());
            } catch (Exception e) {
                log.error(e);
            }
        }

        anaList = analysisReportFlags.fetchBySampleAccessionNumbers(sampleIds);
        for (AnalysisReportFlagsDO anaData : anaList) {
            try {
                analysisReportFlags.fetchForUpdateByAnalysisId(anaData.getAnalysisId());
                anaData.setNotifiedReleased("Y");
                analysisReportFlags.update(anaData);
            } catch (EntityLockedException e) {
                log.error("Could not lock analysis report flag for id " + anaData.getAnalysisId());
            } catch (Exception e) {
                log.error("Error occurred while updating report flags " + e);
            }
        }

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
        body.append("<tr><td>")
            .append(accNum)
            .append("</td>").append("<td>")
            .append(DataBaseUtil.toString(dateCollected))
            .append("</td>").append("<td>")
            .append(DataBaseUtil.toString(dateReceived))
            .append("</td>").append("<td>")
            .append(DataBaseUtil.toString(refInfo))
            .append("</td></tr>\r\n");
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
            if ( !DataBaseUtil.isEmpty(f[i])) {
                if (b.length() > 0)
                    b.append(", ");
                b.append(f[i].trim());
            }
        }

        return b.toString();
    }
}