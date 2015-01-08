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
package org.openelis.scriptlet;

import static org.openelis.scriptlet.SampleSO.Action_Before.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO.Action_After;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for the neonatal domain. It adds a sample item with a default
 * sample type to the sample if it doesn't have any sample items. It
 * recalculates collection age if collection date-time or birth date-time
 * changes. It sets the sample as repeat if there's another sample previosuly
 * entered for its patient. If the sample is marked as repeat, when new tests
 * are added to the sample, it removes those tests from the current sample that
 * had normal results in the previous sample for the patient.
 */
public class NeonatalIAScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy                                proxy;

    private SampleManager1                       previousManager;

    private Integer                              patientId;

    private static HashMap<Integer, TestManager> testManagers;

    private static HashSet<Integer>              normalTestResults;

    private static HashSet<String>               interpretations;

    private static DictionaryDO                  driedBloodSpotDict;

    public NeonatalIAScriptlet1(Proxy proxy) throws Exception {
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing NeonatalIAScriptlet1");
        /*
         * external ids for the analytes for interpretation
         */
        if (interpretations == null) {
            proxy.log(Level.FINE, "Creating a hashset for the analytes for interpretation");
            interpretations = new HashSet<String>();
            interpretations.add("nbs_bt_inter");
            interpretations.add("nbs_cah_inter");
            interpretations.add("nbs_cf_inter");
            interpretations.add("nbs_galt_inter");
            interpretations.add("nbs_hb_inter");
            interpretations.add("nbs_tsh_inter");
            interpretations.add("nbs_irt_inter");
            interpretations.add("nbs_tms_inter");
        }

        /*
         * the dictionary entry for the most common sample type for neonatal
         * domain
         */
        if (driedBloodSpotDict == null) {
            proxy.log(Level.FINE, "Getting the dictionary for 'dried_blood_spot'");
            driedBloodSpotDict = proxy.getDictionaryBySystemName("dried_blood_spot");
        }
    }

    @Override
    public SampleSO run(SampleSO data) {
        String changed;
        SampleNeonatalViewDO sn;
        SampleManager1 sm;

        proxy.log(Level.FINE, "In NeonatalIAScriptlet1.run");

        /*
         * if the sample doesn't have any items then add an item with the most
         * common sample type for neonatal samples
         */
        sm = data.getManager();
        if (sm.item.count() == 0)
            addDefaultSampleItem(data);

        changed = data.getChanged();
        /*
         * if either the collection date/time or patient birth date/time has
         * changed then reset the collection age
         */
        if (SampleMeta.getCollectionDate().equals(changed) ||
            SampleMeta.getCollectionTime().equals(changed) ||
            SampleMeta.getNeonatalPatientBirthDate().equals(changed) ||
            SampleMeta.getNeonatalPatientBirthTime().equals(changed))
            resetCollectionAge(data);

        sn = sm.getSampleNeonatal();
        /*
         * no further processing
         */
        if (sn == null)
            return data;

        /*
         * don't do anything if it's an existing neonatal sample or if it
         * doesn't have a patient
         */
        if ( !data.getActionBefore().contains(NEW_DOMAIN) || sn.getPatientId() == null)
            return data;

        try {
            if (changed != null)
                /*
                 * set this sample as repeat if there's another sample entered
                 * previously for its patient
                 */
                setRepeat(data);
            else if (data.getActionBefore().contains(ANALYSIS) && "Y".equals(sn.getIsRepeat()))
                /*
                 * since the current sample is a repeat sample, remove those
                 * uncommitted tests from it that didn't have any abnormal
                 * results in the previous sample
                 */
                removeNormalTests(data);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }

        return data;
    }

    /**
     * If the sample doesn't have any items then adds an item with the sample
     * type 'dried blood spot'
     */
    private void addDefaultSampleItem(SampleSO data) {
        SampleItemViewDO item;

        proxy.log(Level.FINE, "Adding a sample item by default with sample type: " +
                              driedBloodSpotDict.getSystemName());
        item = data.getManager().item.add();
        item.setTypeOfSampleId(driedBloodSpotDict.getId());
        item.setTypeOfSample(driedBloodSpotDict.getEntry());
        data.addChangedUid(Constants.uid().getSampleItem(item.getId()));
        data.addActionAfter(Action_After.SAMPLE_ITEM_ADDED);
    }

    /**
     * Calculates and resets the collection age as the difference between the
     * collection date-time and patient birth date-time
     */
    private void resetCollectionAge(SampleSO data) {
        Datetime cdt, ct, bdt, bt;
        Date cd, bd;
        Long diff;
        Integer age;
        SampleManager1 sm;
        SampleNeonatalViewDO sn;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();

        proxy.log(Level.FINE, "Resetting collection age");

        cdt = sm.getSample().getCollectionDate();
        ct = sm.getSample().getCollectionTime();
        bdt = sn.getPatient().getBirthDate();
        bt = sn.getPatient().getBirthTime();

        if (cdt == null || bdt == null) {
            age = null;
        } else {
            /*
             * combine collection date and time
             */
            cd = (Date)cdt.getDate().clone();
            if (ct == null) {
                cd.setHours(0);
                cd.setMinutes(0);
            } else {
                cd.setHours(ct.getDate().getHours());
                cd.setMinutes(ct.getDate().getMinutes());
            }
            cdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, cd);

            /*
             * combine patient birth date and time
             */
            bd = (Date)bdt.getDate().clone();
            if (bt == null) {
                bd.setHours(0);
                bd.setMinutes(0);
            } else {
                bd.setHours(bt.getDate().getHours());
                bd.setMinutes(bt.getDate().getMinutes());
            }
            bdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, bd);

            /*
             * calculate the collection age in minutes
             */
            diff = ( (cdt.getDate().getTime() - bdt.getDate().getTime()) / 60000);
            age = diff.intValue();
        }

        sn.setCollectionAge(age);
        data.addRerun(SampleMeta.getNeonatalCollectionAge());
    }

    /**
     * Fetches the most recent previous sample for the sample's neonatal patient
     */
    private void fetchPreviousManager(SampleManager1 sm) throws Exception {
        QueryData field;
        Datetime ed;
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;

        if (patientId != null && patientId.equals(sm.getSampleNeonatal().getPatientId()))
            return;

        /*
         * fetch all samples with this patient
         */
        fields = new ArrayList<QueryData>();
        field = new QueryData();
        field.setKey(SampleMeta.getNeonatalPatientId());
        field.setQuery(sm.getSampleNeonatal().getPatientId().toString());
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        proxy.log(Level.FINE, "Fetching samples for patient: " +
                              sm.getSampleNeonatal().getPatientId());
        try {
            sms = proxy.fetchByQuery(fields, 0, 1000, SampleManager1.Load.RESULT);

            /*
             * find the sample with the most recent entered date;
             */
            ed = null;
            proxy.log(Level.FINE, "Finding the most recent previous sample");
            for (SampleManager1 data : sms) {
                if (ed == null || DataBaseUtil.isAfter(data.getSample().getEnteredDate(), ed)) {
                    previousManager = data;
                    ed = data.getSample().getEnteredDate();
                }
            }
        } catch (NotFoundException e) {
            /*
             * there are no previous samples for this patient
             */
            proxy.log(Level.FINE, "No samples found for patient: " +
                                  sm.getSampleNeonatal().getPatientId());
            previousManager = null;
        }
        patientId = sm.getSampleNeonatal().getPatientId();
    }

    /**
     * Sets the sample in the passed SO as repeat if there's another sample
     * previosuly entered for its patient
     */
    private void setRepeat(SampleSO data) throws Exception {
        String changed;
        SampleNeonatalViewDO sn;
        SampleManager1 sm;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();

        fetchPreviousManager(sm);
        /*
         * check if the current sample is a repeat if a field related to
         * neonatal domain has changed, the sample is not marked as repeat and
         * the patient is an existing one
         */
        changed = data.getChanged();
        if (previousManager != null &&
            (changed.startsWith("_sampleNeonatal") || changed.startsWith("_neonatal")) &&
            "N".equals(sn.getIsRepeat())) {
            proxy.log(Level.FINE, "Setting the sample as repeat");
            sn.setIsRepeat("Y");
            /*
             * notify the scriptlet runner that a field was changed
             */
            data.addRerun(SampleMeta.getNeonatalIsRepeat());
        }
    }

    /**
     * Removes the uncommitted tests from the passed SO's sample that have no
     * abnormal results in the previous sample
     */
    private void removeNormalTests(SampleSO data) throws Exception {
        int i, j, k, l;
        DictionaryDO dict;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        ResultViewDO res;
        TestViewDO t;
        TestResultViewDO tr;
        TestResultManager trm;
        SampleManager1 sm;
        ArrayList<TestManager> tms;
        ArrayList<Integer> anaIds;
        HashSet<Integer> testIds;

        sm = data.getManager();
        fetchPreviousManager(sm);
        if (previousManager == null)
            return;

        /*
         * find out which tests are present in the previous sample
         */
        testIds = new HashSet<Integer>();
        if (testManagers == null)
            testManagers = new HashMap<Integer, TestManager>();

        proxy.log(Level.FINE, "Going through the analyses of the previous sample");

        for (i = 0; i < previousManager.item.count(); i++ ) {
            item = previousManager.item.get(i);
            for (j = 0; j < previousManager.analysis.count(item); j++ ) {
                ana = previousManager.analysis.get(item, j);
                if ( !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) &&
                    testManagers.get(ana.getTestId()) == null)
                    testIds.add(ana.getTestId());
            }
        }

        if (normalTestResults == null)
            normalTestResults = new HashSet<Integer>();
        if (testIds.size() > 0) {
            proxy.log(Level.FINE, "Fetching test managers");
            /*
             * fetch the test managers for the tests found above
             */
            tms = proxy.fetchTestManagersByIds(new ArrayList<Integer>(testIds));

            proxy.log(Level.FINE, "Going through the test results of the fetched managers");
            for (TestManager tm : tms) {
                testManagers.put(tm.getTest().getId(), tm);
                /*
                 * find out which test results are normal
                 */
                trm = tm.getTestResults();
                for (i = 0; i < trm.groupCount(); i++ ) {
                    for (j = 0; j < trm.getResultGroupSize(i + 1); j++ ) {
                        tr = trm.getResultAt(i + 1, j);
                        if (tr.getFlagsId() != null) {
                            dict = proxy.getDictionaryById(tr.getFlagsId());
                            if (dict.getSystemName() != null &&
                                !dict.getSystemName().startsWith("rf_a"))
                                normalTestResults.add(tr.getId());
                        } else {
                            normalTestResults.add(tr.getId());
                        }
                    }
                }
            }
        }

        /*
         * find out which analyses in the previous sample have normal results
         * for the interpretation
         */
        proxy.log(Level.FINE, "Going through the results of the previous sample");
        testIds.clear();
        for (i = 0; i < previousManager.item.count(); i++ ) {
            item = previousManager.item.get(i);
            for (j = 0; j < previousManager.analysis.count(item); j++ ) {
                ana = previousManager.analysis.get(item, j);
                if (Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
                    continue;
                for (k = 0; k < previousManager.result.count(ana); k++ ) {
                    for (l = 0; l < previousManager.result.count(ana, k); l++ ) {
                        res = previousManager.result.get(ana, k, l);
                        /*
                         * if this result is normal and its test is one of the
                         * tests on the neonatal panel and its analyte is an
                         * interpretation, then add its test to the list for
                         * tests with normal results
                         */
                        if (res.getTestResultId() != null &&
                            normalTestResults.contains(res.getTestResultId()) &&
                            interpretations.contains(res.getAnalyteExternalId()))
                            testIds.add(ana.getTestId());
                    }
                }
            }
        }

        proxy.log(Level.FINE, "Finding analyses to remove in the current manager");
        /*
         * find the uncommitted analyses in the current sample whose tests have
         * have normal results in the previous sample
         */
        anaIds = new ArrayList<Integer>();
        for (i = 0; i < sm.item.count(); i++ ) {
            item = sm.item.get(i);
            for (j = 0; j < sm.analysis.count(item); j++ ) {
                ana = sm.analysis.get(item, j);
                if (ana.getId() >= 0)
                    continue;
                if (testIds.contains(ana.getTestId())) {
                    /*
                     * the same version of this test was present in the previous
                     * manager
                     */
                    anaIds.add(ana.getId());
                } else {
                    /*
                     * the same version of this test was not present in the
                     * previous manager, so look for any previous version by
                     * name
                     */
                    for (Integer id : testIds) {
                        t = testManagers.get(id).getTest();
                        if (t.getName().equals(ana.getTestName()) &&
                            t.getMethodName().equals(ana.getMethodName())) {
                            anaIds.add(ana.getId());
                            break;
                        }
                    }
                }
            }
        }

        proxy.log(Level.FINE, "Removing " + anaIds.size() + " analyses");
        /*
         * remove the analyses found above
         */
        for (Integer id : anaIds)
            sm = proxy.removeAnalysis(sm, id);

        data.setManager(sm);
    }

    public static interface Proxy {
        public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                      int max, SampleManager1.Load... elements) throws Exception;

        public ArrayList<TestManager> fetchTestManagersByIds(ArrayList<Integer> ids) throws Exception;

        public DictionaryDO getDictionaryById(Integer id) throws Exception;

        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception;

        public void log(Level level, String message);
    }
}