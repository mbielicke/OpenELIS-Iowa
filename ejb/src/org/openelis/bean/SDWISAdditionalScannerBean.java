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

import static org.openelis.manager.IOrderManager1Accessor.getItems;
import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.PWSMonitorDO;
import org.openelis.domain.PWSViolationDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.manager.IOrderManager1;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * Scans for SDWIS analyses that are associated with certain tests and that have
 * been released after the last time this report was run, and finds positive
 * test results in these analyses. Creates order automatically for these
 * analyses.
 */
@Stateless
@SecurityDomain("openelis")
public class SDWISAdditionalScannerBean {

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private IOrderManager1Bean  orderManager;

    @EJB
    private SampleManager1Bean  sampleManager;
    @EJB
    private PWSManagerBean      pwsManager;

    @EJB
    private PWSViolationBean    pwsViolation;

    @EJB
    private PWSBean             pws;

    private static final Logger log = Logger.getLogger("openelis");

    /**
     * Finds samples with positive results and creates orders for repeat samples
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void scan() throws Exception {
        boolean open, processed, openAllYear;
        int multi;
        String val;
        Datetime current, lastYear;
        // TODO remove this aprilFirst variable
        Calendar cal, vCal, lastYearCal, aprilFirst;
        PWSDO p;
        PWSManager pwsm;
        SampleSDWISViewDO sdwis;
        SampleManager1 sm;
        PWSMonitorManager pwsmm;
        PWSMonitorDO data;
        ArrayList<String> analytes;
        ArrayList<Integer> oids;
        HashMap<Integer, PWSManager> pwsms;
        ArrayList<PWSViolationDO> vList, processedList;
        HashMap<Integer, ArrayList<PWSViolationDO>> facilityMap;

        /*
         * get template order id/ids for the additional order
         * 
         * the format of the system variable is one or more order ids separated
         * by a semicolon:
         * 
         * orderId;orderId;... repeating pattern
         */
        try {
            val = systemVariable.fetchByName("sdwis_additional_test").getValue();
            oids = new ArrayList<Integer>();
            for (String oid : val.split(";")) {
                oids.add(Integer.parseInt(oid));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("sdwis_additional_test"),
                    e);
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
        }

        cal = Calendar.getInstance();
        current = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, cal.getTime());
        vCal = Calendar.getInstance();
        lastYearCal = Calendar.getInstance();
        lastYearCal.add(Calendar.YEAR, -1);
        lastYear = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, lastYearCal.getTime());
        processedList = new ArrayList<PWSViolationDO>();
        pwsms = new HashMap<Integer, PWSManager>();
        facilityMap = new HashMap<Integer, ArrayList<PWSViolationDO>>();
        multi = 0;
        aprilFirst = Calendar.getInstance();
        aprilFirst.set(Calendar.YEAR, 2016);
        aprilFirst.set(Calendar.MONTH, Calendar.APRIL);
        aprilFirst.set(Calendar.DAY_OF_MONTH, 1);
        aprilFirst.set(Calendar.HOUR_OF_DAY, 0);
        aprilFirst.set(Calendar.MINUTE, 0);

        for (PWSViolationDO v : pwsViolation.fetchAll()) {

            /*
             * if the sample ID is null, then the additional has already been
             * processed for that month
             */
            if (v.getSampleId() == null)
                continue;

            vCal.setTime(v.getViolationDate().getDate());

            /*
             * if the violation is in this month, ignore it
             */
            if (vCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH))
                continue;
            p = pws.fetchByTinwsysIsNumber(v.getTinwsysIsNumber());

            /*
             * determine if the pws is open currently and if it is open all year
             */
            if (p.getEndMonth() < p.getStartMonth()) {
                open = ( (cal.get(Calendar.MONTH) + 1 > p.getStartMonth() || cal.get(Calendar.MONTH) + 1 < p.getEndMonth()) ||
                        (cal.get(Calendar.MONTH) + 1 == p.getStartMonth() && cal.get(Calendar.DAY_OF_MONTH) >= p.getStartDay()) || (cal.get(Calendar.MONTH) + 1 == p.getEndMonth() && cal.get(Calendar.DAY_OF_MONTH) < p.getEndDay())) &&
                       DataBaseUtil.isAfter(current, p.getEffBeginDt()) &&
                       (p.getEffEndDt() == null || DataBaseUtil.isAfter(p.getEffEndDt(), current));
                openAllYear = (p.getStartMonth() - p.getEndMonth() == 1);
            } else {
                open = ( (cal.get(Calendar.MONTH) + 1 > p.getStartMonth() && cal.get(Calendar.MONTH) + 1 < p.getEndMonth()) ||
                        (cal.get(Calendar.MONTH) + 1 == p.getStartMonth() && cal.get(Calendar.DAY_OF_MONTH) >= p.getStartDay()) || (cal.get(Calendar.MONTH) + 1 == p.getEndMonth() && cal.get(Calendar.DAY_OF_MONTH) < p.getEndDay())) &&
                       DataBaseUtil.isAfter(current, p.getEffBeginDt()) &&
                       (p.getEffEndDt() == null || DataBaseUtil.isAfter(p.getEffEndDt(), current));
                openAllYear = (p.getStartMonth() == 1 && p.getEndMonth() == 12);
            }

            if (open) {

                /*
                 * Find if we have already created an additional order for this
                 * facility and series. Only one additional order should be
                 * created per month for each specific series.
                 */
                vList = facilityMap.get(v.getTinwsysIsNumber());
                if (vList != null) {
                    processed = false;
                    for (PWSViolationDO pwsv : vList) {
                        if (v.getFacilityId().equals(pwsv.getFacilityId()) &&
                            v.getSeries().equals(pwsv.getSeries())) {
                            processedList.add(v);
                            processed = true;
                            break;
                        }
                    }
                    if (processed)
                        continue;
                    vList.add(v);
                } else {
                    vList = new ArrayList<PWSViolationDO>();
                    vList.add(v);
                    facilityMap.put(v.getTinwsysIsNumber(), vList);
                }
                sm = sampleManager.fetchById(v.getSampleId(),
                                             SampleManager1.Load.ORGANIZATION,
                                             SampleManager1.Load.AUXDATA,
                                             SampleManager1.Load.PROJECT);
                sdwis = getSampleSDWIS(sm);
                pwsm = pwsms.get(sdwis.getPwsId());
                if (pwsm == null) {
                    pwsm = pwsManager.fetchById(sdwis.getPwsId());
                    pwsms.put(sdwis.getPwsId(), pwsm);
                }
                pwsmm = pwsm.getMonitors();
                data = null;
                for (int i = 0; i < pwsmm.count(); i++ ) {
                    data = pwsmm.getMonitorAt(i);
                    if (data.getNumberSamples() != null &&
                        DataBaseUtil.isSame(getSampleSDWIS(sm).getFacilityId(),
                                            data.getStAsgnIdentCd()) &&
                        DataBaseUtil.isSame(v.getSeries(), data.getTiaanlgpTiaanlytName()))
                        break;
                    else
                        data = null;
                }

                if (data == null) {
                    log.log(Level.SEVERE,
                            Messages.get()
                                    .sdwisScan_noValidMonitorWarning(DataBaseUtil.toString(v.getId())));
                    continue;
                }

                // TODO remove this code once all samples collected before april
                // 1, 2016 have been completely processed
                if (aprilFirst.getTime().after(getSample(sm).getCollectionDate().getDate())) {
                    /*
                     * If the facility samples quarterly, only consider
                     * violations from up to one year ago
                     */
                    if ("QUARTERLY".equals(data.getFrequencyName()) &&
                        DataBaseUtil.isAfter(lastYear, v.getViolationDate())) {
                        processedList.add(v);
                        continue;
                    }

                    /*
                     * send five additional bottles
                     */
                    multi = 5;

                    for (Integer oid : oids)
                        createOrder(sm, pwsm, oid, analytes, v, multi);
                    processedList.add(v);
                } else {
                    /*
                     * If the facility samples quarterly and is open all year
                     * and the violation was within the last year, send three
                     * additional bottles
                     */
                    if ("QUARTERLY".equals(data.getFrequencyName()) && openAllYear &&
                        !DataBaseUtil.isAfter(lastYear, v.getViolationDate())) {
                        multi = 3;

                        for (Integer oid : oids)
                            createOrder(sm, pwsm, oid, analytes, v, multi);
                    }
                    processedList.add(v);
                }
            }
        }

        if (processedList.size() > 0)
            updateProcessed(processedList);
    }

    /**
     * duplicates an order template and gets the order manager ready to be
     * filled with data from the sample
     */
    private void createOrder(SampleManager1 sm, PWSManager pwsm, Integer orderTemplate,
                             ArrayList<String> analytes, PWSViolationDO violation, int multiplier) throws Exception {
        int index;
        IOrderManager1 om;

        om = orderManager.duplicate(orderTemplate, true, false).getManager();

        if (getItems(om) != null) {
            for (IOrderItemViewDO item : getItems(om))
                item.setQuantity(item.getQuantity() * multiplier);
        }

        /*
         * additional orders cannot have the original sample number aux data
         * filled
         */
        index = -1;
        for (String s : analytes) {
            if (AuxDataHelperBean.ORIG_SAMPLE_NUMBER.equals(s))
                index = analytes.indexOf(s);
        }
        if (index != -1)
            analytes.remove(index);
        orderManager.createIOrderFromSample(om, sm, analytes);
    }

    /**
     * Find the number of distributions in the monitor manager. Assume that the
     * first two characters in the st_asgn_ident_cd of a distribution system are
     * "95", and that it is exactly three characters long: 950-959
     */
    private int distributionCount(PWSMonitorManager mm) {
        int distCount;
        String stAsgnIdentCd;

        distCount = 0;
        for (int i = 0; i < mm.count(); i++ ) {
            stAsgnIdentCd = mm.getMonitorAt(i).getStAsgnIdentCd();
            if (stAsgnIdentCd.length() != 3)
                continue;
            try {
                if (stAsgnIdentCd.charAt(0) == '9' && stAsgnIdentCd.charAt(1) == '5' &&
                    Integer.parseInt(DataBaseUtil.toString(stAsgnIdentCd.charAt(2))) > -1) {
                    distCount++ ;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return distCount;
    }

    private void updateProcessed(ArrayList<PWSViolationDO> violations) throws Exception {
        for (PWSViolationDO v : violations) {
            v.setSampleId(null);
            pwsViolation.update(v);
        }
    }
}
