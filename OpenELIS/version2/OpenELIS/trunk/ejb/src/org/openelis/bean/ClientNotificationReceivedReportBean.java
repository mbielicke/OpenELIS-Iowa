package org.openelis.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.OrganizationParameterBean.EmailFilter;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.ClientNotificationVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class ClientNotificationReceivedReportBean {
    @EJB
    private AnalysisReportFlagsBean   analysisReportFlags;

    @EJB
    private OrganizationParameterBean organizationParameter;

    @EJB
    private SampleBean                sample;

    @EJB
    private SystemVariableBean        systemVariable;

    private static final Logger       log = Logger.getLogger("openelis");

    /*
     * Execute the report and email its output to specified addresses
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        ArrayList<ClientNotificationVO> resultList;
        Calendar cal;
        Date currentRunDate, lastRunDate;
        String from;
        SystemVariableDO runBackDays;

        try {
            runBackDays = systemVariable.fetchByName("receivable_report_back_days");

            /*
             * subtracting runBackDays from the current time for start date
             */
            cal = Calendar.getInstance();
            currentRunDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(runBackDays.getValue()));
            lastRunDate = cal.getTime();

            from = ReportUtil.getSystemVariableValue("do_not_reply_email_address");
            if (from == null)
                throw new InconsistencyException("System variable 'do_not_reply_email_address' not present.");

            resultList = sample.fetchForClientEmailReceivedReport(lastRunDate, currentRunDate);
            log.fine("Considering " + resultList.size() + " cases to run");
            if (resultList.size() > 0)
                generateEmail(resultList, from);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not generate email(s) ", e);
        }
    }

    protected void generateEmail(ArrayList<ClientNotificationVO> resultList, String from) throws Exception {
        ArrayList<AnalysisReportFlagsDO> anaList;
        ArrayList<Integer> accessionNumbers;
        EmailFilter parsed;
        HashMap<String, EmailFilter> parsedEmails;
        Integer accession, lastAccession;
        String email, lastEmail, collectedDate, receivedDate, ref, qaOverride, filter, filterValue, emailParam;
        StringBuilder contents;

        /*
         * Group unique samples for each email address.
         */
        accessionNumbers = new ArrayList<Integer>();
        collectedDate = null;
        contents = new StringBuilder();
        parsedEmails = new HashMap<String, EmailFilter>();
        lastAccession = -1;
        lastEmail = "xyzzy";
        qaOverride = null;
        receivedDate = null;
        ref = null;
        for (ClientNotificationVO data : resultList) {
            accession = data.getAccessionNumber();
            emailParam = data.getEmail();
            if (parsedEmails.get(emailParam) == null) {
                parsed = organizationParameter.decodeEmail(emailParam);
                parsedEmails.put(emailParam, parsed);
            } else {
                parsed = parsedEmails.get(emailParam);
            }
            email = parsed.getEmail();
            filter = parsed.getFilter();
            filterValue = parsed.getfilterValue();
            if (filter != null) {
                switch (filter) {
                    case "C:":
                        /*
                         * reference field 1 should be collector
                         */
                        if (DataBaseUtil.isEmpty(data.getReferenceField1()) ||
                            !data.getReferenceField1().contains(filterValue))
                            continue;
                        break;
                    case "R:":
                        /*
                         * reference field 3 should be client reference string
                         */
                        if (DataBaseUtil.isEmpty(data.getReferenceField3()) ||
                            !data.getReferenceField3().contains(filterValue))
                            continue;
                        break;
                    case "P:":
                        // for clinical domain samples, reference field 1 should
                        // be provider last name
                        if (DataBaseUtil.isEmpty(data.getReferenceField1()) ||
                            !data.getReferenceField1().contains(filterValue))
                            continue;
                        break;
                    default:
                        // do nothing if the filter is invalid
                }
            }

            if (!lastEmail.equals(email)) {
                if (!"xyzzy".equals(lastEmail) && contents.length() > 0) {
                    printBody(contents, lastAccession, receivedDate, collectedDate, ref, qaOverride);
                    printFooter(contents);
                    try {
                        ReportUtil.sendEmail(from,
                                             lastEmail,
                                             "Samples Received by the State Hygienic Laboratory",
                                             contents.toString());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Could not send email to " + lastEmail, e);
                    }
                    contents.setLength(0);
                    lastAccession = -1;
                }
                lastEmail = email;
                printHeader(contents);
            }

            if (!lastAccession.equals(accession)) {
                if (lastAccession != -1)
                    printBody(contents, lastAccession, receivedDate, collectedDate, ref, qaOverride);

                collectedDate = null;
                if (data.getCollectionDate() != null)
                    collectedDate = DataBaseUtil.concatWithSeparator(ReportUtil.toString(data.getCollectionDate(),
                                                                                         Messages.get()
                                                                                                 .datePattern()),
                                                                     " ",
                                                                     ReportUtil.toString(data.getCollectionTime(),
                                                                                         Messages.get()
                                                                                                 .timePattern()));
                receivedDate = ReportUtil.toString(data.getReceivedDate(),
                                                   Messages.get().dateTimePattern());
                ref = getReferenceFields(data.getReferenceField1(),
                                         data.getReferenceField2(),
                                         data.getReferenceField3(),
                                         data.getProjectName());
                qaOverride = null;

                accessionNumbers.add(accession);
                lastAccession = accession;
            }
            if (data.getSampleQaeventId() != null || data.getAnalysisQaeventId() != null)
                qaOverride = "YES";
        }

        if (contents.length() > 0) {
            printBody(contents, lastAccession, receivedDate, collectedDate, ref, qaOverride);
            printFooter(contents);
            try {
                ReportUtil.sendEmail(from,
                                     lastEmail,
                                     "Samples Received by the State Hygienic Laboratory",
                                     contents.toString());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Could not send email to " + lastEmail, e);
            }
        }

        /*
         * Updating analysis with notified_received flag set to Y (meaning email
         * has been sent). If an exception occurs for updating the flag for an
         * analysis, we don't throw an exception so that the flag can be updated
         * for the remaining analyses. Email associated with the failed analysis
         * will get multiple emails.
         */
        anaList = analysisReportFlags.fetchBySampleAccessionNumbers(accessionNumbers);
        for (AnalysisReportFlagsDO anaData : anaList) {
            try {
                analysisReportFlags.fetchForUpdateByAnalysisId(anaData.getAnalysisId());
                anaData.setNotifiedReceived("Y");
                analysisReportFlags.update(anaData);
            } catch (EntityLockedException e) {
                log.severe("Could not lock analysis report flag for id " + anaData.getAnalysisId());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error occurred while updating report flags", e);
            }
        }
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
        body.append("<tr><td>")
            .append(accNum)
            .append("</td>")
            .append("<td>")
            .append(DataBaseUtil.toString(dateCollected))
            .append("</td>")
            .append("<td>")
            .append(DataBaseUtil.toString(dateReceived))
            .append("</td>")
            .append("<td>")
            .append(DataBaseUtil.toString(refInfo))
            .append("</td>")
            .append("<td>")
            .append(DataBaseUtil.toString(qaOverride))
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
        for (int i = 0; i < f.length; i++) {
            if (!DataBaseUtil.isEmpty(f[i])) {
                if (b.length() > 0)
                    b.append(", ");
                b.append(f[i].trim());
            }
        }
        return b.toString();
    }
}