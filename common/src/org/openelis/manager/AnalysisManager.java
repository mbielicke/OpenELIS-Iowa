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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;

import com.google.gwt.user.client.Window;

public class AnalysisManager implements RPC {
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
        old.setStatusId(proxy().anInPrepId);
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
        data.setStatusId(proxy().anLoggedInId);

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
        AnalysisViewDO data;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        SystemUserPermission perm;

        perm = proxy().getSystemUserPermission();
        data = items.get(index).analysis;
        section = proxy().getSectionFromId(data.getSectionId());

        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasCancelPermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesCancelAnalysis",
                                                  data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        try {
            data.setStatusId(proxy().anCancelledId);
            SectionCache.getSectionFromId(data.getSectionId());
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

        data  = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        //make sure the status is not released, cancelled, or in prep
        if (proxy().anErrorInPrepId.equals(data.getStatusId()) || proxy().anInPrepId.equals(data.getStatusId()) ||
            proxy().anReleasedId.equals(data.getStatusId()) || proxy().anCancelledId.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("wrongStatusNoInitiate"));
            throw errorsList;
        }

        //make sure the user has complete permission for the section
        section = proxy().getSectionFromId(data.getSectionId());
        perm = proxy().getSystemUserPermission();
        if (perm.getSection(section.getName()) == null || !perm.getSection(section.getName()).hasCompletePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesInitiateAnalysis", data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        //validate the sample type
        testMan = getTestAt(index);
        bundle = getBundleAt(index);
        if (!testMan.getSampleTypes().hasType(sampleItemManager.getSampleItemAt(bundle.getSampleItemIndex()).getTypeOfSampleId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("sampleTypeInvalid", data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        if (proxy().anLoggedInId.equals(data.getStatusId()))
            data.setStatusId(proxy().anInitiatedId);
        else if (proxy().anErrorLoggedInId.equals(data.getStatusId()))
            data.setStatusId(proxy().anErrorInitiatedId);

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
        // make sure the status is initiated, on hold or logged-in
        //
        if (!proxy().anOnHoldId.equals(data.getStatusId()) &&
            !proxy().anInitiatedId.equals(data.getStatusId()) &&            
            !proxy().anLoggedInId.equals(data.getStatusId())) { 
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
                                                  data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        // validate the sample type
        testMan = getTestAt(index);
        bundle = getBundleAt(index);
        if ( !testMan.getSampleTypes().hasType(sampleItemManager.getSampleItemAt(bundle.getSampleItemIndex())
                                               .getTypeOfSampleId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("sampleTypeInvalid", data.getTestName(),
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

        data.setStatusId(proxy().anCompletedId);
        if (data.getStartedDate() == null)
            data.setStartedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

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
        SectionViewDO section;
        SystemUserPermission perm;
        ValidationErrorsList errorsList;

        data = items.get(index).analysis;
        assert data.getSectionId() != null : "section id is null";

        // make sure the status is completed
        if (proxy().anReleasedId.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("analysisAlreadyReleased"));
            throw errorsList;
        } else if ( !proxy().anCompletedId.equals(data.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("completeStatusRequiredToRelease",
                                                  data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        // make sure the user has release permission for the section
        perm = proxy().getSystemUserPermission();
        section = proxy().getSectionFromId(data.getSectionId());
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasReleasePermission()) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesReleaseAnalysis",
                                                  data.getTestName(), data.getMethodName()));
            throw errorsList;
        }

        data.setStatusId(proxy().anReleasedId);
        data.setReleasedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        try {
            // add an analysis user record
            getAnalysisUserAt(index).addReleaseRecord();

        } catch (Exception e) {
            // do nothing
        }
    }

    public void unreleaseAnalysisAt(int index) throws Exception {
        AnalysisViewDO data;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        SampleManager man;
        SystemUserPermission perm;
        NoteManager internalNotes; 
        AnalysisListItem item;
        
        item = getItemAt(index);

        data = item.analysis;        
        internalNotes = getInternalNotesAt(index);
        errorsList = new ValidationErrorsList();
        
        if ( !proxy().anReleasedId.equals(data.getStatusId()))             
            errorsList.add(new FormErrorException("wrongStatusUnrelease"));                   
        
        // make sure the user has release permission for the section
        perm = proxy().getSystemUserPermission();
        section = proxy().getSectionFromId(data.getSectionId());
        if (perm.getSection(section.getName()) == null ||
            !perm.getSection(section.getName()).hasReleasePermission()) 
            errorsList.add(new FormErrorException("insufficientPrivilegesUnreleaseAnalysis",
                                                  data.getTestName(), data.getMethodName()));        

        if (internalNotes == null || !internalNotes.hasEditingNote()) 
            errorsList.add(new FormErrorException("unreleaseNoNoteException"));
        
        if (errorsList.size() > 0)
            throw errorsList;
            
        data.setStatusId(proxy().anCompletedId);
        data.setReleasedDate(null);
        data.setPrintedDate(null);
        data.setRevision(data.getRevision() + 1);
        
        man = sampleItemBundle.getSampleManager();
        if (proxy().sampleReleasedId.equals(man.getSample().getStatusId()))
            man.unrelease();                        
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
                                          sampleItemManager.sampleManager, sampleItemBundle, index);
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
    public int add(HashMap<Integer, Integer> idHash) throws Exception {
        return proxy().add(this, idHash);
    }

    public int update(HashMap<Integer, Integer> idHash) throws Exception {
        return proxy().update(this, idHash);
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(String sampleItemSequence,
                         Integer sampleTypeId,
                         String sampleDomain,
                         ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, sampleItemSequence, sampleTypeId, sampleDomain, errorsList);
    }

    public void setDefaultsAt(int index) {
        AnalysisViewDO analysis;

        try {
            analysis = getItemAt(index).analysis;
            analysis.setId(sampleItemManager.getNextTempId());
            analysis.setStatusId(proxy().anLoggedInId);
            analysis.setRevision(0);
            analysis.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
        } catch (Exception e) {
            // ignore
        }
    }

    public AnalysisListItem getItemAt(int i) {
        return items.get(i);
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
                    item.analysisInternalNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ANALYSIS,
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
                    item.analysisExternalNote = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ANALYSIS,
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
                    item.storages = StorageManager.fetchByRefTableRefId(ReferenceTable.ANALYSIS,
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
                        item.analysisResult = AnalysisResultManager.fetchForUpdateWithAnalysisId(item.analysis.getId(),
                                                                                                 item.analysis.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.analysis.getTestId() != null) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchForUpdateWithTestId(item.analysis.getTestId(),
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

    public boolean hasAnalysisResultsAt(int index) {
        return getItemAt(index).analysisResult != null;
    }

    public void setAnalysisResultAt(AnalysisResultManager analysisResult, int i) {
        getItemAt(i).analysisResult = analysisResult;
    }

    // test
    public void setTestAt(TestManager testMan, int index) {
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
        if (data.getIsReportable() == null)
            data.setIsReportable(test.getIsReportable());

        // if there is only 1 unit then set it
        units = null;
        try {
            typeOfSampleId = sampleItemManager.getSampleItemAt(getBundleAt(index).getSampleItemIndex())
                                            .getTypeOfSampleId();
            units = testMan.getSampleTypes().getTypesBySampleType(typeOfSampleId);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        if (units.size() == 1)
            data.setUnitOfMeasureId(units.get(0).getUnitOfMeasureId());

        // if there is a default section then set it
        defaultSect = null;
        try {
            defaultSect = testMan.getTestSections().getDefaultSection();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        if (defaultSect != null)
            data.setSectionId(defaultSect.getSectionId());

        // set preanalyses data
        preAnalysisList = getPreAnalysisList(data.getId());

        for (int i = 0; i < preAnalysisList.size(); i++ ) {
            prevData = preAnalysisList.get(i);
            prevData.setPreAnalysisTest(test.getName());
            prevData.setPreAnalysisMethod(test.getMethodName());
        }

        // merge results if needed
        if (getItemAt(index).analysisResult != null && data.getTestName().equals(oldTestName) &&
            !data.getMethodName().equals(oldMethodName)) {
            try {
                mergeAt(index, data.getTestId());
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    protected void updateAnalysisStatusAt(int index, Integer sampleTypeId) throws Exception {
        TestManager testMan;
        boolean error;
        AnalysisViewDO data;
        Integer currentStatusId;

        data = getItemAt(index).analysis;

        // if the analysis is cancelled stop now
        if (proxy().anCancelledId.equals(data.getStatusId()))
            return;

        testMan = getTestAt(index);
        currentStatusId = data.getStatusId();
        error = false;
        
        // sample item needs to match
        if ( !testMan.getSampleTypes().hasType(sampleTypeId))
            error = true;

        if (error) {
            if (currentStatusId.equals(proxy().anLoggedInId))
                data.setStatusId(proxy().anErrorLoggedInId);
            else if (currentStatusId.equals(proxy().anInitiatedId))
                data.setStatusId(proxy().anErrorInitiatedId);
            else if (currentStatusId.equals(proxy().anCompletedId))
                data.setStatusId(proxy().anErrorCompletedId);
            else if (currentStatusId.equals(proxy().anInPrepId))
                data.setStatusId(proxy().anErrorInPrepId);
        } else {
            if (currentStatusId.equals(proxy().anErrorLoggedInId))
                data.setStatusId(proxy().anLoggedInId);
            else if (currentStatusId.equals(proxy().anErrorInitiatedId))
                data.setStatusId(proxy().anInitiatedId);
            else if (currentStatusId.equals(proxy().anErrorCompletedId))
                data.setStatusId(proxy().anCompletedId);
            else if (currentStatusId.equals(proxy().anErrorInPrepId))
                data.setStatusId(proxy().anInPrepId);
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

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AnalysisListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }

    boolean hasReleasedAnalysis() {
        boolean released;
        AnalysisViewDO an;

        released = false;
        for (int i = 0; i < count(); i++ ) {
            an = getItemAt(i).analysis;

            if (proxy().anReleasedId.equals(an.getStatusId())) {
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

    static class AnalysisListItem implements RPC {
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