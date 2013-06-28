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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used to provide various functionalities related to analyses in
 * a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class AnalysisHelperBean {

    @EJB
    private TestManagerBean testManager;

    /**
     * Returns TestManagers for given test ids. For those tests that are not
     * active, the method looks for the active version of the same tests.
     */
    public HashMap<Integer, TestManager> getTestManagers(ArrayList<Integer> testIds,
                                                         ValidationErrorsList e) throws Exception {
        TestViewDO t;
        ArrayList<TestManager> tms;
        HashMap<Integer, TestManager> map;

        tms = testManager.fetchByIds(testIds);
        map = new HashMap<Integer, TestManager>();
        for (TestManager tm : tms) {
            t = tm.getTest();

            /*
             * if test not active, try to find an active test with this name and
             * method, make sure the old id points to the new manager
             */
            if ("N".equals(t.getIsActive())) {
                try {
                    tm = testManager.fetchActiveByNameMethodName(t.getName(), t.getMethodName());
                    map.put(t.getId(), tm);
                    t = tm.getTest();
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .inactiveTestOnOrderException(t.getName(),
                                                                                     t.getMethodName())));
                    continue;
                }
            }
            map.put(t.getId(), tm);
        }

        return map;
    }

    /**
     * For each test in the VO that passes validation, adds an analysis and
     * results to the manager. Adds validation errors, to the returned VO
     */
    public AnalysisViewDO addAnalysis(SampleManager1 sm, Integer itemId, TestManager tm,
                                      ValidationErrorsList e) throws Exception {
        AnalysisViewDO ana;
        TestViewDO t;
        TestSectionViewDO ts;
        ArrayList<TestTypeOfSampleDO> types;

        t = tm.getTest();
        /*
         * find out if this user has permission to add this test
         */
        ts = tm.getTestSections().getDefaultSection();
        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .insufficientPrivilegesAddTest(t.getName(),
                                                                              t.getMethodName())));
        }

        ana = new AnalysisViewDO();
        ana.setId(sm.getNextUID());
        ana.setRevision(0);
        ana.setTestId(t.getId());
        ana.setTestName(t.getName());
        ana.setMethodId(t.getMethodId());
        ana.setMethodName(t.getMethodName());
        /*
         * if there is a default section then set it
         */
        if (ts != null)
            ana.setSectionId(ts.getSectionId());
        ana.setIsReportable(t.getIsReportable());
        ana.setIsPreliminary("N");

        for (SampleItemViewDO item : getItems(sm)) {
            if (item.getId().equals(itemId)) {
                ana.setSampleItemId(item.getId());
                /*
                 * the first unit within the sample type is the default unit
                 */
                types = tm.getSampleTypes().getTypesBySampleType(item.getTypeOfSampleId());
                if (types.size() > 0)
                    ana.setUnitOfMeasureId(types.get(0).getUnitOfMeasureId());
                break;
            }
        }

        ana.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
        ana.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));

        org.openelis.manager.SampleManager1Accessor.addAnalysis(sm, ana);

        return ana;
    }

    /**
     * Adds results for this analysis from the analytes in the TestManager
     */
    public void addResults(SampleManager1 sm, TestManager tm, Integer analysisId,
                           ArrayList<Integer> analyteIds) throws Exception {
        String reportable;
        boolean addRow;
        ResultViewDO r;
        TestAnalyteManager tam;
        HashSet<Integer> ids;

        ids = null;
        if (analyteIds != null)
            ids = new HashSet<Integer>(analyteIds);

        tam = tm.getTestAnalytes();
        /*
         * By default add analytes that are not supplemental. Add
         * supplemental analytes that are in id list. The id list
         * overrides reportable flag.
         */
        for (ArrayList<TestAnalyteViewDO> list : tam.getAnalytes()) {
            for (TestAnalyteViewDO data : list) {
                reportable = data.getIsReportable();
                if ("N".equals(data.getIsColumn())) {
                    addRow = !Constants.dictionary().TEST_ANALYTE_SUPLMTL.equals(data.getTypeId());
                    if (ids != null) {
                        if (ids.contains(data.getAnalyteId())) {
                            reportable = "Y";
                            addRow = true;
                        } else {
                            reportable = "N";
                        }
                    }
                    
                    if ( !addRow)
                        break;
                }

                r = new ResultViewDO();
                r.setId(sm.getNextUID());
                r.setAnalysisId(analysisId);
                r.setTestAnalyteId(data.getId());
                r.setTestAnalyteTypeId(data.getTypeId());
                r.setIsColumn(data.getIsColumn());
                r.setIsReportable(reportable);
                r.setAnalyteId(data.getAnalyteId());
                r.setAnalyte(data.getAnalyteName());
                r.setResultGroup(data.getResultGroup());
                r.setRowGroup(data.getRowGroup());
                addResult(sm, r);
            }
        }
    }

    /**
     * This method finds and links a prep analysis to the passed analysis. If an
     * existing analysis is not found, the method returns list of prep tests
     * could be added to satisfy the prep requirement.
     */
    public ArrayList<Integer> setPrepForAnalysis(AnalysisViewDO analysis,
                                                 HashMap<Integer, AnalysisViewDO> analyses,
                                                 TestManager tm) throws Exception {
        int i;
        AnalysisViewDO prep;
        TestPrepManager tpm;
        ArrayList<Integer> prepIds;

        /*
         * if this test requires prep tests, first look in the list of existing
         * analyses otherwise add the prep test to the list shown to the user to
         * choose a prep test
         */
        tpm = tm.getPrepTests();
        for (i = 0; i < tpm.count(); i++ ) {
            prep = analyses.get(tpm.getPrepAt(i).getPrepTestId());
            if (prep != null) {
                analysis.setPreAnalysisId(prep.getId());
                analysis.setPreAnalysisTest(prep.getTestName());
                analysis.setPreAnalysisMethod(prep.getMethodName());
                if (Constants.dictionary().ANALYSIS_COMPLETED.equals(prep.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_RELEASED.equals(prep.getStatusId())) {
                    analysis.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                    analysis.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                } else {
                    analysis.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
                }
                return null;
            }
        }

        prepIds = null;
        if (tpm.count() > 0) {
            prepIds = new ArrayList<Integer>();
            for (i = 0; i < tpm.count(); i++ )
                prepIds.add(tpm.getPrepAt(i).getPrepTestId());
        }
        return prepIds;
    }
}