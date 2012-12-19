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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.InconsistencyException;
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
    protected SampleDomainInt                     domainManager, deletedDomainManager;
    protected NoteManager                         sampleInternalNotes;
    protected NoteManager                         sampleExternalNote;
    protected AuxDataManager                      auxData;
    protected SampleDataBundle                    bundle;
    protected boolean                             statusWithError;

    public static final String                    ENVIRONMENTAL_DOMAIN_FLAG = "E",
                                                  HUMAN_DOMAIN_FLAG = "H", ANIMAL_DOMAIN_FLAG = "A", NEWBORN_DOMAIN_FLAG = "N",
                                                  PT_DOMAIN_FLAG = "P", SDWIS_DOMAIN_FLAG = "S", WELL_DOMAIN_FLAG = "W",
                                                  QUICK_ENTRY = "Q";

    protected transient boolean                   unreleaseWithNotes;
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
        domainManager = null;
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
     * Creates a new instance of this object with the specified Sample. Use this
     * function to load an instance of this object from database.
     */
    public static SampleManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    /**
     * Creates a new instance of this object with the specified Sample. Use this
     * function to load an instance of this object from database.
     */
    public static SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return proxy().fetchByAccessionNumber(accessionNumber);
    }

    public static SampleManager fetchWithItemsAnalyses(Integer id) throws Exception {
        return proxy().fetchWithItemsAnalyses(id);
    }
    
    public static SampleManager fetchWithAllDataById(Integer id) throws Exception {
        return proxy().fetchWithAllDataById(id);
    }
    
    public static SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        return proxy().fetchWithAllDataByAccessionNumber(accessionNumber);
    }

    public SampleManager add() throws Exception {
        updateSampleStatus();
        return proxy().add(this);

    }

    public SampleManager update() throws Exception {
        updateSampleStatus();
        return proxy().update(this);
    }
    

    public void updateCache() {
        proxy().updateCache(this);      
    }

    public SampleManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(sample.getId());
    }

    public SampleManager abortUpdate() throws Exception {
        return proxy().abortUpdate(sample.getId());
    }

    public void validate() throws Exception {
        ValidationErrorsList errorList;
        
        errorList = new ValidationErrorsList();
        proxy().validate(this, errorList);

        if (errorList.size() > 0)
            throw errorList;
        
    }

    public void setDefaults() {
        Datetime date;
    
        try {
            date = proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE);
    
            sample.setNextItemSequence(0);
            sample.setRevision(0);
            sample.setEnteredDate(date);
            sample.setStatusId(proxy().samNotVerifiedId);
    
        } catch (Exception e) {
            // ignore, we catch this on the validation
        }
    }

    public void setStatusWithError(boolean withError) {
        statusWithError = withError;
    }

    public void unrelease(boolean unreleaseWithNotes) throws Exception {
        ValidationErrorsList errorsList;

        if ( !proxy().samReleasedId.equals(sample.getStatusId())) {
            errorsList = new ValidationErrorsList();
            errorsList.add(new FormErrorException("wrongStatusUnrelease"));
            throw errorsList;
        }

        sample.setStatusId(proxy().samCompletedId);
        sample.setReleasedDate(null);
        sample.setRevision(sample.getRevision() + 1);     
        this.unreleaseWithNotes = unreleaseWithNotes;
    }   

    public SampleDataBundle getBundle() {
        if (bundle == null)
            bundle = new SampleDataBundle(SampleDataBundle.Type.SAMPLE, this, null, -1);
    
        return bundle;
    }
    
    public void changeDomain(String newDomain) throws Exception {
        if (DataBaseUtil.isEmpty(newDomain) || sample.getDomain().equals(newDomain))
            return;
        if (deletedDomainManager != null)
            throw new InconsistencyException("canChangeDomainOnlyOnce");
        
        deletedDomainManager = domainManager;        
        sample.setDomain(newDomain);        
        createEmptyDomainManager();
    }

    //
    // other managers
    //
    public SampleDomainInt getDomainManager() throws Exception {
        String domain;

        domain = sample.getDomain();

        assert domain != null : "domain is null";

        if (domainManager == null) {
            if (sample.getId() != null) {
                try {
                    if (domain.equals(HUMAN_DOMAIN_FLAG))
                        domainManager = SampleHumanManager.findBySampleId(sample.getId());
                    else if (domain.equals(ENVIRONMENTAL_DOMAIN_FLAG))
                        domainManager = SampleEnvironmentalManager.fetchBySampleId(sample.getId());
                    else if (domain.equals(WELL_DOMAIN_FLAG))
                        domainManager = SamplePrivateWellManager.fetchBySampleId(sample.getId());
                    else if (domain.equals(SDWIS_DOMAIN_FLAG))
                        domainManager = SampleSDWISManager.fetchBySampleId(sample.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (domainManager == null)
                createEmptyDomainManager();
        }

        return domainManager;
    }

    public void createEmptyDomainManager() throws Exception {
        String domain;

        domain = sample.getDomain();
        assert domain != null : "domain is null";

        if (domain.equals(HUMAN_DOMAIN_FLAG))
            domainManager = SampleHumanManager.getInstance();
        else if (domain.equals(ENVIRONMENTAL_DOMAIN_FLAG))
            domainManager = SampleEnvironmentalManager.getInstance();
        else if (domain.equals(WELL_DOMAIN_FLAG))
            domainManager = SamplePrivateWellManager.getInstance();
        else if (domain.equals(SDWIS_DOMAIN_FLAG))
            domainManager = SampleSDWISManager.getInstance();
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
                                                                                sample.getId(),
                                                                                false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (sampleInternalNotes == null) {
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

            if (sampleExternalNote == null) {
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
    // helper methods
    //
    public boolean hasReleasedAnalysis() throws Exception {
        return getSampleItems().hasReleasedAnalysis();
    }
    
    public SampleDomainInt getDeletedDomainManager() {
        return deletedDomainManager;
    }

    protected void updateSampleStatus() throws Exception {
        int e, l, c, r;
        SampleItemManager itemMan;
        AnalysisManager analysisMan;
        SampleItemViewDO data;
        AnalysisViewDO analysis;
        Integer oldStatusId, statusId, analysisStatusId;

        statusId = null;
        e = l = c = r = 0;
        oldStatusId = sample.getStatusId();
        
        //
        // find the lowest common status between all the analysis
        //
        itemMan = getSampleItems();
        for (int s = 0; s < itemMan.count(); s++ ) {
            data = itemMan.getSampleItemAt(s);
            analysisMan = itemMan.getAnalysisAt(s);
            for (int a = 0; a < analysisMan.count(); a++ ) {
                // update the analysis status
                analysisMan.updateAnalysisStatusAt(s, a, data.getTypeOfSampleId());

                analysis = analysisMan.getAnalysisAt(a);
                analysisStatusId = analysis.getStatusId();

                if (analysisStatusId.equals(proxy().anErrorLoggedInId) ||
                    analysisStatusId.equals(proxy().anErrorInitiatedId) ||
                    analysisStatusId.equals(proxy().anErrorInPrepId) ||
                    analysisStatusId.equals(proxy().anErrorCompletedId))
                    e++ ;
                else if (analysisStatusId.equals(proxy().anLoggedInId) ||
                         analysisStatusId.equals(proxy().anInitiatedId) ||
                         analysisStatusId.equals(proxy().anRequeueId) ||
                         analysisStatusId.equals(proxy().anInPrepId))
                    l++ ;
                else if (analysisStatusId.equals(proxy().anCompletedId) ||
                         analysisStatusId.equals(proxy().anOnHoldId))
                    c++ ;
                else if (analysisStatusId.equals(proxy().anReleasedId) ||
                         analysisStatusId.equals(proxy().anCancelledId))
                    r++ ;
            }
        }

        //
        // The only way for a sample to come out of 'Not Verified' status is 
        // through the verification screen
        //
        if (proxy().samNotVerifiedId.equals(oldStatusId))
            return;
        
        //
        // change the sample status to lowest
        //
        if (statusWithError || e > 0)
            statusId = proxy().samErrorId;
        else if (l > 0)
            statusId = proxy().samLoggedInId;
        else if (c > 0)
            statusId = proxy().samCompletedId;
        else if (r > 0)
            statusId = proxy().samReleasedId;

        if (statusId != null && statusId != oldStatusId) {
            if (statusId == proxy().samReleasedId) {
                if (sample.getReleasedDate() == null)
                    sample.setReleasedDate(proxy().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));
            } else {
                sample.setReleasedDate(null);
            }
            sample.setStatusId(statusId);
        }
    }

    protected Integer getNextSequence() {
        Integer s;

        s = sample.getNextItemSequence();
        sample.setNextItemSequence(s + 1);

        return s;
    }
    
    private static SampleManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleManagerProxy();

        return proxy;
    }    
}