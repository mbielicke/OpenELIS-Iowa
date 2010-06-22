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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleManager implements RPC, HasAuxDataInt {
    private static final long                     serialVersionUID = 1L;

    protected SampleDO                            sample;
    protected SampleItemManager                   sampleItems;
    protected SampleOrganizationManager           organizations;
    protected SampleProjectManager                projects;
    protected SampleQaEventManager                qaEvents;
    protected SampleDomainInt                     sampleDomain;
    protected NoteManager                         sampleInternalNotes;
    protected NoteManager                         sampleExternalNote;
    protected AuxDataManager                      auxData;
    protected SampleDataBundle                    bundle;

    protected transient Integer                   anLoggedInId, anInitiatedId, anCompletedId,
                    anReleasedId, anInPrepId, anOnHoldId, anRequeueId, anCancelledId,
                    anErrorLoggedInId, anErrorInitiatedId, anErrorInPrepId, anErrorCompletedId,
                    samLoggedInId, samCompletedId, samReleasedId, samErrorId;
    
    protected transient boolean unreleased;

    public static final String                    ENVIRONMENTAL_DOMAIN_FLAG = "E",
                                                  HUMAN_DOMAIN_FLAG = "H", 
                                                  ANIMAL_DOMAIN_FLAG = "A", 
                                                  NEWBORN_DOMAIN_FLAG = "N",
                                                  PT_DOMAIN_FLAG = "P", 
                                                  SDWIS_DOMAIN_FLAG = "S", 
                                                  WELL_DOMAIN_FLAG = "W",
                                                  QUICK_ENTRY = "Q";

    protected transient static SampleManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected SampleManager() {
        sample = null;
        sampleItems = null;
        organizations = null;
        projects = null;
        sampleDomain = null;
    }

    /**
     * Creates a new instance of this object. A default sample object is also
     * created.
     */
    public static SampleManager getInstance() {
        SampleManager sm;
        sm = new SampleManager();
        sm.sample = new SampleDO();
                              
        return sm;
    }
    
    public SampleDO getSample() {
        return sample;
    }

    public void setSample(SampleDO sample) {
        this.sample = sample;
    }

    /**
     * Creates a new instance of this object with the specified Sample. Use
     * this function to load an instance of this object from database.
     */
    public static SampleManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    /**
     * Creates a new instance of this object with the specified Sample. Use
     * this function to load an instance of this object from database.
     */
    public static SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return proxy().fetchByAccessionNumber(accessionNumber);
    }

    public static SampleManager fetchWithItemsAnalyses(Integer id) throws Exception {
        return proxy().fetchWithItemsAnalyses(id);
    }

    public SampleManager add() throws Exception {
        updateSampleAnalysesStatuses();
        return proxy().add(this);
    
    }

    public SampleManager update() throws Exception {
        updateSampleAnalysesStatuses();
        return proxy().update(this);
    
    }

    public SampleManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(sample.getId());
    }

    public SampleManager abortUpdate() throws Exception {
        return proxy().abortUpdate(sample.getId());
    }

    public void validate() throws Exception {
        ValidationErrorsList errorList = new ValidationErrorsList();
        proxy().validate(this, errorList);
    
        if (errorList.size() > 0)
            throw errorList;
    }

    public SampleDataBundle getBundle() {
        if(bundle == null)
            bundle = new SampleDataBundle(SampleDataBundle.Type.SAMPLE, this, null, -1);

        return bundle;
    }
    
    public void setDefaults() {
        Datetime yToM;
        
        try{
            yToM = proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE);
            
            sample.setNextItemSequence(0);
            sample.setRevision(0);
            sample.setEnteredDate(yToM);
            sample.setReceivedDate(yToM);
            
            loadDictionaryEntries();
            sample.setStatusId(samLoggedInId);
            
        }catch(Exception e){
            //ignore, we catch this on the validation
        }
    }
    
    public void unrelease() throws Exception {
        ValidationErrorsList errorsList;
        
        loadDictionaryEntries();
        
        if(!samReleasedId.equals(sample.getStatusId())){
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("wrongStatusUnrelease"));
            throw errorsList;
        }
            
        unreleased = true;
        sample.setStatusId(samCompletedId);
        sample.setReleasedDate(null);
        sample.setRevision(sample.getRevision()+1);
    }

    //
    //other managers
    //
    public SampleDomainInt getDomainManager() throws Exception {
        String domain;
        
        domain = sample.getDomain();
        
        assert domain != null : "domain is null";
        
        if (sampleDomain == null) {
            if (sample.getId() != null) {
                try {
                    if (domain.equals(HUMAN_DOMAIN_FLAG))
                        sampleDomain = SampleHumanManager.findBySampleId(sample.getId());
                    else if (domain.equals(ENVIRONMENTAL_DOMAIN_FLAG))
                        sampleDomain = SampleEnvironmentalManager.fetchBySampleId(sample.getId());
                    else if (domain.equals(WELL_DOMAIN_FLAG))
                        sampleDomain = SamplePrivateWellManager.fetchBySampleId(sample.getId());
                    else if (domain.equals(SDWIS_DOMAIN_FLAG))
                        sampleDomain = SampleSDWISManager.fetchBySampleId(sample.getId());
    
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (sampleDomain == null) 
                createEmptyDomainManager();
        }
    
        return sampleDomain;
    }
    
    public void createEmptyDomainManager() throws Exception {
        String domain;
        
        domain = sample.getDomain();
        assert domain != null : "domain is null";
        
        if (domain.equals(HUMAN_DOMAIN_FLAG))
            sampleDomain = SampleHumanManager.getInstance();
        else if (domain.equals(ENVIRONMENTAL_DOMAIN_FLAG))
            sampleDomain = SampleEnvironmentalManager.getInstance();
        else if (domain.equals(WELL_DOMAIN_FLAG))
            sampleDomain = SamplePrivateWellManager.getInstance();
        else if (domain.equals(SDWIS_DOMAIN_FLAG))
            sampleDomain = SampleSDWISManager.getInstance();
    }

    public SampleProjectManager getProjects() throws Exception {
        if (projects == null) {
            if (sample.getId() != null) {
                try {
                    projects = SampleProjectManager.fetchBySampleId(sample.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        
            if (projects == null)
                projects = SampleProjectManager.getInstance();
        }
        
        return projects;
    }

    public SampleOrganizationManager getOrganizations() throws Exception {
        if (organizations == null) {
            if (sample.getId() != null) {
                try {
                    organizations = SampleOrganizationManager.fetchBySampleId(sample.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        
            if (organizations == null)
                organizations = SampleOrganizationManager.getInstance();
        }
    
        return organizations;
    }

    public SampleItemManager getSampleItems() throws Exception {
        if (sampleItems == null) {
            if (sample.getId() != null) {
                try {
                    sampleItems = SampleItemManager.fetchBySampleId(sample.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (sampleItems == null)
                sampleItems = SampleItemManager.getInstance();
            
            sampleItems.setSampleManager(this);
        }
    
        return sampleItems;
    }

    public NoteManager getInternalNotes() throws Exception {
        if (sampleInternalNotes == null) {
            if (sample.getId() != null) {
                try {
                    sampleInternalNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.SAMPLE,
                                                                                sample.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (sampleInternalNotes == null){
                sampleInternalNotes = NoteManager.getInstance();
                sampleInternalNotes.setIsExternal(false);
            }
        }
        
        return sampleInternalNotes;
    }

    public NoteManager getExternalNote() throws Exception {
        if (sampleExternalNote == null) {
            if (sample.getId() != null) {
                try {
                    sampleExternalNote = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.SAMPLE,
                                                                               sample.getId(), true);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (sampleExternalNote == null){
                sampleExternalNote = NoteManager.getInstance();
                sampleExternalNote.setIsExternal(true);
            }
        }
    
        return sampleExternalNote;
    }

    public SampleQaEventManager getQaEvents() throws Exception {
        if (qaEvents == null) {
            if (sample.getId() != null) {
                try {
                    qaEvents = SampleQaEventManager.fetchBySampleId(sample.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (qaEvents == null)
                qaEvents = SampleQaEventManager.getInstance();
        }
    
        return qaEvents;
    }

    public AuxDataManager getAuxData() throws Exception {
        if (auxData == null) {
            if (sample.getId() != null) {
                try {
                    auxData = AuxDataManager.fetchById(sample.getId(), ReferenceTable.SAMPLE);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (auxData == null)
                auxData = AuxDataManager.getInstance();
        }
    
        return auxData;
    }

    //
    //helper methods
    //
    public boolean hasReleasedAnalysis() throws Exception {
        return getSampleItems().hasReleasedAnalysis();
    }

    protected void updateSampleAnalysesStatuses() throws Exception {
        int e = 0, l = 0, c = 0, r = 0;
        SampleItemManager itemMan;
        AnalysisManager analysisMan;
        SampleItemViewDO sampleItemDO;
        AnalysisViewDO anDO;
        Integer oldStatusId, statusId, analysisStatusId;
    
        loadDictionaryEntries();
        statusId = null;
        oldStatusId = sample.getStatusId();
    
        itemMan = getSampleItems();
        for (int s = 0; s < itemMan.count(); s++ ) {
            sampleItemDO = itemMan.getSampleItemAt(s);
            analysisMan = itemMan.getAnalysisAt(s);
            for (int a = 0; a < analysisMan.count(); a++ ) {
                // update the analysis status
                analysisMan.updateAnalysisStatusAt(a, sampleItemDO.getTypeOfSampleId());
    
                anDO = analysisMan.getAnalysisAt(a);
                analysisStatusId = anDO.getStatusId();
    
                if (analysisStatusId.equals(anErrorLoggedInId) ||
                    analysisStatusId.equals(anErrorInitiatedId) ||
                    analysisStatusId.equals(anErrorInPrepId) ||
                    analysisStatusId.equals(anErrorCompletedId))
                    e++ ;
                else if (analysisStatusId.equals(anLoggedInId) ||
                         analysisStatusId.equals(anInitiatedId) ||
                         analysisStatusId.equals(anRequeueId) ||
                         analysisStatusId.equals(anInPrepId))
                    l++ ;
                else if (analysisStatusId.equals(anCompletedId) ||
                         analysisStatusId.equals(anOnHoldId))
                    c++ ;
                else if (analysisStatusId.equals(anReleasedId) ||
                         analysisStatusId.equals(anCancelledId))
                    r++ ;
            }
        }
    
        //if the sample has been unreleased we dont want to update the status
        if(!unreleased){
            if (e > 0) {
                statusId = samErrorId;
            } else if (l > 0) {
                statusId = samLoggedInId;
            } else if (c > 0) {
                if (sample.getReleasedDate() != null)
                    sample.setReleasedDate(null);
                statusId = samCompletedId;
                
                if(oldStatusId.equals(samReleasedId))
                    sample.setRevision(sample.getRevision());
            } else if (r > 0) {
                if (sample.getReleasedDate() == null)
                    sample.setReleasedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                statusId = samReleasedId;
            }
        
            // if the sample is in error keep it that way
            if (samErrorId.equals(sample.getStatusId()))
                statusId = samErrorId;
        
            sample.setStatusId(statusId);
        }
    }
    
    Integer getNextSequence(){
        Integer s;
        
        s = sample.getNextItemSequence();
        sample.setNextItemSequence(s+1);
        
        return s;
    }
    private void loadDictionaryEntries() throws Exception {
        if(anLoggedInId == null){
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
            samLoggedInId = proxy().getIdFromSystemName("sample_logged_in");
            samCompletedId = proxy().getIdFromSystemName("sample_completed");
            samReleasedId = proxy().getIdFromSystemName("sample_released");
            samErrorId = proxy().getIdFromSystemName("sample_error");
        }
    }

    private static SampleManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleManagerProxy();

        return proxy;
    }
}