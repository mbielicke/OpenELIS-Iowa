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
import org.openelis.domain.DataObject;
import org.openelis.domain.MethodDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;

/**
 * This class is used to provide various functionalities related to analyses in
 * a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class AnalysisHelperBean {

    @EJB
    private SectionCacheBean sectionCache;

    @EJB
    private TestManagerBean  testManager;
    
    @EJB
    private UserCacheBean    userCache;

    @EJB
    private MethodBean       method;

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
                                                       .test_inactiveTestException(t.getName(),
                                                                                   t.getMethodName())));
                    continue;
                }
            }
            map.put(t.getId(), tm);
        }

        return map;
    }

    /**
     * Adds an analysis to the sample item with the given id and sets its test
     * and unit from the test manager. Sets the section id to the passed value
     * if it's not null otherwise sets it to the default section from the test.
     * Adds an error to the list if the section doesn't have permission to add
     * the test.
     */
    public AnalysisViewDO addAnalysis(SampleManager1 sm, Integer sampleItemId, TestManager tm,
                                      Integer sectionId, ValidationErrorsList e) throws Exception {
        AnalysisViewDO ana;
        TestViewDO t;
        TestSectionViewDO ts;
        TestSectionManager tsm;
        ArrayList<TestTypeOfSampleDO> types;

        t = tm.getTest();
        tsm = tm.getTestSections();
        ts = null;

        /*
         * if section id is specified, then use it for checking permissions
         * otherwise use the default section
         */
        if (sectionId != null) {
            for (int i = 0; i < tsm.count(); i++) {
                ts = tsm.getSectionAt(i);
                if (ts.getSectionId().equals(sectionId))
                    break;
            }
        } else {
            ts = tsm.getDefaultSection();
        }

        /*
         * find out if this user has permission to add this test
         */
        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .analysis_insufficientPrivilegesAddTestWarning(getSample(sm).getAccessionNumber(),
                                                                                              t.getName(),
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
         * if a section was found above then set it
         */
        if (ts != null) {
            ana.setSectionId(ts.getSectionId());
            ana.setSectionName(ts.getSection());
        }

        ana.setIsReportable(t.getIsReportable());
        ana.setIsPreliminary("N");

        for (SampleItemViewDO item : getItems(sm)) {
            if (item.getId().equals(sampleItemId)) {
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
    public void addResults(SampleManager1 sm, TestManager tm, AnalysisViewDO ana,
                           ArrayList<Integer> analyteIds,
                           HashMap<Integer, HashMap<Integer, String>> oldResults) throws Exception {
        boolean addRow;
        String reportable, oldVal;
        TestAnalyteManager tam;
        HashSet<Integer> ids;
        ResultViewDO r;
        ResultFormatter rf;
        HashMap<Integer, String> oldRow;

        ids = null;
        if (analyteIds != null)
            ids = new HashSet<Integer>(analyteIds);

        tam = tm.getTestAnalytes();
        rf = tm.getFormatter();
        /*
         * By default add analytes that are not supplemental. Add supplemental
         * analytes that are in id list. The id list overrides reportable flag.
         */
        oldRow = null;
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

                    /*
                     * find out if a row beginning with this analyte was present
                     * in the old results
                     */
                    if (oldResults != null)
                        oldRow = oldResults.get(data.getAnalyteId());
                }

                r = createResult(sm, ana, data, reportable, rf);

                if (oldRow != null && r.getValue() == null) {
                    oldVal = oldRow.get(r.getAnalyteId());
                    /*
                     * if the old results had a value for this analyte then set
                     * it in this result
                     */
                    if (oldVal != null)
                        r.setValue(oldVal);
                }

                addResult(sm, r);
            }
        }
    }

    /**
     * Adds result rows with the specified row analytes at the positions
     * specified by the corresponding indexes. Assumes that the two lists are of
     * the same length and the indexes are in ascending order.
     */
    public SampleManager1 addRowAnalytes(SampleManager1 sm, AnalysisViewDO ana,
                                         ArrayList<TestAnalyteViewDO> insertAnalytes,
                                         ArrayList<Integer> insertAt) throws Exception {
        int i, j, rpos, pos;
        Integer lastrg, nextrg;
        TestAnalyteViewDO insertAna;
        ResultViewDO r;
        TestManager tm;
        TestAnalyteManager tam;
        ResultFormatter rf;

        i = 0;
        j = 0;
        rpos = -1;
        lastrg = null;
        nextrg = null;

        tm = testManager.fetchWithAnalytesAndResults(ana.getTestId());
        tam = tm.getTestAnalytes();
        rf = tm.getFormatter();

        while (i < insertAnalytes.size()) {
            /*
             * the next position to insert
             */
            pos = insertAt.get(i);
            insertAna = insertAnalytes.get(i);

            while (j < getResults(sm).size() && pos != rpos) {
                /*
                 * skip the results of other analyses
                 */
                r = getResults(sm).get(j);
                if ( !r.getAnalysisId().equals(ana.getId())) {
                    j++ ;
                    continue;
                }

                /*
                 * this is the start of the next row
                 */
                if ("N".equals(r.getIsColumn())) {
                    rpos++ ;
                    lastrg = nextrg;
                }

                nextrg = r.getRowGroup();
                j++ ;
            }

            /*
             * the row group of analyte must be same as the previous or the next
             * row
             */
            if ( !insertAna.getRowGroup().equals(lastrg) && !insertAna.getRowGroup().equals(nextrg))
                throw new InconsistencyException(Messages.get()
                                                         .analysis_invalidPositionForAnalyteException(getSample(sm).getAccessionNumber(),
                                                                                                      ana.getTestName(),
                                                                                                      ana.getMethodName()));

            if (j != getResults(sm).size())
                j-- ;

            /*
             * find the row and column analytes in the test
             */
            for (ArrayList<TestAnalyteViewDO> tas : tam.getAnalytes()) {
                if (tas.get(0).getId().equals(insertAna.getId())) {
                    /*
                     * create results from this row and add them to the analysis
                     */
                    for (TestAnalyteViewDO ta : tas) {
                        r = createResult(sm, ana, ta, ta.getIsReportable(), rf);
                        getResults(sm).add(j++ , r);
                    }
                    break;
                }
            }

            i++ ;
        }

        return sm;
    }

    /**
     * This method finds and links a prep analysis to the passed analysis. If an
     * existing analysis is not found, the method returns list of prep tests
     * that could be added to satisfy the prep requirement.
     */
    public ArrayList<Integer> setPrepForAnalysis(AnalysisViewDO ana,
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
                ana.setPreAnalysisId(prep.getId());
                ana.setPreAnalysisTest(prep.getTestName());
                ana.setPreAnalysisMethod(prep.getMethodName());
                if (Constants.dictionary().ANALYSIS_COMPLETED.equals(prep.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_RELEASED.equals(prep.getStatusId())) {
                    ana.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                    ana.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                } else {
                    ana.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
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

    /**
     * This method changes the specified analysis's method to the specified
     * method. The old results are removed and their values are merged with the
     * results added from the new method. Returns a list of prep tests that could
     * be added to satisfy the prep requirement for the new test.
     */
    public SampleTestReturnVO changeMethod(SampleManager1 sm, Integer analysisId,
                                                   Integer methodId) throws Exception {
        int i;
        Integer rowAnaId;
        TestViewDO t;
        MethodDO m;
        AnalysisViewDO ana;
        ResultViewDO r;
        SampleItemViewDO item;
        TestManager tm;
        TestSectionViewDO ts;
        TestSectionManager tsm;
        SampleTestReturnVO ret;
        ValidationErrorsList e;
        ArrayList<Integer> prepIds;
        ArrayList<TestTypeOfSampleDO> types;
        ArrayList<DataObject> removed;
        ArrayList<ResultViewDO> results;
        HashMap<Integer, AnalysisViewDO> anaByTest;
        HashMap<Integer, String> row;
        HashMap<Integer, HashMap<Integer, String>> rows;

        m = method.fetchById(methodId);
        ana = (AnalysisViewDO)sm.getObject(sm.getAnalysisUid(analysisId));

        try {
            tm = testManager.fetchActiveByNameMethodName(ana.getTestName(), m.getName());
            t = tm.getTest();
        } catch (NotFoundException ex) {
            throw new InconsistencyException(Messages.get()
                                                     .test_inactiveTestException(ana.getTestName(),
                                                                                 m.getName()));
        }

        ana.setTestId(t.getId());
        ana.setTestName(t.getName());
        ana.setMethodId(t.getMethodId());
        ana.setMethodName(t.getMethodName());

        e = new ValidationErrorsList();
        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ret.setErrors(e);

        tsm = tm.getTestSections();
        ts = tsm.getDefaultSection();

        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .analysis_insufficientPrivilegesAddTestWarning(getSample(sm).getAccessionNumber(),
                                                                                              t.getName(),
                                                                                              t.getMethodName())));
        }

        if (ts != null) {
            ana.setSectionId(ts.getSectionId());
            ana.setSectionName(ts.getSection());
        } else {
            ana.setSectionId(null);
            ana.setSectionName(null);
        }

        item = (SampleItemViewDO)sm.getObject(sm.getSampleItemUid(ana.getSampleItemId()));
        /*
         * the first unit within the sample type is the default unit
         */
        types = tm.getSampleTypes().getTypesBySampleType(item.getTypeOfSampleId());
        if (types.size() > 0)
            ana.setUnitOfMeasureId(types.get(0).getUnitOfMeasureId());
        else
            ana.setUnitOfMeasureId(null);

        anaByTest = new HashMap<Integer, AnalysisViewDO>();
        for (AnalysisViewDO a : getAnalyses(sm)) {
            if (Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId()))
                continue;

            /*
             * create a mapping between test ids and analyses to determine
             * whether any prep tests for the new test are present in the sample
             */
            if (anaByTest.get(a.getTestId()) == null)
                anaByTest.put(a.getTestId(), a);

            /*
             * reset the prep test and method names of the analyses that have
             * this analysis as their prep analysis
             */
            if (ana.getId().equals(a.getPreAnalysisId())) {
                a.setPreAnalysisTest(t.getName());
                a.setPreAnalysisMethod(t.getMethodName());
            }
        }

        /*
         * find and set the prep needed for the new test from the sample or
         * create a list of prep tests
         */
        prepIds = setPrepForAnalysis(ana, anaByTest, tm);
        if (prepIds != null)
            for (Integer id : prepIds)
                ret.addTest(ana.getSampleItemId(), id, ana.getId(), null, null, null, false, null);

        results = getResults(sm);
        rows = null;
        if (results != null) {
            row = null;
            removed = getRemoved(sm);
            rowAnaId = null;
            i = 0;
            while (i < results.size()) {
                r = results.get(i);
                if ( !ana.getId().equals(r.getAnalysisId())) {
                    i++ ;
                    continue;
                }

                if ("N".equals(r.getIsColumn()))
                    rowAnaId = r.getAnalyteId();

                /*
                 * to merge old results with the new test's results create a two
                 * level hash; create the hash only if at least one result has a
                 * value
                 */
                if (r.getValue() != null) {
                    if (rows == null)
                        rows = new HashMap<Integer, HashMap<Integer, String>>();
                    /*
                     * the top level groups analytes and values by their row
                     * analyte
                     */
                    row = rows.get(rowAnaId);
                    if (row == null) {
                        row = new HashMap<Integer, String>();
                        rows.put(rowAnaId, row);
                    }

                    /*
                     * the next level is used to keep to track of the values of
                     * individual analytes
                     */
                    if (row.get(r.getAnalyteId()) == null)
                        row.put(r.getAnalyteId(), r.getValue());
                }

                /*
                 * remove the old result
                 */
                if (r.getId() > 0) {
                    if (removed == null) {
                        removed = new ArrayList<DataObject>();
                        setRemoved(sm, removed);
                    }
                    removed.add(r);
                }

                results.remove(i);
            }
        }

        /*
         * add the results from the new test and merge them with the old ones
         */
        addResults(sm, tm, ana, null, rows);

        return ret;
    }

    /**
     * Loads the defaults, defined in this analysis' test, in the results of the
     * analysis. Doesn't change the existing values if a default is not defined.
     * Sets the type to null in all results of the analysis to force validation.
     */
    public SampleManager1 changeAnalysisUnit(SampleManager1 sm, Integer analysisId, Integer unitId) throws Exception {
        ResultViewDO r;
        AnalysisViewDO ana;
        TestManager tm;
        ResultFormatter rf;
        SampleTestReturnVO ret;
        ArrayList<ResultViewDO> results;

        ret = new SampleTestReturnVO();
        ret.setManager(sm);
        ana = (AnalysisViewDO)sm.getObject(sm.getAnalysisUid(analysisId));
        ana.setUnitOfMeasureId(unitId);

        results = getResults(sm);
        if (results == null || results.size() == 0)
            return sm;

        tm = testManager.fetchWithAnalytesAndResults(ana.getTestId());
        rf = tm.getFormatter();
        for (int i = 0; i < results.size(); i++ ) {
            r = results.get(i);
            if ( !r.getAnalysisId().equals(ana.getId())) {
                i++ ;
                continue;
            }

            setDefault(r, unitId, rf);
        }

        return sm;
    }

    private ResultViewDO createResult(SampleManager1 sm, AnalysisViewDO ana, TestAnalyteViewDO ta,
                                      String reportable, ResultFormatter rf) {
        ResultViewDO r;

        r = new ResultViewDO();
        r.setId(sm.getNextUID());
        r.setAnalysisId(ana.getId());
        r.setTestAnalyteId(ta.getId());
        r.setTestAnalyteTypeId(ta.getTypeId());
        r.setIsColumn(ta.getIsColumn());
        r.setIsReportable(reportable);
        r.setAnalyteId(ta.getAnalyteId());
        r.setAnalyte(ta.getAnalyteName());
        r.setResultGroup(ta.getResultGroup());
        r.setRowGroup(ta.getRowGroup());
        setDefault(r, ana.getUnitOfMeasureId(), rf);

        return r;
    }

    /**
     * If a default is defined for the result's result group and this unit then
     * sets it as the value; otherwise value is not changed. Sets the type to
     * null in both cases to force validation.
     */
    private void setDefault(ResultViewDO r, Integer unitId, ResultFormatter rf) {
        String def;

        def = rf.getDefault(r.getResultGroup(), unitId);
        if (def != null)
            r.setValue(def);
        r.setTypeId(null);
    }

    public void initiateAnalysis(AnalysisViewDO data) throws Exception {
        SystemUserPermission perm;
        SectionViewDO section;

        assert data.getSectionId() != null : "section id is null";

        // make sure the status is not released, cancelled, or in prep
        if (Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_INPREP.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId()))
            throw new FormErrorException(Messages.get().wrongStatusNoInitiate());

        // make sure the user has complete permission for the section
        section = sectionCache.getById(data.getSectionId());
        perm = userCache.getPermission();
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCompletePermission())
            throw new FormErrorException(Messages.get()
                                                 .insufficientPrivilegesInitiateAnalysis(data.getTestName(),
                                                                                         data.getMethodName()));

        if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_ON_HOLD.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_REQUEUE.equals(data.getStatusId()))
            data.setStatusId(Constants.dictionary().ANALYSIS_INITIATED);
        else if (Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(data.getStatusId()))
            data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_INITIATED);

        if (data.getStartedDate() == null)
            data.setStartedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
    }
}