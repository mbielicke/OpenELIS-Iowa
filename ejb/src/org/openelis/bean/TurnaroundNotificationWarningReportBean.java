package org.openelis.bean;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SectionParameterDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class TurnaroundNotificationWarningReportBean {
    @EJB
    private SampleBean           sample;

    @EJB
    DictionaryCacheBean          dictionaryCache;

    @EJB
    private SectionParameterBean sectParamBean;

    private static final Logger  log = Logger.getLogger("openelis");

    private Integer               sectParamTypeId;

    @PostConstruct
    public void init() {
        try {
            sectParamTypeId = dictionaryCache.getBySystemName("section_ta_warn").getId();
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
        }
    }

    /*
     * Execute the report and email its output to specified addresses
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        ArrayList<Object[]> resultList;

        resultList = sample.fetchForTurnaroundWarningReport();
        log.fine("Considering " + resultList.size() + " cases to run");
        if (resultList.size() > 0) {
            try {
                generateEmail(resultList);
            } catch (Exception anyE) {
                log.log(Level.SEVERE, "Could not generate email(s)", anyE);
            }
        }
    }

    protected void generateEmail(ArrayList<Object[]> resultList) throws Exception {
        int i;
        ArrayList<SectionParameterDO> emailList;
        Datetime date, now;
        Integer accession, prevSecId, currSecId, holdingTime, warningTime;
        Float daysElapsed;
        SimpleDateFormat yToD, yToM;
        String colString, expireString, recString, toEmail, test, method, orgName, sectionName, prevSectionName, anaStatus;
        StringBuilder contents;
        Timestamp availableDate, colDate, expireDate, nowDateTime, recDate;
        Time colTime;

        prevSecId = null;
        prevSectionName = null;
        contents = new StringBuilder();
        now = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date());
        nowDateTime = new Timestamp(now.getDate().getTime());
        yToD = new SimpleDateFormat("yyyy-MM-dd");
        yToM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        toEmail = "";
        contents.setLength(0);
        for (Object[] result : resultList) {
            accession = (Integer)result[0];
            colDate = (Timestamp)result[1];
            colTime = (Time)result[2];
            recDate = (Timestamp)result[3];
            orgName = (String)result[4];
            test = (String)result[5];
            warningTime = (Integer)result[6];
            holdingTime = (Integer)result[7];
            method = (String)result[8];
            currSecId = (Integer)result[9];
            sectionName = (String)result[10];
            anaStatus = (String)result[11];
            availableDate = (Timestamp)result[12];
            
            date = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(availableDate.getTime()));
            if (date.add(warningTime).compareTo(now) > 0)
                continue;

            colString = "";
            if (colDate != null) {
                colString = yToD.format(colDate);
                if (colTime != null)
                    colString = yToM.format(JasperUtil.concatDateAndTime(colDate, colTime));
            }

            recString = "";
            if (recDate != null)
                recString = yToM.format(recDate);

            expireDate = JasperUtil.changeDate(availableDate, holdingTime, Calendar.HOUR);
            expireString = "";
            if (expireDate != null)
                expireString = yToM.format(expireDate);

            daysElapsed = JasperUtil.daysAndHours(JasperUtil.delta_hours(availableDate, nowDateTime));

            if (prevSecId != currSecId) {
                if (prevSecId != null) {
                    printFooter(contents);
                    sendEmail(toEmail, "Turnaround Warning Notification: "+prevSectionName, contents.toString());
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
                    log.fine("No Turnaround Warning Email Address(es) for Section ("+sectionName+").");
                    continue;
                }
                printHeader(contents);
                prevSecId = currSecId;
                prevSectionName = sectionName;
            }
            printBody(contents, accession, test, method, anaStatus, orgName, colString, recString, daysElapsed, expireString);
        }
        if (resultList.size() > 0 && contents.length() > 0) {
            printFooter(contents);
            sendEmail(toEmail, "Turnaround Warning Notification: "+prevSectionName, contents.toString());
        }
    }

    protected void printHeader(StringBuilder body) {
        body.append("\r\n")
            .append("The following sample(s) have exceeded warning TAT from date available:\r\n")
            .append("<br><br>\r\n")
            .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
            .append("    <tr><td>Accession No.</td>")
            .append("<td>Test</td>")
            .append("<td>Method</td>")
            .append("<td>Status</td>")
            .append("<td>Organization</td>")
            .append("<td>Collected</td>")
            .append("<td>Received</td>")
            .append("<td>Days Elapsed</td>")
            .append("<td>Expires</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String test, String method,
                             String anaStatus, String orgName, String colDate, String recDate,
                             Float daysElapsed, String expireDate) {
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
            .append(colDate)
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
            .append("<ul><li>Days Elapsed values are in days and hours. i.e., 4.09 is 4 days and 9 hours</li></ul>\r\n");
    }

    protected void sendEmail(String toEmail, String subject, String body) {
        try {
            ReportUtil.sendEmail("do-not-reply@shl.uiowa.edu", toEmail, subject, body);
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }
    }
}