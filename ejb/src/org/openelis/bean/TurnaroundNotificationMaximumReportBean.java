package org.openelis.bean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.SectionParameterDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SectionParameterLocal;
import org.openelis.local.TurnaroundNotificationMaximumReportLocal;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class TurnaroundNotificationMaximumReportBean implements
                                                    TurnaroundNotificationMaximumReportLocal {
    @EJB
    private SampleLocal           sample;

    @EJB
    DictionaryCacheLocal          dictionaryCache;

    @EJB
    private SectionParameterLocal sectParamBean;

    private static final Logger   log = Logger.getLogger(TurnaroundNotificationMaximumReportBean.class);

    private Integer               sectParamTypeId;

    @PostConstruct
    public void init() {

        try {
            sectParamTypeId = dictionaryCache.getBySystemName("section_ta_max").getId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /*
     * Execute the report and email its output to specified addresses
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        ArrayList<Object[]> resultList;

        resultList = sample.fetchForTurnaroundMaximumReport();
        log.debug("Considering " + resultList.size() + " cases to run");
        if (resultList.size() > 0)
            generateEmail(resultList);
    }

    protected void generateEmail(ArrayList<Object[]> resultList) throws Exception {
        int i;
        ArrayList<SectionParameterDO> emailList;
        Datetime date, now;
        Integer accession, prevSecId, currSecId, holdingTime, maxTime;
        Float daysElapsed;
        SimpleDateFormat sdf;
        String expireString, recString, toEmail, test, method, orgName, sectionName, prevSectionName, anaStatus;
        StringBuilder contents;
        Timestamp availableDate, expireDate, nowDateTime, recDate;

        prevSecId = null;
        prevSectionName = null;
        contents = new StringBuilder();
        now = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date());
        nowDateTime = new Timestamp(now.getDate().getTime());
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        toEmail = "";
        contents.setLength(0);
        for (Object[] result : resultList) {
            accession = (Integer)result[0];
            recDate = (Timestamp)result[1];
            orgName = (String)result[2];
            test = (String)result[3];
            maxTime = (Integer)result[4];
            holdingTime = (Integer)result[5];
            method = (String)result[6];
            currSecId = (Integer)result[7];
            sectionName = (String)result[8];
            anaStatus = (String)result[9];
            availableDate = (Timestamp)result[10];
            
            date = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(availableDate.getTime()));
            if (date.add(maxTime).compareTo(now) > 0)
                continue;

            daysElapsed = JasperUtil.daysAndHours(JasperUtil.delta_hours(availableDate, nowDateTime));
            expireDate = JasperUtil.changeDate(availableDate, holdingTime, Calendar.HOUR);
            try {
                expireString = "";
                if (expireDate != null)
                    expireString = sdf.format(expireDate);
                recString = "";
                if (recDate != null)
                    recString = sdf.format(recDate);
            } catch (Exception e) {
                throw e;
            }

            if (prevSecId != currSecId) {
                if (prevSecId != null) {
                    printFooter(contents);
                    sendEmail(toEmail, "Turnaround Maximum Notification: "+prevSectionName, contents.toString());
                    toEmail = "";
                    contents.setLength(0);
                }
                try {
                    emailList = sectParamBean.fetchBySectionIdAndTypeId(currSecId, sectParamTypeId);
                    for (i = 0; i < emailList.size(); i++ ) {
                        if (toEmail.length() > 0)
                            toEmail += ",";
                        toEmail += emailList.get(i).getValue().trim();
                    }
                } catch (NotFoundException nfE) {
                    log.warn("No Turnaround Max Email Address(es) for Section ("+sectionName+").");
                    continue;
                }
                printHeader(contents);
                prevSecId = currSecId;
                prevSectionName = sectionName;
            }
            printBody(contents, accession, test, method, daysElapsed, expireString, orgName, anaStatus, recString);
        }
        if (resultList.size() > 0 && contents.length() > 0) {
            printFooter(contents);
            sendEmail(toEmail, "Turnaround Maximum Notification: "+prevSectionName, contents.toString());
        }
    }

    protected void printHeader(StringBuilder body) {
        body.append("\r\n")
            .append("The following sample(s) have gone past the maximum turnaround time:\r\n")
            .append("<br><br>\r\n")
            .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
            .append("    <tr><td>Accession No.</td>")
            .append("<td>Test</td>")
            .append("<td>Method</td>")
            .append("<td>Status</td>")
            .append("<td>Organization</td>")
            .append("<td>Received</td>")
            .append("<td>Days Elapsed</td>")
            .append("<td>Expires</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String test, String method,
                             Float daysElapsed, String expireDate, String orgName,
                             String anaStatus, String recDate) {
        body.append("<tr><td align=\"center\">")
            .append(accNum)
            .append("</td>")
            .append("<td>")
            .append(test)
            .append("</td>")
            .append("<td>")
            .append(method)
            .append("</td>")
            .append("<td>")
            .append(anaStatus)
            .append("</td>")
            .append("<td>")
            .append(orgName)
            .append("</td>")
            .append("<td>")
            .append(recDate)
            .append("</td>")
            .append("<td>")
            .append(daysElapsed)
            .append("</td>")
            .append("<td>")
            .append(expireDate)
            .append("</td></tr>\r\n");
    }

    protected void printFooter(StringBuilder body) {
        body.append("</table>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>Days Elapsed values are in days and hours. i.e., 4.09 is 4 days and 9 hours</li></ul>\r\n")
            .append("<br>\r\n")
            .append("<b>Additional Information:</b>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>This mail was sent from an automated e-mail server. Please do not reply to this message.</li>\r\n")
            .append("    <li><div style='color:#006400'>Save the Environment, Go GREEN.</div></li></ul>\r\n")
            .append("    <br>\r\n")
            .append("<b>Disclaimer:</b>\r\n")
            .append("<br><br>\r\n")
            .append("<i>This email and any files transmitted with it are confidential and intended solely for the use of the individual or entity to whom they are addressed. If you have received this email in error please notify the system manager. This message contains confidential information and is intended only for the individual named. If you are not the named addressee you should not disseminate, distribute or copy this e-mail. Please notify <a href='mailto:ask-shl@uiowa.edu'>ask-shl@uiowa.edu</a> immediately by e-mail if you have received this e-mail by mistake and delete this e-mail from your system. If you are not the intended recipient you are notified that disclosing, copying, distributing or taking any action in reliance on the contents of this information is strictly prohibited.</i>");
    }

    protected void sendEmail(String toEmail, String subject, String body) {
        try {
            ReportUtil.sendEmail("do-not-reply@shl.uiowa.edu", toEmail, subject, body);
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }
    }
}