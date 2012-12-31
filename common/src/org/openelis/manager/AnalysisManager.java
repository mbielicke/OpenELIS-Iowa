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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;

public class AnalysisManager implements Serializable {
    private static final long                       serialVersionUID = 1L;

    protected Integer                               sampleItemId;
    protected ArrayList<AnalysisListItem>           items, deletedList;
    protected SampleDataBundle                      sampleItemBundle;
    protected SampleItemManager                     sampleItemManager;

    protected transient static AnalysisManagerProxy proxy;

    public static AnalysisManager getInstance() {
        AnalysisManager m;

        m = new AnalysisManager();
        m.items = new ArrayList<AnalysisListItem>();

        return m;
    }

    public AnalysisViewDO getAnalysisAt(int i) {
        return getItemAt(i).analysis;
    }

    public ArrayList<AnalysisViewDO> getAnalysisList() {
        ArrayList<AnalysisViewDO> l;

        l = new ArrayList<AnalysisViewDO>();
        for (int i = 0; i < count(); i++ )
            l.add(getItemAt(i).analysis);

        return l;

    }

    public void setAnalysisAt(AnalysisViewDO analysis, int i) {
        getItemAt(i).analysis = analysis;
    }

    public int addAnalysis() {
        AnalysisListItem i;
        AnalysisViewDO a;

        i = new AnalysisListItem();
        a = new AnalysisViewDO();

        i.analysis = a;
        items.add(i);
        setDefaultsAt(count() - 1);

        return count() - 1;
    }

    /**
     * Adds an empty prep analysis and links the actual analysis with the newly
     * added prep test.
     * 
     * @param linkIndex
     *        Is the index of the actual analysis that is going to be linked to
     *        the new prep analysis
     * @return
     */
    public int addPreAnalysis(int linkIndex) {
        int i;
        AnalysisViewDO prep, old;

        i = addAnalysis();
        prep = getAnalysisAt(i);
        old = getAnalysisAt(linkIndex);

        // set the old analysis parameters according to new prep
        old.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
        old.setAvailableDate(null);
        old.setPreAnalysisId(prep.getId());

        return i;
    }

    /**
     * Unlinks the actual analysis from the prep analysis. The actual analysis
     * is updated with status 'Logged In' and an available data and time is set.
     * 
     * @param index
     */
    public void unlinkPrepTest(int index) {
        AnalysisViewDO data;

        data = getItemAt(index).analysis;
        data.setPreAnalysisId(null);
        data.setPreAnalysisTest(null);
        data.setPreAnalysisMethod(null);
        data.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);

        try {
            data.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
        } catch (Exception e) {
            data.setAvailableDate(null);
        }
    }

    public int addReflexAnalysis(Integer parentAnalysisId, Integer parentResultId) {
        int addedIndex;
        AnalysisViewDO data;

        addedIndex = addAnalysis();
        data = getAnalysisAt(addedIndex);

        // set the reflex analysis parent values
        data.setParentAnalysisId(parentAnalysisId);
        data.setParentResultId(parentResultId);

        return addedIndex;
    }

    public void removeAnalysisAt(int index) {
        AnalysisListItem tmpList;
        SampleDataBundle bundle;

        if (items == null || index >= items.size())
            return;

        tmpList = items.remove(index);

        // renumber sample bundle analyses indexes
        // when a node is removed
        for (int i = index; i < items.size(); i++ ) {
            bundle = items.get(i).bundle;

            if (bundle != null)
                bundle.setIndex(i);
        }

        if (deletedList == null)
            deletedList = new ArrayList<AnalysisListItem>();

        if (tmpList.analysis.getId() != null && tmpList.analysis.getId() >= 0)
            deletedList.add(tmpList);
    }

    public void cancelAnalysisAt(int index) throws Exception {
        int i, j;
        AnalysisViewDO data, checkData;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        SystemUserPermission perm;
        SampleDataBundle bundle;
        SampleItemManager siMan;
        AnalysisManager aMan;

        perm = proxy().getSystemUserPermission();
        data = items.get(index).analysis;
        section = proxy().getSectionFromId(data.getSectionId());

        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCancelPermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesCancelAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }
        
        //
        // check to see if any released analyses on this sample link to this analysis
        // for prep
        //
        bundle = items.get(index).bundle;
        siMan = bundle.getSampleManager().getSampleItems();
        for (i = 0; i < siMan.count(); i++) {
            aMan = siMan.getAnalysisAt(i);
            for (j = 0; j < aMan.count(); j++) {
                checkData = aMan.getAnalysisAt(j);
                if (data.getId().equals(checkData.getPreAnalysisId()) &&
                                Constants.dictionary().ANALYSIS_RELEASED.equals(checkData.getStatusId())) {
                    errorsList = new ValidationErrorsList();
                    errorsList.add(new FormErrorException("noCancelPrepWithReleasedTest",
                                                          data.getTestName(), data.getMethodName(),
                                                          checkData.getTestName(), checkData.getMethodName()));
                    throw errorsList;
                }
            }
        }

        try {
            data.setStatusId(Constants.dictionary().ANALYSIS_CANCELLED);
            SectionCache.getById(data.getSectionId());
            data.setPreAnalysisId(null);

        } catch (Exception e) {
            return;
        }
    }

    public void initiateAnalysisAt(int index) throws Exception {
        AnalysisViewDO data;
        SystemUserPermission perm;
        SectionViewDO section;
        TestManager testMan;
        SampleDataBundle bundle;
        ValidationErrorsList errorsList;

        data = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        // make sure the status is not released, cancelled, or in prep
        if (Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_INPREP.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("wrongStatusNoInitiate"));
            throw errorsList;
        }

        // make sure the user has complete permission for the section
        section = proxy().getSectionFromId(data.getSectionId());
        perm = proxy().getSystemUserPermission();
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCompletePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesInitiateAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // validate the sample type
        testMan = getTestAt(index);
        bundle = getBundleAt(index);
        if ( !testMan.getSampleTypes()
                     .hasType(sampleItemManager.getSampleItemAt(bundle.getSampleItemIndex())
                                               .getTypeOfSampleId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("sampleTypeInvalid",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(data.getStatusId()) ||
            Constants.dictionary().ANALYSIS_REQUEUE.equals(data.getStatusId()))
            data.setStatusId(Constants.dictionary().ANALYSIS_INITIATED);
        else if (Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN.equals(data.getStatusId()))
            data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_INITIATED);

        if (data.getStartedDate() == null)
            data.setStartedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
    }

    public void completeAnalysisAt(int index) throws Exception {
        AnalysisViewDO data;
        SystemUserPermission perm;
        SectionViewDO section;
        TestManager testMan;
        SampleDataBundle bundle;
        ValidationErrorsList errorsList;

        data = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        //
        // make sure the status is completed, initiated, on hold or logged-in
        //
        if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(data.getStatusId()) &&
            !Constants.dictionary().ANALYSIS_ON_HOLD.equals(data.getStatusId()) &&
            !Constants.dictionary().ANALYSIS_INITIATED.equals(data.getStatusId()) &&
            !Constants.dictionary().ANALYSIS_LOGGED_IN.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("wrongStatusNoComplete"));
            throw errorsList;

        }

        // make sure the user has complete permission for the section
        section = proxy().getSectionFromId(data.getSectionId());
        perm = proxy().getSystemUserPermission();
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCompletePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesCompleteAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // validate the sample type
        testMan = getTestAt(index);
        bundle = getBundleAt(index);
        if ( !testMan.getSampleTypes()
                     .hasType(sampleItemManager.getSampleItemAt(bundle.getSampleItemIndex())
                                               .getTypeOfSampleId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("sampleTypeInvalid",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // if the sample/analysis has an overriding QA event go ahead and
        // complete the analysis
        // make sure all required results are filled, and all results are valid
        // this method will throw an exception if it finds and error
        if ( !getQAEventAt(index).hasResultOverrideQA() &&
            !getSampleItemManager().getSampleManager().getQaEvents().hasResultOverrideQA())
            getAnalysisResultAt(index).validateForComplete(data);

        data.setStatusId(Constants.dictionary().ANALYSIS_COMPLETED);
        if (data.getStartedDate() == null)
            data.setStartedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        if (data.getCompletedDate() == null)
            data.setCompletedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        try {
            // add an analysis user record
            getAnalysisUserAt(index).addCompleteRecord();

        } catch (Exception e) {
            // do nothing
        }

    }

    public void releaseAnalysisAt(int index) throws Exception {
        AnalysisViewDO data;
        SampleDO sample;
        SectionViewDO section;
        SystemUserPermission perm;
        ValidationErrorsList errorsList;

        data = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        // make sure the status is completed
        if (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("analysisAlreadyReleased"));
            throw errorsList;
        } else if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("completeStatusRequiredToRelease",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // sample must not be in 'Not Verified' status
        sample = items.get(index).bundle.getSampleManager().getSample();
        if (Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(sample.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("sampleNotVerifiedForAnalysisRelease",
                                                  sample.getAccessionNumber().toString(),
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // make sure the user has release permission for the section
        perm = proxy().getSystemUserPermission();
        section = proxy().getSectionFromId(data.getSectionId());
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasReleasePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesReleaseAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        data.setStatusId(Constants.dictionary().ANALYSIS_RELEASED);
        // data.setReleasedDate(proxy().getCurrentDatetime(Datetime.YEAR,
        // Datetime.MINUTE));

        // add an analysis user record
        getAnalysisUserAt(index).addReleaseRecord();
    }

    public void unreleaseAnalysisAt(int index) throws Exception {
        AnalysisViewDO data;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        SampleManager man;
        SystemUserPermission perm;
        NoteManager intenotes, samNotes;
        AnalysisListItem item;

        item = getItemAt(index);

        data = item.analysis;
        intenotes = getInternalNotesAt(index);
        errorsList = new ValidationErrorsList();

        if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
            errorsList.add(new FormErrorException("wrongStatusUnrelease"));

        // make sure the user has release permission for the section
        perm = proxy().getSystemUserPermission();
        section = proxy().getSectionFromId(data.getSectionId());
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasReleasePermission())
            errorsList.add(new FormErrorException("insufficientPrivilegesUnreleaseAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));

        if (intenotes == null || !intenotes.hasEditingNote())
            errorsList.add(new FormErrorException("unreleaseNoNoteException"));

        if (errorsList.size() > 0)
            throw errorsList;

        data.setStatusId(Constants.dictionary().ANALYSIS_COMPLETED);
        data.setReleasedDate(null);
        data.setPrintedDate(null);
        data.setRevision(data.getRevision() + 1);

        // every unreleased sample needs an internal comment describing the
        // reason
        man = sampleItemBundle.getSampleManager();
        if (Constants.dictionary().SAMPLE_RELEASED.equals(man.getSample().getStatusId()))
            man.unrelease(false);
    }

    public void unInitiateAnalysisAt(int index, boolean clearStartedDate) throws Exception {
        AnalysisViewDO data;
        SampleDataBundle bundle;
        SectionViewDO section;
        SystemUserPermission perm;
        TestManager testMan;
        ValidationErrorsList errorsList;
        
        data = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        // make sure the status is initiated
        if (!Constants.dictionary().ANALYSIS_INITIATED.equals(data.getStatusId()) &&
            !Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(data.getStatusId()))
            return;

        // make sure the user has complete permission for the section
        section = proxy().getSectionFromId(data.getSectionId());
        perm = proxy().getSystemUserPermission();
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCompletePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesUnInitiateAnalysis",
                                                  data.getTestName(),
                                                  data.getMethodName()));
            throw errorsList;
        }

        // validate the sample type and set the status
        testMan = getTestAt(index);
        bundle = getBundleAt(index);
        if (!testMan.getSampleTypes().hasType(sampleItemManager.getSampleItemAt(bundle.getSampleItemIndex())
                                                               .getTypeOfSampleId()))
            data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN);
        else
            data.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
        
        if (clearStartedDate)
            data.setStartedDate(null);
    }
    
    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }

    public SampleDataBundle getBundleAt(int index) {
        SampleDataBundle bundle;

        bundle = getItemAt(index).bundle;
        if (bundle == null) {
            bundle = new SampleDataBundle(SampleDataBundle.Type.ANALYSIS,
                                          sampleItemManager.sampleManager,
                                          sampleItemBundle,
                                          index);
            getItemAt(index).bundle = bundle;
        }

        return bundle;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use
     * this function to load an instance of this object from database.
     */
    public static AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return proxy().fetchBySampleItemId(sampleItemId);
    }

    // service methods
    public int update(HashMap<Integer, Integer> idHash) throws Exception {
        return proxy().update(this, idHash);
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(String sampleItemSequence, Integer sampleTypeId, String sampleDomain,
                         ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, sampleItemSequence, sampleTypeId, sampleDomain, errorsList);
    }

    public void setDefaultsAt(int index) {
        AnalysisViewDO analysis;

        try {
            analysis = getItemAt(index).analysis;
            analysis.setId(sampleItemManager.getNextTempId());
            analysis.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
            analysis.setRevision(0);
            analysis.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
        } catch (Exception e) {
            // ignore
        }
    }

    public AnalysisListItem getItemAt(int i) {
        return items.get(i);
    }

    void addItem(AnalysisListItem item) {
        items.add(item);
        item.bundle = new SampleDataBundle(SampleDataBundle.Type.ANALYSIS,
                                           sampleItemManager.sampleManager,
                                           sampleItemBundle,
                                           items.size() - 1);
    }

    //
    // other managers
    //
    // qaevents
    public AnalysisQaEventManager getQAEventAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.qaEvents == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.qaEvents = AnalysisQaEventManager.fetchByAnalysisId(item.analysis.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.qaEvents == null)
                item.qaEvents = AnalysisQaEventManager.getInstance();
        }

        return item.qaEvents;
    }

    public void setQAEventAt(AnalysisQaEventManager qaEvent, int i) {
        getItemAt(i).qaEvents = qaEvent;
    }

    // internal notes
    public NoteManager getInternalNotesAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.analysisInternalNotes == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisInternalNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().ANALYSIS,
                                                                                       item.analysis.getId(),
                                                                                       false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.analysisInternalNotes == null) {
                item.analysisInternalNotes = NoteManager.getInstance();
                item.analysisInternalNotes.setIsExternal(false);
            }
        }

        return item.analysisInternalNotes;
    }

    public void setInternalNotes(NoteManager notes, int i) {
        getItemAt(i).analysisInternalNotes = notes;
    }

    // external note
    public NoteManager getExternalNoteAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.analysisExternalNote == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisExternalNote = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().ANALYSIS,
                                                                                      item.analysis.getId(),
                                                                                      true);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.analysisExternalNote == null) {
                item.analysisExternalNote = NoteManager.getInstance();
                item.analysisExternalNote.setIsExternal(true);
            }
        }

        return item.analysisExternalNote;
    }

    public void setExternalNoteAt(NoteManager note, int i) {
        getItemAt(i).analysisExternalNote = note;
    }

    // storage
    public StorageManager getStorageAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.storages == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.storages = StorageManager.fetchByRefTableRefId(Constants.table().ANALYSIS,
                                                                        item.analysis.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.storages == null)
                item.storages = StorageManager.getInstance();
        }

        return item.storages;
    }

    public void setStorageAt(StorageManager storage, int i) {
        getItemAt(i).storages = storage;
    }

    public AnalysisUserManager getAnalysisUserAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.analysisUsers == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisUsers = AnalysisUserManager.fetchByAnalysisId(item.analysis.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.analysisUsers == null)
                item.analysisUsers = AnalysisUserManager.getInstance();
        }

        return item.analysisUsers;
    }

    public void setAnalysisUserAt(AnalysisUserManager analysisUser, int i) {
        getItemAt(i).analysisUsers = analysisUser;
    }

    // analysis test result
    public AnalysisResultManager getAnalysisResultAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.analysisResult == null) {
            if (item.analysis != null) {
                if (item.analysis.getId() != null && item.analysis.getId() > 0) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchByAnalysisId(item.analysis.getId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.analysis.getTestId() != null) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchByTestId(item.analysis.getTestId(),
                                                                                  item.analysis.getUnitOfMeasureId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }

            if (item.analysisResult == null)
                item.analysisResult = AnalysisResultManager.getInstance();
        }

        return item.analysisResult;
    }

    public AnalysisResultManager getDisplayAnalysisResultAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.analysisResult == null) {
            if (item.analysis != null) {
                if (item.analysis.getId() != null && item.analysis.getId() > -1) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchByAnalysisId(item.analysis.getId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }

            if (item.analysisResult == null)
                item.analysisResult = AnalysisResultManager.getInstance();
        }

        return item.analysisResult;
    }

    public boolean hasAnalysisResultsAt(int index) throws Exception {
        return getAnalysisResultAt(index).rowCount() > 0;
    }

    public void setAnalysisResultAt(AnalysisResultManager analysisResult, int i) {
        getItemAt(i).analysisResult = analysisResult;
    }

    // test
    public void setTestAt(TestManager testMan, int index, boolean forOrderImport) throws Exception {
        TestViewDO test;
        AnalysisViewDO data, prevData;
        ArrayList<TestTypeOfSampleDO> units;
        TestSectionViewDO defaultSect;
        Integer typeOfSampleId;
        ArrayList<AnalysisViewDO> preAnalysisList;
        String oldTestName, oldMethodName;

        getItemAt(index).tests = testMan;
        data = getItemAt(index).analysis;
        test = testMan.getTest();

        oldTestName = data.getTestName();
        oldMethodName = data.getMethodName();

        data.setTestId(test.getId());
        data.setTestName(test.getName());
        data.setMethodId(test.getMethodId());
        data.setMethodName(test.getMethodName());
        data.setUnitOfMeasureId(null);
        data.setSectionId(null);
        if (data.getIsReportable() == null)
            data.setIsReportable(test.getIsReportable());

        // set the first unit for this sample type if there are any units for it
        typeOfSampleId = sampleItemManager.getSampleItemAt(getBundleAt(index).getSampleItemIndex())
                                          .getTypeOfSampleId();
        units = testMan.getSampleTypes().getTypesBySampleType(typeOfSampleId);
        if (units.size() > 0)
            data.setUnitOfMeasureId(units.get(0).getUnitOfMeasureId());

        // if there is a default section then set it
        defaultSect = testMan.getTestSections().getDefaultSection();
        if (defaultSect != null)
            data.setSectionId(defaultSect.getSectionId());

        // set preanalyses data
        preAnalysisList = getPreAnalysisList(data.getId());
        for (int i = 0; i < preAnalysisList.size(); i++ ) {
            prevData = preAnalysisList.get(i);
            prevData.setPreAnalysisTest(test.getName());
            prevData.setPreAnalysisMethod(test.getMethodName());
        }
        
        if ( !forOrderImport) {
            /*
             * merge the analysis if same test but different method; wipe &
             * reload the results if not saved analysis test and method both
             * changed
             */
            if ( !DataBaseUtil.isSame(data.getTestName(), oldTestName) ||
                getItemAt(index).analysisResult == null)
                getItemAt(index).analysisResult = AnalysisResultManager.fetchByTestId(data.getTestId(),
                                                                                      data.getUnitOfMeasureId());
            else if ( !DataBaseUtil.isSame(data.getMethodName(), oldMethodName))
                mergeAt(index, data.getTestId());
        } else {
            /*
             * When the results for a test added from an order, there are no previous
             * results with which they can be merged. Also, the results included 
             * supplemental analytes.
             */
            getItemAt(index).analysisResult = AnalysisResultManager.fetchByTestIdForOrderImport(data.getTestId(),
                                                                                          data.getUnitOfMeasureId());
        }
    }

    public void removeTestAt(int index) {
        AnalysisListItem item;
        AnalysisViewDO data, preData;
        ArrayList<AnalysisViewDO> preAnalysisList;

        item = getItemAt(index);

        item.tests = null;
        item.analysisResult = null;
        data = item.analysis;

        data.setTestId(null);
        data.setTestName(null);
        data.setMethodId(null);
        data.setMethodName(null);
        data.setIsReportable(null);
        data.setUnitOfMeasureId(null);
        data.setSectionId(null);

        // set preanalyses data
        preAnalysisList = getPreAnalysisList(data.getId());

        for (int i = 0; i < preAnalysisList.size(); i++ ) {
            preData = preAnalysisList.get(i);
            preData.setPreAnalysisTest(null);
            preData.setPreAnalysisMethod(null);
        }
    }

    public TestManager getTestAt(int i) throws Exception {
        AnalysisListItem item;

        item = getItemAt(i);
        if (item.tests == null) {
            if (item.analysis != null && item.analysis.getTestId() != null) {
                try {
                    item.tests = TestManager.fetchWithPrepTestsSampleTypes(item.analysis.getTestId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (item.tests == null)
                item.tests = TestManager.getInstance();
        }

        return item.tests;
    }

    //
    // helper methods
    //

    protected void updateAnalysisStatusAt(int itemIndex, int anaIndex, Integer sampleTypeId) throws Exception {
        int i;
        TestManager testMan;
        boolean error;
        AnalysisViewDO data;
        Integer currentStatusId;
        SampleDataBundle bundle;
        SampleItemManager siMan;
        AnalysisManager anaMan;

        data = getItemAt(anaIndex).analysis;

        // if the analysis is cancelled stop now
        if (Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId()))
            return;

        testMan = getTestAt(anaIndex);
        currentStatusId = data.getStatusId();

        //
        // if this analysis' status is "In Prep" then we have to check to see
        // whether its prep analysis' status has been changed to "Completed"
        // and if that is the case then this analysis's status should be set to
        // "Logged In"
        //

        if (currentStatusId.equals(Constants.dictionary().ANALYSIS_COMPLETED)) {
            bundle = getBundleAt(anaIndex);
            siMan = bundle.sampleManager.getSampleItems();
            for (i = 0; i < siMan.count(); i++ ) {
                anaMan = bundle.sampleManager.getSampleItems().getAnalysisAt(i);
                for (AnalysisViewDO temp : anaMan.getAnalysisList()) {
                    if (DataBaseUtil.isSame(temp.getPreAnalysisId(), data.getId()) &&
                        temp.getStatusId().equals(Constants.dictionary().ANALYSIS_INPREP)) {
                        temp.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                        temp.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
                    }
                }
            }
        }

        error = false;

        // sample item needs to match
        if ( !testMan.getSampleTypes().hasType(sampleTypeId))
            error = true;

        if (error) {
            if (currentStatusId.equals(Constants.dictionary().ANALYSIS_LOGGED_IN))
                data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_INITIATED))
                data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_INITIATED);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_COMPLETED))
                data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_COMPLETED);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_INPREP))
                data.setStatusId(Constants.dictionary().ANALYSIS_ERROR_INPREP);
        } else {
            if (currentStatusId.equals(Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN))
                data.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_ERROR_INITIATED))
                data.setStatusId(Constants.dictionary().ANALYSIS_INITIATED);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_ERROR_COMPLETED))
                data.setStatusId(Constants.dictionary().ANALYSIS_COMPLETED);
            else if (currentStatusId.equals(Constants.dictionary().ANALYSIS_ERROR_INPREP))
                data.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
        }
    }

    protected void removeAnalysisAtNoDelete(int index) {
        SampleDataBundle bundle;

        if (items == null || index >= items.size())
            return;

        items.remove(index);

        // renumber sample bundle analyses indexes
        // when a node is removed
        for (int i = index; i < items.size(); i++ ) {
            bundle = items.get(i).bundle;

            if (bundle != null)
                bundle.setIndex(i);
        }
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getSampleItemId() {
        return sampleItemId;
    }

    void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    SampleItemManager getSampleItemManager() {
        return sampleItemManager;
    }

    void setSampleItemManager(SampleItemManager sampleItemManager) {
        this.sampleItemManager = sampleItemManager;
    }

    SampleDataBundle getSampleItemBundle() {
        return sampleItemBundle;
    }

    void setSampleItemBundle(SampleDataBundle sampleItemBundle) {
        this.sampleItemBundle = sampleItemBundle;
    }

    void setTestManagerWithResultAt(TestManager man, Integer analysisId, int index) throws Exception {
        getItemAt(index).tests = man;
        try {
            getItemAt(index).analysisResult = AnalysisResultManager.fetchByAnalysisId(analysisId);
        } catch (NotFoundException ignE) {
            // not possible unless they really wanted no result
        }
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AnalysisListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }

    public boolean hasReleasedAnalysis() {
        boolean released;
        AnalysisViewDO an;

        released = false;
        for (int i = 0; i < count(); i++ ) {
            an = getItemAt(i).analysis;

            if (Constants.dictionary().ANALYSIS_RELEASED.equals(an.getStatusId())) {
                released = true;
                break;
            }
        }

        return released;
    }

    int addAnalysis(AnalysisViewDO analysis) {
        AnalysisListItem item;

        item = new AnalysisListItem();
        item.analysis = analysis;
        items.add(item);

        return items.size() - 1;
    }

    private void mergeAt(int index, Integer testId) throws Exception {
        AnalysisListItem item;
        AnalysisResultManager resultMan;

        item = getItemAt(index);
        resultMan = getAnalysisResultAt(index);
        resultMan.setMergeTestId(testId);
        resultMan.setMergeUnitId(item.analysis.getUnitOfMeasureId());

        try {
            item.analysisResult = AnalysisResultManager.merge(resultMan);
        } catch (NotFoundException e) {
            // ignore
        } catch (Exception e) {
            throw e;
        }
    }

    private ArrayList<AnalysisViewDO> getPreAnalysisList(Integer preAnalysisId) {
        ArrayList<AnalysisViewDO> returnList;
        AnalysisViewDO data;

        returnList = new ArrayList<AnalysisViewDO>();
        for (int i = 0; i < count(); i++ ) {
            data = getItemAt(i).analysis;

            if (preAnalysisId.equals(data.getPreAnalysisId()))
                returnList.add(data);
        }

        return returnList;
    }

    private static AnalysisManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisManagerProxy();

        return proxy;
    }

    static class AnalysisListItem implements Serializable {
        private static final long serialVersionUID = 1L;

        AnalysisViewDO            analysis;
        AnalysisResultManager     analysisResult;
        AnalysisQaEventManager    qaEvents;
        NoteManager               analysisInternalNotes, analysisExternalNote;
        StorageManager            storages;
        AnalysisUserManager       analysisUsers;
        TestManager               tests;
        SampleDataBundle          bundle;
    }
}