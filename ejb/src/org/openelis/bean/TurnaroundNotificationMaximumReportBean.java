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
import org.openelis.domain.OptionListItem;
import org.openelis.domain.SectionParameterDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SectionParameterLocal;
import org.openelis.local.TurnaroundNotificationMaximumReportLocal;
import org.openelis.report.Prompt;
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

    @EJB
    private PrinterCacheLocal     printers;

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

    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            prn = printers.getListByType("pdf");
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
        Integer accession, prevSecId, currSecId, holdingTime;
        Float daysElapsed;
        String toEmail, test, method;
        StringBuilder contents;
        Timestamp colDate, colTime, recDate, checkDateTime, expireDate, nowDateTime;
        Datetime date, now;
        SimpleDateFormat sdf;
        String expiredDate;
        ArrayList<SectionParameterDO> emailList;

        prevSecId = null;
        contents = new StringBuilder();
        now = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date());
        nowDateTime = new Timestamp(now.getDate().getTime());
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        toEmail = "";
        contents.setLength(0);
        for (Object[] result : resultList) {
            currSecId = (Integer)result[3];
            accession = (Integer)result[0];
            test = (String)result[1];
            method = (String)result[2];
            colDate = (Timestamp)result[4];
            colTime = (Timestamp)result[5];
            recDate = (Timestamp)result[6];
            holdingTime = (Integer)result[8];
            checkDateTime = getDate(colDate, colTime, recDate);
            daysElapsed = JasperUtil.daysAndHours(JasperUtil.delta_hours(checkDateTime, nowDateTime));
            expireDate = JasperUtil.changeDate(checkDateTime, holdingTime, Calendar.HOUR);
            try {
                expiredDate = sdf.format(expireDate);
            } catch (Exception e) {
                throw e;
            }
            date = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(checkDateTime.getTime()));
            if ( (date.add((Integer)result[7])).compareTo(now) > 0)
                continue;

            if (prevSecId != currSecId) {
                if (prevSecId != null) {
                    printFooter(contents);
                    sendEmail(toEmail, "Turnaround Maximum Violation", contents.toString());
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
                    log.warn("No Turnaround Max Email Address(es) for Section (" + currSecId + ").");
                    continue;
                }
                printHeader(contents);
                prevSecId = currSecId;
            }
            printBody(contents, accession, test, method, daysElapsed, expiredDate);
        }
        if (resultList.size() > 0 && contents.length() > 0) {
            printFooter(contents);
            sendEmail(toEmail, "Turnaround Maximum Violation", contents.toString());
        }
    }

    private Timestamp getDate(Timestamp colDate, Timestamp colTime, Timestamp recDate) throws Exception {
        Timestamp checkDateTime;
        SimpleDateFormat sdf;
        Date time;
        long timeInMinSec;

        sdf = new SimpleDateFormat("hh:mm");
        try {
            time = sdf.parse("00:00");
        } catch (Exception e) {
            throw e;
        }
        timeInMinSec = time.getTime();

        if (colDate != null) {
            if (colTime != null)
                checkDateTime = JasperUtil.concatDateAndTime(colDate, colTime);
            else
                checkDateTime = JasperUtil.concatDateAndTime(colDate, new Timestamp(timeInMinSec));
        } else {
            checkDateTime = recDate;
        }

        return checkDateTime;
    }

    protected void printHeader(StringBuilder body) {
        body.append("\r\n")
            .append("The following sample(s) have gone past the maximum turnaround time:\r\n")
            .append("<br><br>\r\n")
            .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
            .append("    <tr><td>Accession No.</td>")
            .append("<td>Test</td>")
            .append("<td>Method</td>")
            .append("<td>Days Elapsed</td>")
            .append("<td>Expires</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String test, String method,
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
            .append("    <br>\r\n")
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