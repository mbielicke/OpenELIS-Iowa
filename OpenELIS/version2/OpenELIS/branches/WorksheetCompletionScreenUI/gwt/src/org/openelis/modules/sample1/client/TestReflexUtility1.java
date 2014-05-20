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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;
import java.util.HashSet;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestReflexManager;

/**
 * This class is used to obtain the reflex tests triggered by specific results
 */
public abstract class TestReflexUtility1 {

    /**
     * overridden to return the TestManager for the test with this id
     */
    public abstract TestManager getTestManager(Integer testId) throws Exception;

    public ArrayList<SampleTestRequestVO> getReflexTests(ArrayList<SampleManager1> sms,
                                                         ArrayList<ArrayList<ResultViewDO>> results) throws Exception {
        int i;
        ArrayList<SampleTestRequestVO> tests;
        
        tests = new ArrayList<SampleTestRequestVO>();
        for (i = 0; i < sms.size(); i++)
            tests.addAll(getReflexTests(sms.get(i), results.get(i)));
        
        return tests;
    }
    
    public ArrayList<SampleTestRequestVO> getReflexTests(SampleManager1 sm,
                                                         ArrayList<ResultViewDO> results) throws Exception {

        int i, j;
        boolean allowDup;
        Integer anaId;
        TestManager tm;
        TestReflexManager trm;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        SampleTestRequestVO test;
        HashSet<Integer> testIds;
        ArrayList<SampleTestRequestVO> tests;
        ArrayList<TestReflexViewDO> trs;

        tests = null;
        if (results == null || results.size() == 0)
            return tests;

        testIds = new HashSet<Integer>();
        /*
         * find out which tests have been assigned to not cancelled analyses in
         * the sample
         */
        for (i = 0; i < sm.item.count(); i++ ) {
            item = sm.item.get(i);
            for (j = 0; j < sm.analysis.count(item); j++ ) {
                ana = sm.analysis.get(item, j);
                if ( !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
                    testIds.add(ana.getTestId());
            }
        }

        ana = null;
        anaId = null;
        trm = null;
        for (ResultViewDO r : results) {
            if ( !r.getAnalysisId().equals(anaId)) {
                anaId = r.getAnalysisId();
                ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(anaId));
                tm = getTestManager(ana.getTestId());
                trm = tm.getReflexTests();
            }
            /*
             * get the reflex tests triggered by this result
             */
            trs = trm.getReflexListByTestAnalyteIdTestResultId(r.getTestAnalyteId(),
                                                               r.getTestResultId());
            if (trs == null || trs.size() == 0)
                continue;

            /*
             * if a reflex test allows duplication or is not present in the
             * sample then add its information to the returned list
             */
            for (TestReflexViewDO tr : trs) {
                if (Constants.dictionary().REFLEX_PROMPT.equals(tr.getFlagsId()) ||
                    Constants.dictionary().REFLEX_AUTO.equals(tr.getFlagsId())) {
                    allowDup = true;
                } else if ( !testIds.contains(tr.getAddTestId())) {
                    allowDup = false;
                } else {
                    continue;
                }

                if (tests == null)
                    tests = new ArrayList<SampleTestRequestVO>();
                test = new SampleTestRequestVO(sm.getSample().getId(),
                                               ana.getSampleItemId(),
                                               tr.getAddTestId(),
                                               ana.getId(),
                                               null,
                                               r.getId(),                                               
                                               null,
                                               allowDup,
                                               null);
                tests.add(test);
            }
        }
        return tests;
    }
}