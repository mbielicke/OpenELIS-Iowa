package org.openelis.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SectionParameterDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class TurnaroundWarningNotificationBean {
    @EJB
    private AnalysisBean         analysis;

    @EJB
    DictionaryCacheBean          dictionaryCache;

    @EJB
    private SampleManager1Bean   sampleManager;

    @EJB
    private SectionParameterBean sectParam;
    
    @EJB
    private TestBean             test;

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
    public void generateNotifications() throws Exception {
        int hours;
        AnalysisViewDO aVDO;
        ArrayList<Object[]> resultList;
        ArrayList<Integer> analysisIds;
        ArrayList<AnalysisViewDO> analyses;
        ArrayList<SampleManager1> sMans;
        ArrayList<SampleOrganizationViewDO> sampleOrgs;
        ArrayList<SectionParameterDO> sectList;
        Date tempDate;
        Datetime date, expireDate, now;
        DictionaryDO dictDO;
        Float daysElapsed;
        HashMap<Integer, AnalysisViewDO> analysesById;
        HashMap<Integer, SampleManager1> sMansByAnalysisId;
        HashMap<Integer, TestViewDO> testsById;
        Integer analysisId, prevSectId, warningTime;
        SampleManager1 sMan;
        String anaStatus, collectionString, expireString, orgName, prevSectName, receivedString, toEmail;
        StringBuilder contents;
        Timestamp availableDate;
        TestViewDO tVDO;

        now = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date());

        analysisIds = new ArrayList<Integer>();
        try {
            resultList = analysis.fetchForTurnaroundWarningReport();
            log.fine("Considering " + resultList.size() + " cases to run");
            if (resultList.size() > 0) {
                for (Object[] result : resultList) {
                    analysisId = (Integer)result[0];
                    availableDate = (Timestamp)result[1];
                    warningTime = (Integer)result[2];

                    date = new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(availableDate.getTime()));
                    if (date.add(warningTime).compareTo(now) > 0)
                        continue;
                    analysisIds.add(analysisId);
                }

                analysesById = new HashMap<Integer, AnalysisViewDO>();
                sMansByAnalysisId = new HashMap<Integer, SampleManager1>();
                testsById = new HashMap<Integer, TestViewDO>();

                sMans = sampleManager.fetchByAnalyses(analysisIds, SampleManager1.Load.ORGANIZATION);
                for (SampleManager1 data : sMans) {
                    analyses = SampleManager1Accessor.getAnalyses(data);
                    for (AnalysisViewDO data1 : analyses) {
                        analysesById.put(data1.getId(), data1);
                        sMansByAnalysisId.put(data1.getId(), data);
                    }
                }
                
                contents = new StringBuilder();
                prevSectId = -1;
                prevSectName = null;
                tempDate = new Date();
                toEmail = "";
                for (Integer aId : analysisIds) {
                    aVDO = analysesById.get(aId);
                    /*
                     * If an analysis was found in the turnaround query but not in
                     * the SampleManager query, it is an orphan analysis left over
                     * from a bug that used to be in the Send-out Order import code.
                     */
                    if (aVDO == null)
                        continue;
                    
                    sMan = sMansByAnalysisId.get(aId);
                    if (!prevSectId.equals(aVDO.getSectionId())) {
                        if (prevSectId != -1) {
                            printFooter(contents);
                            sendEmail(toEmail, "Turnaround Warning Notification: "+prevSectName, contents.toString());
                            toEmail = "";
                            contents.setLength(0);
                        }

                        try {
                            sectList = sectParam.fetchBySectionIdAndTypeId(aVDO.getSectionId(), sectParamTypeId);
                            for (SectionParameterDO data : sectList) {
                                if (toEmail.length() > 0)
                                    toEmail += ",";
                                toEmail += data.getValue().trim();
                            }
                        } catch (NotFoundException nfE) {
                            log.fine("No Turnaround Warning Email Address(es) for Section ("+aVDO.getSectionName()+").");
                            continue;
                        }
                        printHeader(contents);
                        prevSectId = aVDO.getSectionId();
                        prevSectName = aVDO.getSectionName();
                    }
                    
                    dictDO = dictionaryCache.getById(aVDO.getStatusId());
                    anaStatus = dictDO.getEntry();

                    orgName = "";
                    if (Constants.domain().PRIVATEWELL.equals(sMan.getSample().getDomain())) {
                        if (sMan.getSamplePrivateWell().getOrganization() != null)
                            orgName = sMan.getSamplePrivateWell().getOrganization().getName();
                    } else {
                        sampleOrgs = SampleManager1Accessor.getOrganizations(sMan);
                        if (sampleOrgs != null) {
                            for (SampleOrganizationViewDO soVDO : sampleOrgs) {
                                if (Constants.dictionary().ORG_REPORT_TO.equals(soVDO.getTypeId())) {
                                    orgName = soVDO.getOrganizationName();
                                    break;
                                }
                            }
                        }
                    }
                    
                    collectionString = "";
                    if (sMan.getSample().getCollectionDate() != null) {
                        collectionString = ReportUtil.toString(sMan.getSample().getCollectionDate(), Messages.get().datePattern());
                        if (sMan.getSample().getCollectionTime() != null)
                            collectionString = collectionString + " " + ReportUtil.toString(sMan.getSample().getCollectionTime(), Messages.get().timePattern());
                    }

                    receivedString = "";
                    if (sMan.getSample().getReceivedDate() != null)
                        receivedString = ReportUtil.toString(sMan.getSample().getReceivedDate(), Messages.get().dateTimePattern());

                    tVDO = testsById.get(aVDO.getTestId());
                    if (tVDO == null) {
                        tVDO = test.fetchById(aVDO.getTestId());
                        testsById.put(tVDO.getId(), tVDO);
                    }

                    hours = (int) (now.getDate().getTime() - aVDO.getAvailableDate().getDate().getTime()) / 1000 / 60 / 60;
                    daysElapsed = JasperUtil.daysAndHours(hours);
                    
                    tempDate.setTime(aVDO.getAvailableDate().getDate().getTime() + tVDO.getTimeHolding() * 60L * 60L * 1000L);
                    expireDate = new Datetime(Datetime.YEAR, Datetime.MINUTE, tempDate);
                    expireString = "";
                    if (expireDate != null)
                        expireString = ReportUtil.toString(expireDate, Messages.get().dateTimePattern());

                    printBody(contents, sMan.getSample().getAccessionNumber(), aVDO.getTestName(),
                              aVDO.getMethodName(), anaStatus, orgName, collectionString,
                              receivedString, daysElapsed, expireString);
                }
                if (resultList.size() > 0 && contents.length() > 0) {
                    printFooter(contents);
                    sendEmail(toEmail, "Turnaround Warning Notification: "+prevSectName, contents.toString());
                }
            }
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Failed to run Turnaround Warning Notification ", anyE);
            throw anyE;
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
            log.log(Level.SEVERE, "Failed to send Turnaround Warning Notification email ", anyE);
        }
    }
}