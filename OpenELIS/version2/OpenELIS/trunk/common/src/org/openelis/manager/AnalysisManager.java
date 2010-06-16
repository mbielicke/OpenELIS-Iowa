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
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.ValidationErrorsList;

import com.google.gwt.user.client.Window;

public class AnalysisManager implements RPC {
    private static final long                       serialVersionUID = 1L;

    protected Integer                               sampleItemId;
    protected transient Integer                     anLoggedInId, anInitiatedId, anCompletedId,
                    anReleasedId, anInPrepId, anOnHoldId, anRequeueId, anCancelledId,
                    anErrorLoggedInId, anErrorInitiatedId, anErrorInPrepId, anErrorCompletedId;

    protected ArrayList<AnalysisListItem>           items, deletedList;
    protected SampleDataBundle                      sampleItemBundle;
    protected SampleItemManager                     sampleItemManager;
    protected transient static AnalysisManagerProxy proxy;

    public static AnalysisManager getInstance() {
        AnalysisManager am;

        am = new AnalysisManager();
        am.items = new ArrayList<AnalysisListItem>();

        return am;
    }

    // analysis
    public AnalysisViewDO getAnalysisAt(int i) {
        return getItemAt(i).analysis;

    }

    public ArrayList<AnalysisViewDO> getAnalysisList() {
        ArrayList<AnalysisViewDO> returnList;
        
        returnList = new ArrayList<AnalysisViewDO>();
        for(int i=0; i<count(); i++)
            returnList.add(getItemAt(i).analysis);
        
        return returnList;

    }
    
    public void setAnalysisAt(AnalysisViewDO analysis, int i) {
        getItemAt(i).analysis = analysis;
    }

    public int addAnalysis() {
        AnalysisListItem item;
        AnalysisViewDO analysis;

        item = new AnalysisListItem();
        analysis = new AnalysisViewDO();

        item.analysis = analysis;
        items.add(item);
        
        setDefaultsAt(count()-1);

        return count() - 1;
    }

    /**
     * Adds an empty prep analysis and links the actual analysis with the 
     * newly added prep test.
     * 
     * @param linkIndex Is the index of the actual analysis that is going to be linked
     * to the new prep analysis
     * @return
     */
    public int addPreAnalysis(int linkIndex) {
        int addedIndex;
        AnalysisViewDO prep, old;

        addedIndex = addAnalysis();
        prep = getAnalysisAt(addedIndex);
        old = getAnalysisAt(linkIndex);

        // set the old analysis parameters according to new prep
        old.setStatusId(anInPrepId);
        old.setAvailableDate(null);
        old.setPreAnalysisId(prep.getId());

        return addedIndex;
    }

    /**
     * Unlinks the actual analysis from the prep analysis.  The actual analysis
     * is updated with status 'Logged In' and an available data and time is set.
     * @param index
     */
    public void unlinkPrepTest(int index) {
        AnalysisViewDO anDO;

        anDO = getItemAt(index).analysis;
        anDO.setPreAnalysisId(null);
        anDO.setPreAnalysisTest(null);
        anDO.setPreAnalysisMethod(null);
        anDO.setStatusId(anLoggedInId);
        
        try{
            anDO.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
        }catch(Exception e){
            anDO.setAvailableDate(null);
        }
    }
    
    public int addReflexAnalysis(Integer parentAnalysisId, Integer parentResultId) {
        int addedIndex;
        AnalysisViewDO reflex;

        addedIndex = addAnalysis();
        reflex = getAnalysisAt(addedIndex);

        // set the reflex analysis parent values
        reflex.setParentAnalysisId(parentAnalysisId);
        reflex.setParentResultId(parentResultId);

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
        AnalysisViewDO anDO;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        SecurityUtil security;
        
        security = proxy().getSecurityUtil();
        anDO  = items.get(index).analysis;
        section = proxy().getSectionFromId(anDO.getSectionId());
        
        if(security.getSection(section.getName()) == null || !security.getSection(section.getName()).hasCancelPermission()){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesCancelAnalysis", anDO.getTestName(), anDO.getMethodName()));
            throw errorsList;
        }
        
        try {
            loadDictionaryEntries();
            anDO.setStatusId(anCancelledId);
            SectionCache.getSectionFromId(anDO.getSectionId());
            anDO.setPreAnalysisId(null);
            
        } catch (Exception e) {
            return;
        }
    }
    
    public void completeAnalysisAt(int index) throws Exception {
        AnalysisViewDO anDO;
        SecurityUtil security;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        
        anDO  = items.get(index).analysis;
        assert anDO.getSectionId() != null : "section id is null";
        loadDictionaryEntries();
        
        if(anRequeueId.equals(anDO.getStatusId())){ //make sure the status is not requeue
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("requeueStatusNoComplete"));
            throw errorsList;
        }else if(anCompletedId.equals(anDO.getStatusId())){ //make sure status is not already complete
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("analysisAlreadyComplete"));
            throw errorsList;
        }
        
        //make sure the user has complete permission for the section
        section = proxy().getSectionFromId(anDO.getSectionId());
        security = proxy().getSecurityUtil();
        if(security.getSection(section.getName()) == null || !security.getSection(section.getName()).hasCompletePermission()){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesCompleteAnalysis", anDO.getTestName(), anDO.getMethodName()));
            throw errorsList;
        }
        
        //if the sample/analysis has an overriding QA event go ahead and complete the analysis
        //make sure all required results are filled, and all results are valid
        //this method will throw an exception if it finds and error
        if(!getQAEventAt(index).hasResultOverrideQA() && !getSampleItemManager().getSampleManager().getQaEvents().hasResultOverrideQA())
            getAnalysisResultAt(index).validateForComplete(anDO);
        
        anDO.setStatusId(anCompletedId);
        if(anDO.getStartedDate() == null)
            anDO.setStartedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        anDO.setCompletedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        try{
            //add an analysis user record
            getAnalysisUserAt(index).addCompleteRecord();
            
        }catch(Exception e){
            //do nothing
        }
        
    }
    
    public void releaseAnalyssisAt(int index) throws Exception {
        AnalysisViewDO anDO;
        SecurityUtil security;
        SectionViewDO section;
        ValidationErrorsList errorsList;
        
        anDO  = items.get(index).analysis;
        assert anDO.getSectionId() != null : "section id is null";
        loadDictionaryEntries();
        
        //make sure the status is completed
        if(anReleasedId.equals(anDO.getStatusId())){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("analysisAlreadyReleased"));
            throw errorsList;
        }else if(!anCompletedId.equals(anDO.getStatusId())){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("completeStatusRequiredToRelease", anDO.getTestName(), anDO.getMethodName()));
            throw errorsList;
        }
        
        //make sure the user has release permission for the section
        security = proxy().getSecurityUtil();
        section = proxy().getSectionFromId(anDO.getSectionId());
        if(security.getSection(section.getName()) == null || !security.getSection(section.getName()).hasReleasePermission()){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("insufficientPrivilegesReleaseAnalysis", anDO.getTestName(), anDO.getMethodName()));
            throw errorsList;
        }
        
        anDO.setStatusId(anReleasedId);
        anDO.setReleasedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));

        try{
            //add an analysis user record
            getAnalysisUserAt(index).addReleaseRecord();
            
        }catch(Exception e){
            //do nothing
        }
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
            loadDictionaryEntries();
            analysis = getItemAt(index).analysis;
            analysis.setId(sampleItemManager.getNextTempId());
            analysis.setStatusId(anLoggedInId);
            analysis.setRevision(0);
            analysis.setAvailableDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
        } catch (Exception e) {
            // ignore
        }
    }

    // item
    public AnalysisListItem getItemAt(int i) {
        return items.get(i);
    }

    //
    // other managers
    //
    // qaevents
    public AnalysisQaEventManager getQAEventAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);
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
        AnalysisListItem item = getItemAt(i);
        if (item.analysisInternalNotes == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisInternalNotes = NoteManager.fetchByRefTableRefIdIsExt(
                                                                                       ReferenceTable.ANALYSIS,
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
        AnalysisListItem item = getItemAt(i);
        if (item.analysisExternalNote == null) {
            if (item.analysis != null && item.analysis.getId() != null) {
                try {
                    item.analysisExternalNote = NoteManager.fetchByRefTableRefIdIsExt(
                                                                                      ReferenceTable.ANALYSIS,
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
        AnalysisListItem item = getItemAt(i);

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
    
    //analysis user
    public AnalysisUserManager getAnalysisUserAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

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
        AnalysisListItem item = getItemAt(i);

        if (item.analysisResult == null) {
            if (item.analysis != null) {
                if (item.analysis.getId() != null && item.analysis.getId() > 0) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchForUpdateWithAnalysisId(
                                                                                   item.analysis.getId(),
                                                                                   item.analysis.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.analysis.getTestId() != null) {
                    try {
                        item.analysisResult = AnalysisResultManager.fetchForUpdateWithTestId(item.analysis.getTestId(), item.analysis.getUnitOfMeasureId());
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
        AnalysisListItem item = getItemAt(i);

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
    
    public boolean hasAnalysisResultsAt(int index){ 
        return getItemAt(index).analysisResult != null;
    }
    
    public void setAnalysisResultAt(AnalysisResultManager analysisResult, int i) {
        getItemAt(i).analysisResult = analysisResult;
    }

    // test
    public void setTestAt(TestManager testMan, int index) {
        TestViewDO test;
        AnalysisViewDO anDO, preAnDO;
        ArrayList<TestTypeOfSampleDO> units;
        TestSectionViewDO defaultDO;
        Integer typeOfSample;
        ArrayList<AnalysisViewDO> preAnalysisList;
        String oldTestName, oldMethodName;
        
        getItemAt(index).tests = testMan;
        anDO = getItemAt(index).analysis;
        test = testMan.getTest();
        
        oldTestName = anDO.getTestName();
        oldMethodName = anDO.getMethodName();
        
        anDO.setTestId(test.getId());
        anDO.setTestName(test.getName());
        anDO.setMethodId(test.getMethodId());
        anDO.setMethodName(test.getMethodName());
        anDO.setIsReportable(test.getIsReportable());

        // if there is only 1 unit then set it
        units = null;
        try {
            typeOfSample = sampleItemManager.getSampleItemAt(getBundleAt(index).getSampleItemIndex()).getTypeOfSampleId();
            units = testMan.getSampleTypes().getTypesBySampleType(typeOfSample);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        if (units.size() == 1)
            anDO.setUnitOfMeasureId(units.get(0).getUnitOfMeasureId());

        // if there is a default section then set it
        defaultDO = null;
        try {
            defaultDO = testMan.getTestSections().getDefaultSection();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        if (defaultDO != null)
            anDO.setSectionId(defaultDO.getSectionId());
        
        //set preanalyses data
        preAnalysisList = getPreAnalysisList(anDO.getId());
        
        for(int i=0; i<preAnalysisList.size(); i++){
            preAnDO = preAnalysisList.get(i);
            preAnDO.setPreAnalysisTest(test.getName());
            preAnDO.setPreAnalysisMethod(test.getMethodName());
        }
        
        //merge results if needed
        if(getItemAt(index).analysisResult != null && 
                        anDO.getTestName().equals(oldTestName) && 
                        !anDO.getMethodName().equals(oldMethodName)){
            try{
                mergeAt(index, anDO.getTestId());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void removeTestAt(int index){
        AnalysisListItem item;
        AnalysisViewDO anDO, preAnDO;
        ArrayList<AnalysisViewDO> preAnalysisList;
        
        item = getItemAt(index);
        
        item.tests = null;
        item.analysisResult = null;
        anDO = item.analysis;
        
        anDO.setTestId(null);
        anDO.setTestName(null);
        anDO.setMethodId(null);
        anDO.setMethodName(null);
        anDO.setIsReportable(null);
        anDO.setUnitOfMeasureId(null);
        anDO.setSectionId(null);
        
      //set preanalyses data
        preAnalysisList = getPreAnalysisList(anDO.getId());
        
        for(int i=0; i<preAnalysisList.size(); i++){
            preAnDO = preAnalysisList.get(i);
            preAnDO.setPreAnalysisTest(null);
            preAnDO.setPreAnalysisMethod(null);
        }
    }

    public TestManager getTestAt(int i) throws Exception {
        AnalysisListItem item = getItemAt(i);

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
    public Integer getAnalysisIdByTestId(Integer testId) {
        Integer id = null;

        for (int i = 0; i < count(); i++ ) {

        }

        return id;
    }

    protected void updateAnalysisStatusAt(int index, Integer sampleTypeId) throws Exception {
        TestManager testMan;
        boolean error = false;
        AnalysisViewDO anDO;
        Integer currentStatus;

        loadDictionaryEntries();
        anDO = getItemAt(index).analysis;

        // if the analysis is cancelled stop now
        if (anCancelledId.equals(anDO.getStatusId()))
            return;

        testMan = getTestAt(index);
        currentStatus = anDO.getStatusId();

        // sample item needs to match
        if ( !testMan.getSampleTypes().hasType(sampleTypeId))
            error = true;

        if (error) {
            if (currentStatus.equals(anLoggedInId))
                anDO.setStatusId(anErrorLoggedInId);
            else if (currentStatus.equals(anInitiatedId))
                anDO.setStatusId(anErrorInitiatedId);
            else if (currentStatus.equals(anCompletedId))
                anDO.setStatusId(anErrorCompletedId);
            else if (currentStatus.equals(anInPrepId))
                anDO.setStatusId(anErrorInPrepId);
        } else {
            if (currentStatus.equals(anErrorLoggedInId))
                anDO.setStatusId(anLoggedInId);
            else if (currentStatus.equals(anErrorInitiatedId))
                anDO.setStatusId(anInitiatedId);
            else if (currentStatus.equals(anErrorCompletedId))
                anDO.setStatusId(anCompletedId);
            else if (currentStatus.equals(anErrorInPrepId))
                anDO.setStatusId(anInPrepId);
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

            if (anReleasedId.equals(an.getStatusId())) {
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

    private ArrayList<AnalysisViewDO> getPreAnalysisList(Integer preAnalysisId){
        ArrayList<AnalysisViewDO> returnList;
        AnalysisViewDO anDO;
        
        returnList = new ArrayList<AnalysisViewDO>();
        for(int i=0; i<count(); i++){
            anDO = getItemAt(i).analysis;
            
            if(preAnalysisId.equals(anDO.getPreAnalysisId()))
                returnList.add(anDO);
        }
        
        return returnList;
    }

    private void loadDictionaryEntries() throws Exception {
        if (anLoggedInId == null) {
            anLoggedInId = proxy().getIdFromSystemName("analysis_logged_in");
            anInitiatedId = proxy().getIdFromSystemName("analysis_initiated");
            anCompletedId = proxy().getIdFromSystemName("analysis_completed");
            anReleasedId = proxy().getIdFromSystemName("analysis_released");
            anInPrepId = proxy().getIdFromSystemName("analysis_inprep");
            anOnHoldId = proxy().getIdFromSystemName("analysis_on_hold");
            anRequeueId = proxy().getIdFromSystemName("analysis_requeue");
            anCancelledId = proxy().getIdFromSystemName("analysis_cancelled");
            anErrorLoggedInId = proxy().getIdFromSystemName("analysis_error_logged_in");
            anErrorInitiatedId = proxy().getIdFromSystemName("analysis_error_initiated");
            anErrorInPrepId = proxy().getIdFromSystemName("analysis_error_inprep");
            anErrorCompletedId = proxy().getIdFromSystemName("analysis_error_completed");
        }
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