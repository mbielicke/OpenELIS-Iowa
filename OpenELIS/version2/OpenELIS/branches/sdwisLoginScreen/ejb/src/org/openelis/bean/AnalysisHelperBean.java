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
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.MethodDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * This class is used to provide various functionalities related to analyses in
 * a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class AnalysisHelperBean {

    @EJB
    private TestManagerBean     testManager;

    @EJB
    private UserCacheBean       userCache;

    @EJB
    private MethodBean          method;

    @EJB
    private DictionaryCacheBean dictionaryCache;

    @EJB
    private AnalysisBean        analysis;

    @EJB
    private ResultBean          result;

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
        Integer accession;
        AnalysisViewDO ana;
        TestViewDO t;
        TestSectionViewDO ts;
        TestSectionManager tsm;
        ArrayList<TestTypeOfSampleDO> types;

        t = tm.getTest();
        tsm = tm.getTestSections();
        ts = null;

        /*
         * if section id is specified, then use it for checking permissions;
         * otherwise, use the default section
         */
        if (sectionId != null) {
            for (int i = 0; i < tsm.count(); i++ ) {
                ts = tsm.getSectionAt(i);
                if (ts.getSectionId().equals(sectionId))
                    break;
            }
        } else {
            ts = tsm.getDefaultSection();
        }

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        /*
         * find out if this user has permission to add this test
         */
        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .analysis_insufficientPrivilegesAddTestWarning(accession,
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
     * This method changes the specified analysis's method to the specified
     * method. The old results are removed and their values are merged with the
     * results added from the new method. Returns a list of prep tests that
     * could be added to satisfy the prep requirement for the new test.
     */
    public SampleTestReturnVO changeAnalysisMethod(SampleManager1 sm, Integer analysisId,
                                                   Integer methodId) throws Exception {
        int i;
        Integer accession, rowAnaId;
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
        HashMap<Integer, ResultViewDO> row;
        HashMap<Integer, HashMap<Integer, ResultViewDO>> oldResults;

        m = method.fetchById(methodId);
        ana = null;
        /*
         * find the analysis whose method is to be changed
         */
        for (AnalysisViewDO a : getAnalyses(sm)) {
            if (a.getId().equals(analysisId)) {
                ana = a;
                break;
            }
        }

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

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .analysis_insufficientPrivilegesAddTestWarning(accession,
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

        /*
         * find this analysis's sample item
         */
        item = null;
        for (SampleItemViewDO it : getItems(sm)) {
            if (it.getId().equals(ana.getSampleItemId())) {
                item = it;
                break;
            }
        }

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
        prepIds = setPrepForAnalysis(sm, ana, anaByTest, tm);
        if (prepIds != null)
            for (Integer id : prepIds)
                ret.addTest(sm.getSample().getId(),
                            ana.getSampleItemId(),
                            id,
                            ana.getId(),
                            null,
                            null,
                            null,
                            false,
                            null);

        results = getResults(sm);
        oldResults = null;
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
                    if (oldResults == null)
                        oldResults = new HashMap<Integer, HashMap<Integer, ResultViewDO>>();
                    /*
                     * the top level groups analytes and values by their row
                     * analyte
                     */
                    row = oldResults.get(rowAnaId);
                    if (row == null) {
                        row = new HashMap<Integer, ResultViewDO>();
                        oldResults.put(rowAnaId, row);
                    }

                    /*
                     * the next level is used to keep track of the values of
                     * individual analytes
                     */
                    if (row.get(r.getAnalyteId()) == null)
                        row.put(r.getAnalyteId(), r);
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
        addResults(sm, tm, ana, null, oldResults);

        return ret;
    }

    /**
     * This method sets the specified status in the analysis. It also updates
     * any links between other analyses and this one, if need be, because of the
     * change in status. Does not include the current status in the validation
     * for making sure that it can be changed to the specified one.
     */
    public SampleManager1 changeAnalysisStatus(SampleManager1 sm, Integer analysisId,
                                               Integer statusId) throws Exception {
        boolean resultsOverriden, addUser, hasUnreleaseNote;
        Integer accession;
        Datetime now;
        AnalysisViewDO ana;
        AnalysisUserViewDO au;
        SampleItemViewDO item;
        SystemUserVO su;
        SystemUserPermission perm;
        TestManager tm;
        ArrayList<AnalysisViewDO> prepAnas, rflxAnas;
        HashMap<Integer, AnalysisUserViewDO> cmplUsers;
        AnalysisUserViewDO relUser;

        ana = null;
        prepAnas = new ArrayList<AnalysisViewDO>();
        rflxAnas = new ArrayList<AnalysisViewDO>();
        /*
         * find the analysis whose status is to be changed; also, find which
         * analyses have it as their prep and/or reflex analysis
         */
        for (AnalysisViewDO data : getAnalyses(sm)) {
            if (data.getId().equals(analysisId)) {
                ana = data;
            } else {
                if (analysisId.equals(data.getPreAnalysisId()))
                    prepAnas.add(data);
                if (analysisId.equals(data.getParentAnalysisId()))
                    rflxAnas.add(data);
            }
        }

        /*
         * find the analysis' sample item
         */
        item = null;
        for (SampleItemViewDO data : getItems(sm)) {
            if (data.getId().equals(ana.getSampleItemId())) {
                item = data;
                break;
            }
        }

        /*
         * find the users that completed/released the analysis
         */
        cmplUsers = new HashMap<Integer, AnalysisUserViewDO>();
        relUser = null;
        if (getUsers(sm) != null) {
            for (AnalysisUserViewDO data : getUsers(sm)) {
                if ( !analysisId.equals(data.getAnalysisId()))
                    continue;
                if (Constants.dictionary().AN_USER_AC_COMPLETED.equals(data.getActionId()))
                    cmplUsers.put(data.getSystemUserId(), data);
                else if (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId()))
                    relUser = data;
            }
        }

        /*
         * find out if the sample or the analysis has an overriding QA event
         */
        resultsOverriden = false;
        if (getSampleQAs(sm) != null) {
            for (SampleQaEventViewDO sqa : getSampleQAs(sm)) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId())) {
                    resultsOverriden = true;
                    break;
                }
            }
        }

        if ( !resultsOverriden && getAnalysisQAs(sm) != null) {
            for (AnalysisQaEventViewDO aqa : getAnalysisQAs(sm)) {
                if (analysisId.equals(aqa.getAnalysisId()) &&
                    Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId())) {
                    resultsOverriden = true;
                    break;
                }
            }
        }

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        perm = userCache.getPermission();

        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);

        /*
         * change the status to the specified value after performing any
         * necessary checks and change any necessary fields
         */
        if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(statusId)) {
            if (ana.getAvailableDate() == null)
                ana.setAvailableDate(now);
            else if (ana.getStartedDate() != null)
                ana.setStartedDate(null);
        } else if (Constants.dictionary().ANALYSIS_INPREP.equals(statusId)) {
            ana.setAvailableDate(null);
        } else if (Constants.dictionary().ANALYSIS_INITIATED.equals(statusId)) {
            if (ana.getSectionName() == null ||
                !perm.getSection(ana.getSectionName()).hasCompletePermission()) {
                throw new InconsistencyException(Messages.get()
                                                         .analysis_insufficientPrivilegesInitiateException(accession,
                                                                                                           ana.getTestName(),
                                                                                                           ana.getMethodName()));
            }

            if (ana.getStartedDate() == null)
                ana.setStartedDate(now);
        } else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(statusId)) {
            if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId())) {
                /*
                 * the analysis is being completed
                 */
                if (ana.getSectionName() == null ||
                    !perm.getSection(ana.getSectionName()).hasCompletePermission()) {
                    throw new InconsistencyException(Messages.get()
                                                             .analysis_insufficientPrivilegesCompleteException(accession,
                                                                                                               ana.getTestName(),
                                                                                                               ana.getMethodName()));
                }

                /*
                 * we're validating the analysis here as well as the manager
                 * bean, because we want to throw exceptions on some analyses
                 * that can't be completed while allowing others on the same
                 * sample to be completed. This applies to cases when multiple
                 * analyses on the same sample are getting completed.
                 */
                tm = testManager.fetchWithAnalytesAndResults(ana.getTestId());

                try {
                    analysis.validate(ana, tm, sm, item);
                } catch (ValidationErrorsList err) {
                    /*
                     * analysis validate can throw errors, warnings and
                     * cautions; cautions are assumed to be permissible at this
                     * point
                     */
                    if (err.hasWarnings() || err.hasErrors())
                        throw err;
                }

                if ( !resultsOverriden)
                    validateResults(sm, accession, ana, tm.getFormatter());

                /*
                 * if this is the prep analysis of some in-prep analyses then
                 * move them to logged-in
                 */
                for (AnalysisViewDO data : prepAnas) {
                    if (Constants.dictionary().ANALYSIS_INPREP.equals(data.getStatusId())) {
                        data.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                        data.setAvailableDate(now);
                    }
                }

                if (ana.getStartedDate() == null)
                    ana.setStartedDate(now);

                if (ana.getCompletedDate() == null)
                    ana.setCompletedDate(now);

                /*
                 * if the logged in user hasn't already completed this analysis,
                 * add a record for it
                 */
                addUser = true;
                su = perm.getUser();
                if (cmplUsers != null) {
                    au = cmplUsers.get(su.getId());
                    if (au != null)
                        addUser = false;
                }

                if (addUser) {
                    au = new AnalysisUserViewDO();
                    au.setId(sm.getNextUID());
                    au.setAnalysisId(analysisId);
                    au.setSystemUserId(su.getId());
                    au.setSystemUser(su.getLoginName());
                    au.setActionId(Constants.dictionary().AN_USER_AC_COMPLETED);
                    addUser(sm, au);
                }
            } else {
                /*
                 * the analysis is being unreleased
                 */

                if (ana.getSectionName() == null ||
                    !perm.getSection(ana.getSectionName()).hasReleasePermission()) {
                    throw new InconsistencyException(Messages.get()
                                                             .analysis_insufficientPrivilegesUnreleaseException(accession,
                                                                                                                ana.getTestName(),
                                                                                                                ana.getMethodName()));
                }

                /*
                 * the analysis must have an uncommitted internal note for
                 * describing the reason for unreleasing it
                 */
                hasUnreleaseNote = false;
                if (getAnalysisInternalNotes(sm) != null) {
                    for (NoteViewDO data : getAnalysisInternalNotes(sm)) {
                        if (data.getId() < 0 && analysisId.equals(data.getReferenceId())) {
                            hasUnreleaseNote = true;
                            break;
                        }
                    }
                }

                if ( !hasUnreleaseNote)
                    throw new InconsistencyException(Messages.get()
                                                             .sample_unreleaseNoNoteException(accession));

                ana.setReleasedDate(null);
                ana.setPrintedDate(null);
                ana.setRevision(ana.getRevision() + 1);

                /*
                 * increment the sample's revision and blank the released date
                 * if the sample will be unreleased as well
                 */
                if (Constants.dictionary().SAMPLE_RELEASED.equals(getSample(sm).getStatusId())) {
                    getSample(sm).setRevision(getSample(sm).getRevision() + 1);
                    getSample(sm).setReleasedDate(null);
                }

                /*
                 * mark the sample for extra processing e.g. e-save as a result
                 * of the analysis getting unreleased
                 */
                setPostProcessing(sm, SampleManager1.PostProcessing.UNRELEASE);
            }
        } else if (Constants.dictionary().ANALYSIS_RELEASED.equals(statusId)) {
            if (ana.getSectionName() == null ||
                !perm.getSection(ana.getSectionName()).hasReleasePermission()) {
                throw new InconsistencyException(Messages.get()
                                                         .analysis_insufficientPrivilegesReleaseException(accession,
                                                                                                          ana.getTestName(),
                                                                                                          ana.getMethodName()));
            }

            /*
             * can't release the analysis if sample is not verified
             */
            if (Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(getSample(sm).getStatusId())) {
                throw new InconsistencyException(Messages.get()
                                                         .analysis_cantReleaseSampleNotVerifiedException(accession,
                                                                                                         ana.getTestName(),
                                                                                                         ana.getMethodName()));
            }

            tm = testManager.fetchWithAnalytesAndResults(ana.getTestId());

            /*
             * validate the results to make sure that the values of any required
             * analytes were not removed after completing the analysis
             */
            if ( !resultsOverriden)
                validateResults(sm, accession, ana, tm.getFormatter());

            /*
             * if a user released this analysis before then delete that record;
             * create a new one
             */
            if (relUser != null) {
                getUsers(sm).remove(relUser);
                if (getRemoved(sm) == null)
                    setRemoved(sm, new ArrayList<DataObject>());
                getRemoved(sm).add(relUser);
            }

            su = perm.getUser();
            au = new AnalysisUserViewDO();
            au.setId(sm.getNextUID());
            au.setAnalysisId(analysisId);
            au.setSystemUserId(su.getId());
            au.setSystemUser(su.getLoginName());
            au.setActionId(Constants.dictionary().AN_USER_AC_RELEASED);
            addUser(sm, au);
        } else if (Constants.dictionary().ANALYSIS_CANCELLED.equals(statusId)) {
            if (ana.getId() < 0)
                throw new InconsistencyException(Messages.get()
                                                         .analysis_cantCancelUncommitedException(accession,
                                                                                                 ana.getTestName(),
                                                                                                 ana.getMethodName()));

            if (ana.getSectionName() == null ||
                !perm.getSection(ana.getSectionName()).hasCancelPermission()) {
                throw new InconsistencyException(Messages.get()
                                                         .analysis_insufficientPrivilegesCancelException(accession,
                                                                                                         ana.getTestName(),
                                                                                                         ana.getMethodName()));
            }

            /*
             * if the analysis to be cancelled is prep analysis of or was
             * reflexed by any analyses then remove those links
             */
            for (AnalysisViewDO data : prepAnas) {
                /*
                 * the analysis can't be cancelled if it's the prep for any
                 * released analysis
                 */
                if (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId())) {
                    throw new InconsistencyException(Messages.get()
                                                             .analysis_cantCancelPrepWithReleasedTest(accession,
                                                                                                      ana.getTestName(),
                                                                                                      ana.getMethodName(),
                                                                                                      data.getTestName(),
                                                                                                      data.getMethodName()));
                }
                unlinkFromPrep(sm, data);
            }

            for (AnalysisViewDO data : rflxAnas) {
                data.setParentAnalysisId(null);
                data.setParentResultId(null);
            }

            /*
             * if any other analysis is this analysis' prep or was reflexed by
             * it then remove those links
             */
            ana.setPreAnalysisId(null);
            ana.setPreAnalysisTest(null);
            ana.setPreAnalysisMethod(null);
            ana.setParentAnalysisId(null);
            ana.setParentResultId(null);
        }

        /*
         * The status of a completed analysis can be changed to something other
         * than released e.g. by specifying a prep analysis for it. In those
         * cases, the completed date needs to be blanked.
         */
        if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(statusId) &&
            !Constants.dictionary().ANALYSIS_RELEASED.equals(statusId) &&
            ana.getCompletedDate() != null)
            ana.setCompletedDate(null);

        ana.setStatusId(statusId);

        return sm;
    }

    /**
     * This method sets the specified unit in the analysis and loads the
     * defaults defined for this unit in this analysis' results that don't have
     * a value. Sets the type to null in all results of this analysis to force
     * validation.
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
        ana = null;
        /*
         * find the analysis whose unit is to be changed
         */
        for (AnalysisViewDO a : getAnalyses(sm)) {
            if (a.getId().equals(analysisId)) {
                ana = a;
                break;
            }
        }
        ana.setUnitOfMeasureId(unitId);

        results = getResults(sm);
        if (results == null || results.size() == 0)
            return sm;

        tm = testManager.fetchWithAnalytesAndResults(ana.getTestId());
        rf = tm.getFormatter();
        /*
         * set the defaults for this unit in this analysis' results
         */
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

    /**
     * This method sets preAnalysisId as the prep analysis id of the specified
     * analysis. If preAnalysisId is null then the analysis is taken out of
     * in-prep status.
     */
    public SampleManager1 changeAnalysisPrep(SampleManager1 sm, Integer analysisId,
                                             Integer preAnalysisId) throws Exception {
        Integer accession;
        String status;
        AnalysisViewDO ana, prep;

        ana = null;
        prep = null;
        /*
         * find the analysis whose prep analysis is to be changed and also the
         * analysis that is to be set as the prep
         */
        for (AnalysisViewDO a : getAnalyses(sm)) {
            if (a.getId().equals(analysisId))
                ana = a;
            else if (a.getId().equals(preAnalysisId))
                prep = a;
        }

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        /*
         * can't change the prep test of a released or cancelled analysis
         */
        if (Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId())) {
            status = dictionaryCache.getById(ana.getStatusId()).getEntry();
            throw new InconsistencyException(Messages.get()
                                                     .analysis_cantChangePrepException(accession,
                                                                                       ana.getTestName(),
                                                                                       ana.getMethodName(),
                                                                                       status));

        }

        if (preAnalysisId == null) {
            /*
             * remove the link to the prep analysis
             */
            unlinkFromPrep(sm, ana);
        } else {
            if (Constants.dictionary().ANALYSIS_CANCELLED.equals(prep.getStatusId())) {
                throw new InconsistencyException(Messages.get()
                                                         .analysis_cantSetAsPrepException(accession,
                                                                                          prep.getTestName(),
                                                                                          prep.getMethodName(),
                                                                                          ana.getTestName(),
                                                                                          ana.getMethodName()));
            }

            /*
             * create the link to the prep analysis
             */
            setPrepAnalysis(sm, ana, prep);
        }

        return sm;
    }

    /**
     * This method removes the analysis with the specified id and all of its
     * child data, e.g. results, qa events etc. It also removes any links
     * between other analyses and this one. Does not remove a previously
     * committed analysis.
     */
    public SampleManager1 removeAnalysis(SampleManager1 sm, Integer analysisId) throws Exception {
        int i;
        Integer accession;
        AnalysisViewDO ana;
        NoteViewDO note;
        StorageViewDO st;
        ArrayList<AnalysisViewDO> prepAnas, rflxAnas;
        ArrayList<ResultViewDO> results;
        ArrayList<AnalysisQaEventViewDO> qas;
        ArrayList<NoteViewDO> notes;
        ArrayList<StorageViewDO> sts;

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        ana = null;
        prepAnas = new ArrayList<AnalysisViewDO>();
        rflxAnas = new ArrayList<AnalysisViewDO>();
        /*
         * find the analysis to be removed; also, find which analyses have it as
         * their prep and/or reflex analysis
         */
        for (AnalysisViewDO a : getAnalyses(sm)) {
            if (a.getId().equals(analysisId)) {
                ana = a;
            } else {
                if (analysisId.equals(a.getPreAnalysisId()))
                    prepAnas.add(a);
                if (analysisId.equals(a.getParentAnalysisId()))
                    rflxAnas.add(a);
            }
        }

        /*
         * can't remove a previously committed analysis
         */
        if (ana.getId() > 0)
            throw new InconsistencyException(Messages.get()
                                                     .analysis_cantRemoveCommitedException(accession,
                                                                                           ana.getTestName(),
                                                                                           ana.getMethodName()));

        /*
         * if the analysis to be removed is the prep analysis of any analyses
         * then remove those links
         */
        for (AnalysisViewDO a : prepAnas)
            unlinkFromPrep(sm, a);

        /*
         * if the analysis to be removed was reflexed by any analyses then
         * remove those links
         */
        for (AnalysisViewDO a : rflxAnas) {
            a.setParentAnalysisId(null);
            a.setParentResultId(null);
        }

        /*
         * remove the analysis and the child data linked to it
         */
        getAnalyses(sm).remove(ana);

        results = getResults(sm);
        if (results != null) {
            i = 0;
            while (i < results.size()) {
                if (ana.getId().equals(results.get(i).getAnalysisId()))
                    results.remove(i);
                else
                    i++ ;
            }
        }

        qas = getAnalysisQAs(sm);
        if (qas != null) {
            i = 0;
            while (i < qas.size()) {
                if (ana.getId().equals(qas.get(i).getAnalysisId()))
                    qas.remove(i);
                else
                    i++ ;
            }
        }

        notes = getAnalysisInternalNotes(sm);
        if (notes != null) {
            i = 0;
            while (i < notes.size()) {
                note = notes.get(i);
                if (Constants.table().ANALYSIS.equals(note.getReferenceTableId()) &&
                    ana.getId().equals(note.getReferenceId()))
                    notes.remove(i);
                else
                    i++ ;
            }
        }

        notes = getAnalysisExternalNotes(sm);
        if (notes != null) {
            i = 0;
            while (i < notes.size()) {
                note = notes.get(i);
                if (Constants.table().ANALYSIS.equals(note.getReferenceTableId()) &&
                    ana.getId().equals(note.getReferenceId())) {
                    notes.remove(i);
                    /*
                     * an analysis can have only one external note
                     */
                    break;
                } else {
                    i++ ;
                }
            }
        }

        sts = getStorages(sm);
        if (sts != null) {
            i = 0;
            while (i < sts.size()) {
                st = sts.get(i);
                if (Constants.table().ANALYSIS.equals(st.getReferenceTableId()) &&
                    ana.getId().equals(st.getReferenceId())) {
                    sts.remove(i);
                } else {
                    i++ ;
                }
            }
        }

        return sm;
    }

    /**
     * This method finds and links a prep analysis to the passed analysis. If an
     * existing analysis is not found, the method returns list of prep tests
     * that could be added to satisfy the prep requirement.
     */
    public ArrayList<Integer> setPrepForAnalysis(SampleManager1 sm, AnalysisViewDO ana,
                                                 HashMap<Integer, AnalysisViewDO> analyses,
                                                 TestManager tm) throws Exception {
        int i;
        AnalysisViewDO prep;
        TestPrepManager tpm;
        ArrayList<Integer> prepIds;

        /*
         * if this test requires prep tests, first look in the list of existing
         * analyses, otherwise add the prep test to the list shown to the user
         * to choose a prep test
         */
        tpm = tm.getPrepTests();
        for (i = 0; i < tpm.count(); i++ ) {
            prep = analyses.get(tpm.getPrepAt(i).getPrepTestId());
            if (prep != null) {
                setPrepAnalysis(sm, ana, prep);
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
     * Sets the second analysis as the prep of the first. If the prep analysis
     * is completed or released then sets the status of the analytical analysis
     * as logged-in and the available date as the current date-time, otherwise
     * sets those fields to in-prep and null respectively.
     */
    public void setPrepAnalysis(SampleManager1 sm, AnalysisViewDO ana, AnalysisViewDO prep) throws Exception {
        ana.setPreAnalysisId(prep.getId());
        ana.setPreAnalysisTest(prep.getTestName());
        ana.setPreAnalysisMethod(prep.getMethodName());
        if (Constants.dictionary().ANALYSIS_COMPLETED.equals(prep.getStatusId()) ||
            Constants.dictionary().ANALYSIS_RELEASED.equals(prep.getStatusId()))
            changeAnalysisStatus(sm, ana.getId(), Constants.dictionary().ANALYSIS_LOGGED_IN);
        else
            changeAnalysisStatus(sm, ana.getId(), Constants.dictionary().ANALYSIS_INPREP);
    }

    /**
     * Adds results for this analysis from the analytes in the TestManager
     */
    public void addResults(SampleManager1 sm, TestManager tm, AnalysisViewDO ana,
                           ArrayList<Integer> analyteIds,
                           HashMap<Integer, HashMap<Integer, ResultViewDO>> oldResults) throws Exception {
        boolean addRow;
        Integer dictId;
        String reportable, value;
        ResultViewDO oldr;
        TestAnalyteManager tam;
        HashSet<Integer> ids;
        ResultViewDO r;
        ResultFormatter rf;
        HashMap<Integer, ResultViewDO> oldRow;

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
                    oldr = oldRow.get(r.getAnalyteId());
                    /*
                     * if the old results had a value for this analyte then set
                     * it in this result
                     */
                    if (oldr != null && oldr.getValue() != null) {
                        r.setValue(oldr.getValue());
                        r.setTypeId(null);
                        try {
                            /*
                             * validate the old value so that the type gets set
                             */
                            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(oldr.getTypeId())) {
                                dictId = Integer.valueOf(oldr.getValue());
                                value = dictionaryCache.getById(dictId).getEntry();
                            } else {
                                value = oldr.getValue();
                            }
                            ResultHelper.formatValue(r, value, ana.getUnitOfMeasureId(), rf);
                        } catch (Exception e) {
                            // ignore because the value will get validated later
                        }
                    }
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
    public ArrayList<Integer> addRowAnalytes(SampleManager1 sm, AnalysisViewDO ana,
                                             ArrayList<TestAnalyteViewDO> insertAnalytes,
                                             ArrayList<Integer> insertAt) throws Exception {
        int i, j, rpos, pos;
        ArrayList<Integer> positions;
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
        positions = new ArrayList<Integer>();

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
                                                                                                      insertAna.getAnalyteName(),
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
                        getResults(sm).add(j, r);
                        if ("N".equals(ta.getIsColumn()))
                            positions.add(j);
                        j++ ;
                    }
                    break;
                }
            }

            i++ ;
        }

        return positions;
    }

    /**
     * Unlinks the passed analysis from its prep analysis and moves it to
     * logged-in status if it's still in in-prep status
     */
    private void unlinkFromPrep(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
        ana.setPreAnalysisId(null);
        ana.setPreAnalysisTest(null);
        ana.setPreAnalysisMethod(null);
        if (Constants.dictionary().ANALYSIS_INPREP.equals(ana.getStatusId()))
            changeAnalysisStatus(sm, ana.getId(), Constants.dictionary().ANALYSIS_LOGGED_IN);
    }

    /**
     * Creates a new result, links it to the analysis and sets its value as the
     * default defined for its test analyte
     */
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
        r.setAnalyteExternalId(ta.getAnalyteExternalId());
        r.setResultGroup(ta.getResultGroup());
        r.setRowGroup(ta.getRowGroup());
        setDefault(r, ana.getUnitOfMeasureId(), rf);

        return r;
    }

    /**
     * If a default is defined for the result's result group and this unit then
     * sets it as the value; otherwise value is not changed. Validates the
     * default and sets it as the value if it's valid and also sets the type.
     */
    private void setDefault(ResultViewDO r, Integer unitId, ResultFormatter rf) {
        String def;

        if (r.getTypeId() != null)
            r.setTypeId(null);
        def = rf.getDefault(r.getResultGroup(), unitId);
        if (def != null) {
            r.setValue(def);
            try {
                /*
                 * validate the default so that the type gets set
                 */
                ResultHelper.formatValue(r, def, unitId, rf);
            } catch (Exception e) {
                // ignore because the value will get validated later
            }
        }
    }

    /**
     * Validates whether the results of the analysis are valid and all required
     * results have a value
     */
    private void validateResults(SampleManager1 sm, Integer accession, AnalysisViewDO ana,
                                 ResultFormatter rf) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();
        for (ResultViewDO r : getResults(sm)) {
            if ( !ana.getId().equals(r.getAnalysisId()))
                continue;

            if ( !DataBaseUtil.isEmpty(r.getValue())) {
                try {
                    result.validate(r, rf, accession, ana);
                } catch (Exception err) {
                    DataBaseUtil.mergeException(e, err);
                }
            } else if (Constants.dictionary().TEST_ANALYTE_REQ.equals(r.getTestAnalyteTypeId())) {
                e.add(new FormErrorException(Messages.get()
                                                     .result_valueRequiredException(accession,
                                                                                    ana.getTestName(),
                                                                                    ana.getMethodName(),
                                                                                    r.getAnalyte())));
            }
        }

        if (e.size() > 0)
            throw e;
    }
}