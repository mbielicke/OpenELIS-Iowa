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

import static org.openelis.scriptlet.SampleSO.Operation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for performing operations for the neonatal domain e.g. the ones
 * related to repeat samples
 */
public class NeonatalDomainScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy                                proxy;

    private SampleManager1                       previousManager;

    private Integer                              patientId;

    private static HashMap<Integer, TestManager> testManagers;

    private static HashSet<Integer>              normalTestResults;

    private static HashSet<String>               interpretations;

    private static DictionaryDO                  driedBloodSpotDict;

    public NeonatalDomainScriptlet1(Proxy proxy) throws Exception {
        /*
         * external ids for the analytes for interpretation
         */
        if (interpretations == null) {
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
         * the dictionary for most common sample type for neonatal domain
         */
        if (driedBloodSpotDict == null)
            driedBloodSpotDict = DictionaryCache.getBySystemName("dried_blood_spot");

        this.proxy = proxy;
    }

    @Override
    public SampleSO run(SampleSO data) {
        String changed;
        SampleItemViewDO item;
        SampleNeonatalDO sn;
        SampleManager1 sm;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();

        /*
         * if the sample doesn't have any items then add an item with "dried
         * blood spot" as its sample type
         */
        if (sm.item.count() == 0) {
            item = sm.item.add();
            item.setTypeOfSampleId(driedBloodSpotDict.getId());
            item.setTypeOfSample(driedBloodSpotDict.getEntry());
        }

        /*
         * don't do anything if it's an existing neonatal sample or if it
         * doesn't have a patient
         */
        if ( !data.getOperations().contains(NEW_DOMAIN_ADDED) || sn.getPatientId() == null)
            return data;

        try {
            changed = data.getChanged();
            if (changed != null) {
                fetchPreviousManager(sm);
                /*
                 * check if the current sample is a repeat if a field related to
                 * neonatal domain has changed, the sample is not marked as
                 * repeat and the patient is an existing one
                 */
                if (previousManager != null &&
                    (changed.startsWith("_sampleNeonatal") || changed.startsWith("_neonatal")) &&
                    "N".equals(sn.getIsRepeat())) {
                    sn.setIsRepeat("Y");
                    /*
                     * notify the scriptlet runner that a field was changed
                     */
                    data.addRerun(SampleMeta.getNeonatalIsRepeat());
                }
            } else if (data.getOperations().contains(TEST_ADDED) && "Y".equals(sn.getIsRepeat())) {
                fetchPreviousManager(sm);
                /*
                 * since the current sample is a repeat sample, remove any
                 * uncommitted tests from it that didn't have any abnormal
                 * results in the previous sample
                 */
                if (previousManager != null) {
                    sm = removeNormalTests(sm);
                    data.setManager(sm);
                }
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }

        return data;
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

        try {
            sms = proxy.fetchByQuery(fields, 0, 1000, SampleManager1.Load.RESULT);

            /*
             * find the sample with the most recent entered date;
             */
            ed = null;
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
            previousManager = null;
        }
        patientId = sm.getSampleNeonatal().getPatientId();
    }

    /**
     * TODO remove the uncommitted analyses from the current sample that are
     * linked to tests that have no abnormal results in the previous sample
     */
    public SampleManager1 removeNormalTests(SampleManager1 sm) throws Exception {
        int i, j, k, l;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        ResultViewDO res;
        TestViewDO t;
        TestResultViewDO tr;
        TestResultManager trm;
        DictionaryDO dict;
        ArrayList<TestManager> tms;
        ArrayList<Integer> anaIds;
        HashSet<Integer> testIds;
        Datetime bt, et;

        /*
         * find out which tests are present in the previous sample
         */
        testIds = new HashSet<Integer>();
        if (testManagers == null)
            testManagers = new HashMap<Integer, TestManager>();

        bt = Datetime.getInstance();
        proxy.log(Level.FINE, "Starting removeTests", null);
        for (i = 0; i < previousManager.item.count(); i++ ) {
            item = previousManager.item.get(i);
            for (j = 0; j < previousManager.analysis.count(item); j++ ) {
                ana = previousManager.analysis.get(item, j);
                if ( !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) &&
                    testManagers.get(ana.getTestId()) == null)
                    testIds.add(ana.getTestId());
            }
        }
        et = Datetime.getInstance();
        proxy.log(Level.FINE, "Going through the analyses of the previous sample took " +
                              (et.getDate().getTime() - bt.getDate().getTime()), null);

        if (normalTestResults == null)
            normalTestResults = new HashSet<Integer>();
        if (testIds.size() > 0) {
            bt = Datetime.getInstance();
            /*
             * fetch the test managers for the tests found above
             */
            tms = proxy.fetchTestManagersByIds(new ArrayList<Integer>(testIds));
            et = Datetime.getInstance();
            proxy.log(Level.FINE, "Fetching test managers took " +
                                  (et.getDate().getTime() - bt.getDate().getTime()), null);
            bt = Datetime.getInstance();
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
            et = Datetime.getInstance();
            proxy.log(Level.FINE, "Going through test analytes and results took " +
                                  (et.getDate().getTime() - bt.getDate().getTime()), null);
        }

        bt = Datetime.getInstance();
        /*
         * find out which analyses in the previous sample have normal results
         * for the interpretation
         */
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

        et = Datetime.getInstance();
        proxy.log(Level.FINE, "Going through the results of the previous sample took " +
                              (et.getDate().getTime() - bt.getDate().getTime()), null);

        bt = Datetime.getInstance();
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

        et = Datetime.getInstance();
        proxy.log(Level.FINE, "Going through the analyses of the current sample took " +
                              (et.getDate().getTime() - bt.getDate().getTime()), null);

        bt = Datetime.getInstance();
        /*
         * remove the analyses found above
         */
        for (Integer id : anaIds)
            sm = proxy.removeAnalysis(sm, id);

        et = Datetime.getInstance();
        proxy.log(Level.FINE, "Removing analyses took " +
                              (et.getDate().getTime() - bt.getDate().getTime()), null);

        return sm;
    }

    public static interface Proxy {
        public ArrayList<SampleManager1> fetchByQuery(ArrayList<QueryData> fields, int first,
                                                      int max, SampleManager1.Load... elements) throws Exception;

        public ArrayList<TestManager> fetchTestManagersByIds(ArrayList<Integer> ids) throws Exception;

        public DictionaryDO getDictionaryById(Integer id) throws Exception;

        public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception;

        public void log(Level level, String message, Exception e);
    }
}
