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

import static org.openelis.manager.OrderManager1Accessor.getItems;
import static org.openelis.manager.SampleManager1Accessor.getResults;
import static org.openelis.manager.SampleManager1Accessor.getSampleSDWIS;
import static org.openelis.manager.SampleManager1Accessor.getSample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.PWSMonitorDO;
import org.openelis.domain.PWSViolationDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

/**
 * Scans for SDWIS analyses that are associated with certain tests and that have
 * been released after the last time this report was run, and finds positive
 * test results in these analyses. Creates order automatically for these
 * analyses.
 */
@Stateless
@SecurityDomain("openelis")
public class SDWISViolationScannerBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                manager;

    @EJB
    private SystemVariableBean           systemVariable;

    @EJB
    private SampleManagerOrderHelperBean sampleManagerOrderHelper;

    @EJB
    private SampleManager1Bean           sampleManager;

    @EJB
    private OrderManager1Bean            orderManager;

    @EJB
    private TestResultBean               testResult;

    @EJB
    private PWSManagerBean               pwsManager;

    @EJB
    private PWSViolationBean             pwsViolation;

    private static final Logger          log = Logger.getLogger("openelis");

    /**
     * Finds samples with positive results and creates orders for repeat samples
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void scan() throws Exception {
        Integer testId;
        String val, series, temp[], sTemp[];
        SampleSDWISViewDO sdwis;
        TestResultViewDO tr;
        Date startTime, endTime;
        SystemVariableDO lastRun;
        Calendar cal;
        SimpleDateFormat dateTimeFormat;
        PWSManager pwsm;
        ArrayList<String> analytes;
        ArrayList<Integer> roids, toids, tids, aids;
        ArrayList<SampleManager1> sms;
        ArrayList<TestResultViewDO> trs;
        HashMap<Integer, TestResultViewDO> positives;
        HashMap<Integer, ArrayList<Integer>> repeatTemplates, triggerTemplates;
        HashMap<Integer, String> seriesMap;
        HashMap<Integer, PWSManager> pwsms;

        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        endTime = cal.getTime();

        /*
         * get the last run date
         */
        dateTimeFormat = new SimpleDateFormat(Messages.get().dateTimePattern());
        lastRun = null;
        try {
            lastRun = systemVariable.fetchForUpdateByName("last_sdwis_violation_run");
            startTime = dateTimeFormat.parse(lastRun.getValue());
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("last_sdwis_violation_run"),
                    e);
            if (lastRun != null)
                systemVariable.abortUpdate(lastRun.getId());
            throw e;
        }

        /*
         * create a mapping between the tests defined in the system variable and
         * the order templates associated with them
         * 
         * the format of the system variable is several test ids, each mapped to
         * a series string and a list of order ids(templates) with a prefix
         * charater: R for repeat, T for trigger
         * testId,series:orderId,RorderId,TorderId; repeating pattern
         */
        try {
            val = systemVariable.fetchByName("sdwis_repeat_tests").getValue();
            tids = new ArrayList<Integer>();
            roids = new ArrayList<Integer>();
            toids = new ArrayList<Integer>();
            seriesMap = new HashMap<Integer, String>();
            repeatTemplates = new HashMap<Integer, ArrayList<Integer>>();
            triggerTemplates = new HashMap<Integer, ArrayList<Integer>>();
            for (String tid : val.split(";")) {
                roids.clear();
                toids.clear();
                temp = tid.split(":");
                sTemp = temp[0].split(",");
                testId = Integer.parseInt(sTemp[0]);
                seriesMap.put(testId, sTemp[1]);
                tids.add(testId);
                for (String template : temp[1].split(",")) {
                    if (template.charAt(0) == 'R')
                        roids.add(Integer.parseInt(template.substring(1)));
                    else if (template.charAt(0) == 'T')
                        toids.add(Integer.parseInt(template.substring(1)));
                }
                repeatTemplates.put(testId, roids);
                triggerTemplates.put(testId, toids);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("sdwis_repeat_tests"),
                    e);
            systemVariable.abortUpdate(lastRun.getId());
            throw e;
        }

        /*
         * find the aux data analytes whose values need to be copied to the
         * order
         */
        analytes = null;
        try {
            val = systemVariable.fetchByName("sdwis_aux_data").getValue();
            analytes = new ArrayList<String>();
            for (String ana : val.split(";"))
                analytes.add(ana);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("sdwis_aux_data"),
                    e);
            systemVariable.abortUpdate(lastRun.getId());
        }

        /*
         * get all of the test results with abnormal flags that are associated
         * with the tests we are interested in
         */
        trs = testResult.fetchTestResultsByTestIdsAndFlagPattern(tids, "rf%a%");
        if (trs.size() == 0) {
            log.log(Level.SEVERE, Messages.get().sdwisScan_noAbnormalResultsFoundException());
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }
        positives = new HashMap<Integer, TestResultViewDO>();
        for (TestResultViewDO data : trs)
            positives.put(data.getId(), data);

        /*
         * get the analyses in the SDWIS domain with the test ids and within the
         * correct time period
         */
        aids = new ArrayList<Integer>();
        for (AnalysisViewVO ana : fetchForSDWISViolation(startTime, endTime, tids))
            aids.add(ana.getAnalysisId());
        log.log(Level.INFO, Messages.get().gen_numberCases(DataBaseUtil.toString(aids.size())));
        if (aids.size() == 0) {
            lastRun.setValue(dateTimeFormat.format(endTime));
            systemVariable.update(lastRun);
            return;
        }
        sms = sampleManager.fetchByAnalyses(aids,
                                            SampleManager1.Load.ORGANIZATION,
                                            SampleManager1.Load.AUXDATA,
                                            SampleManager1.Load.SINGLERESULT,
                                            SampleManager1.Load.PROJECT);

        /*
         * create orders for templates that are associated with the test of the
         * first positive result of each sample that is in the bacterial
         * category and is of the routine sample type
         */
        pwsms = new HashMap<Integer, PWSManager>();
        for (SampleManager1 sm : sms) {
            sdwis = getSampleSDWIS(sm);
            if (Constants.dictionary().SDWIS_CATEGORY_BACTERIAL.equals(sdwis.getSampleCategoryId()) &&
                Constants.dictionary().SMPL_TYPE_RT.equals(sdwis.getSampleTypeId())) {
                pwsm = pwsms.get(sdwis.getPwsId());
                if (pwsm == null) {
                    pwsm = pwsManager.fetchById(sdwis.getPwsId());
                    pwsms.put(sdwis.getPwsId(), pwsm);
                }
                for (ResultViewDO r : getResults(sm)) {
                    tr = positives.get(r.getTestResultId());
                    if (tr != null) {
                        series = seriesMap.get(tr.getTestId());
                        for (Integer oid : repeatTemplates.get(tr.getTestId()))
                            createOrder(sm, pwsm, oid, series, analytes, false);
                        for (Integer oid : triggerTemplates.get(tr.getTestId()))
                            createOrder(sm, pwsm, oid, series, analytes, true);
                        try {
                            updateViolation(sdwis.getFacilityId(),
                                            series,
                                            pwsm.getPWS().getTinwsysIsNumber(),
                                            getSample(sm).getCollectionDate().getDate(),
                                            getSample(sm).getId());
                        } catch (Exception e) {
                            log.log(Level.SEVERE, e.getMessage(), e);
                            systemVariable.abortUpdate(lastRun.getId());
                        }
                        break;
                    }
                }
            }
        }

        lastRun.setValue(dateTimeFormat.format(endTime));
        systemVariable.update(lastRun);
    }

    /**
     * Try to find a violation that is in the same month and year as the
     * violation we are currently looking at. If one is found, do nothing. If
     * one is not found, this is the first violation for this month and year so
     * insert it into the database.
     */
    private void updateViolation(String facilityId, String series, Integer tinwsysIsNumber,
                                 Date violationDate, Integer sampleId) throws Exception {
        PWSViolationDO violation;
        Calendar firstOfMonth, lastOfMonth;

        firstOfMonth = Calendar.getInstance();
        firstOfMonth.setTime(violationDate);
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        lastOfMonth = Calendar.getInstance();
        lastOfMonth.setTime(violationDate);
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        try {
            violation = pwsViolation.fetchByFacilityIdAndSeries(facilityId,
                                                                series,
                                                                tinwsysIsNumber,
                                                                firstOfMonth.getTime(),
                                                                lastOfMonth.getTime());
        } catch (NotFoundException e) {
            violation = new PWSViolationDO();
            violation.setFacilityId(facilityId);
            violation.setSeries(series);
            violation.setTinwsysIsNumber(tinwsysIsNumber);
            violation.setViolationDate(DataBaseUtil.toYD(firstOfMonth.getTime()));
            violation.setSampleId(sampleId);
            pwsViolation.add(violation);
        }
    }

    /**
     * duplicates an order template and gets the order manager ready to be
     * filled with data from the sample
     */
    private void createOrder(SampleManager1 sm, PWSManager pwsm, Integer orderTemplate,
                             String series, ArrayList<String> analytes, boolean trigger) throws Exception {
        int multi;
        OrderManager1 om;
        PWSMonitorManager pwsmm;
        PWSMonitorDO data;

        om = orderManager.duplicate(orderTemplate, true);

        /*
         * Find out how many bottles we need to send, and multiply each of the
         * item quantities. This does not need to be done for trigger orders.
         */
        if (getItems(om) != null && !trigger) {
            multi = 1;
            pwsmm = pwsm.getMonitors();
            for (int i = 0; i < pwsmm.count(); i++ ) {
                data = pwsmm.getMonitorAt(i);
                if (data.getNumberSamples() != null &&
                    DataBaseUtil.isSame(getSampleSDWIS(sm).getFacilityId(), data.getStAsgnIdentCd()) &&
                    DataBaseUtil.isSame(series, data.getTiaanlgpTiaanlytName())) {
                    if (data.getNumberSamples() == 1)
                        multi = 4;
                    else if (data.getNumberSamples() > 1)
                        multi = 3;
                    break;
                }
            }

            for (OrderItemViewDO item : getItems(om))
                item.setQuantity(item.getQuantity() * multi);
        }

        sampleManagerOrderHelper.createOrderFromSample(om, sm, analytes);
    }

    /**
     * find analyses released between the given dates with potential SDWIS
     * violations(abnormal results)
     */
    private List<AnalysisViewVO> fetchForSDWISViolation(Date startDate, Date endDate,
                                                        ArrayList<Integer> tids) {
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchForSDWISViolation");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("ids", tids);

        return query.getResultList();
    }
}