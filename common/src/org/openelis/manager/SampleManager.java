/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleManager implements RPC, HasNotesInt, HasAuxDataInt {
    private static final long serialVersionUID = 1L;
    
    protected SampleDO sample;
    protected SampleItemManager sampleItems;
    protected SampleOrganizationManager organizations;
    protected SampleProjectManager projects;
    protected SampleQaEventManager qaEvents;
    protected SampleDomainInt sampleDomain;
    protected NoteManager sampleInternalNotes;
    protected NoteManager sampleExternalNote;
    protected AuxDataManager auxData;
    
    protected Boolean hasReleasedCancelledAnalysis;
    
    public static final String  ENVIRONMENTAL_DOMAIN_FLAG   = "E",
                                HUMAN_DOMAIN_FLAG           = "H",
                                ANIMAL_DOMAIN_FLAG          = "A",
                                NEWBORN_DOMAIN_FLAG         = "N";
    
    protected transient static SampleManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for allocation.
     */
    protected SampleManager() {
        sample = null;
        sampleItems = null;
        organizations = null;
        projects = null;
        sampleDomain = null;
    }

    /**
     * Creates a new instance of this object. A default Specimen object is also created.
     */
    public static SampleManager getInstance() {
        SampleManager sm;

        sm = new SampleManager();
        sm.sample = new SampleDO();

        return sm;
    }

    /**
     * Creates a new instance of this object with the specified Specimen. Use this function to load an instance of this object from database.
     */
    public static SampleManager findById(Integer id) throws Exception {
        return proxy().fetch(id);
    }
    
    public static SampleManager findByIdWithItemsAnalyses(Integer id) throws Exception {
        return proxy().fetchWithItemsAnalyses(id);
    }
    
    /**
     * Creates a new instance of this object with the specified Specimen. Use this function to load an instance of this object from database.
     */
    public static SampleManager findByAccessionNumber(Integer accessionNumber) throws Exception {
        return proxy().fetchByAccessionNumber(accessionNumber);
    }
    
    public SampleManager fetchForUpdate() throws Exception {
        if(sample.getId() == null)
            throw new Exception("sample id is null");
        
        return proxy().fetchForUpdate(sample.getId());
    }
    
    public void validateAccessionNumber(SampleDO sampleDO) throws Exception {
        proxy().validateAccessionNumber(sampleDO);
    }
    
    public void unrelease() {

    }

    public void updateStatus() {
        
    }
    
    public boolean hasReleasedCancelledAnalysis() throws Exception {
        if(hasReleasedCancelledAnalysis != null)
            return hasReleasedCancelledAnalysis.booleanValue();
        
        SampleItemManager itemMan;
        AnalysisManager analysisMan;
        AnalysisViewDO anDO;
        boolean value = false;
        Integer cancelledId, releasedId;
    
        cancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
        releasedId = DictionaryCache.getIdFromSystemName("analysis_released");
        
        itemMan = getSampleItems();
        for(int s=0; s<itemMan.count(); s++){
            analysisMan = itemMan.getAnalysisAt(s);
            for(int a=0; a<analysisMan.count(); a++){
                anDO = analysisMan.getAnalysisAt(a);
                if(cancelledId.equals(anDO.getStatusId()) || releasedId.equals(anDO.getStatusId())){
                    value = true;
                    break;
                }
            }
        }
        
        setHasReleasedCancelledAnalysis(value);

        return value;
    }
    
    public void setHasReleasedCancelledAnalysis(boolean value){
        hasReleasedCancelledAnalysis = new Boolean(value);
    }
    
    //getters and setters
    /**
     * Returns the managed Sample object.
     */
    public SampleDO getSample() {
        return sample;
    }
    
    public void setSample(SampleDO sample) {
        this.sample = sample;
    }
    
    public NoteManager getNotes() throws Exception {
        throw new UnsupportedOperationException();
    }
    
    //get manager methods
    public SampleDomainInt getDomainManager() throws Exception {
        if(sample.getDomain() == null)
            throw new InconsistencyException("domain is null");
        
        if(sampleDomain == null){
            if(sample.getId() != null){
                try{
                    if(HUMAN_DOMAIN_FLAG.equals(sample.getDomain()))
                        sampleDomain = SampleHumanManager.findBySampleId(sample.getId());
                    else if(ENVIRONMENTAL_DOMAIN_FLAG.equals(sample.getDomain()))
                        sampleDomain = SampleEnvironmentalManager.findBySampleId(sample.getId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(sampleDomain == null){
            if(HUMAN_DOMAIN_FLAG.equals(sample.getDomain()))
                sampleDomain = SampleHumanManager.getInstance();
            else if(ENVIRONMENTAL_DOMAIN_FLAG.equals(sample.getDomain()))
                sampleDomain = SampleEnvironmentalManager.getInstance();
        }
        
        return sampleDomain;
    }

    public SampleItemManager getSampleItems() throws Exception {
        if(sampleItems == null){
            if(sample.getId() != null){
                try{
                    sampleItems = SampleItemManager.findBySampleId(sample.getId());
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(sampleItems == null)
             sampleItems = SampleItemManager.getInstance();
     
        return sampleItems;
    }
    
    public SampleOrganizationManager getOrganizations() throws Exception {
        if(organizations == null){
            if(sample.getId() != null){
                try{
                    organizations = SampleOrganizationManager.findBySampleId(sample.getId());
                    
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(organizations == null)
            organizations = SampleOrganizationManager.getInstance();
     
        return organizations;
    }

    public SampleProjectManager getProjects() throws Exception {
        if(projects == null){
            if(sample.getId() != null){
                try{
                    projects = SampleProjectManager.findBySampleId(sample.getId());
                    
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(projects == null)
            projects = SampleProjectManager.getInstance();
     
        return projects;
    }

    
    public SampleQaEventManager getQaEvents() throws Exception {
        if(qaEvents == null){
            if(sample.getId() != null){
                try{
                    qaEvents = SampleQaEventManager.findBySampleId(sample.getId());
                    
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(qaEvents == null)
             qaEvents = SampleQaEventManager.getInstance();
     
        return qaEvents;
    }
    
    public AuxDataManager getAuxDataforUpdate() throws Exception{
        return getAuxData(true);
    }
    
    public AuxDataManager getAuxData() throws Exception {
        return getAuxData(false);
    }
    
    private AuxDataManager getAuxData(boolean forUpdate) throws Exception {
        if(auxData == null){
            if(sample.getId() != null){
                try{
                    if(forUpdate)
                        auxData = AuxDataManager.fetchByIdForUpdate(sample.getId(), ReferenceTable.SAMPLE);
                    else
                        auxData = AuxDataManager.fetchById(sample.getId(), ReferenceTable.SAMPLE
                                                           );
                }
                catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
         if(auxData == null)
             auxData = AuxDataManager.getInstance();
     
        return auxData;
    }
    
    public NoteManager getInternalNotes() throws Exception {
        if(sampleInternalNotes == null){
            if(sample.getId() != null){
                try{
                    sampleInternalNotes = NoteManager.fetchByRefTableRefId(ReferenceTable.SAMPLE_INTERNAL_NOTE, sample.getId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(sampleInternalNotes == null)
            sampleInternalNotes = NoteManager.getInstance();

        return sampleInternalNotes;
    }  
    
    public NoteManager getExternalNote() throws Exception {
        if(sampleExternalNote == null){
            if(sample.getId() != null){
                try{
                    sampleExternalNote = NoteManager.fetchByRefTableRefId(ReferenceTable.SAMPLE, sample.getId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(sampleExternalNote == null)
            sampleExternalNote = NoteManager.getInstance();

        return sampleExternalNote;
    }  

  //service methods
    public SampleManager add() throws Exception {
        return proxy().add(this);
        
    }
    
    public SampleManager update() throws Exception {
        return proxy().update(this);
        
    }
    
    public SampleManager abort() throws Exception {
        return proxy().abort(sample.getId());
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorList = new ValidationErrorsList();
        proxy().validate(this, errorList);
        
        if(errorList.size() > 0)
            throw errorList;
    }
    
    private static SampleManagerProxy proxy(){
        if(proxy == null) 
            proxy = new SampleManagerProxy();
        
        return proxy;
    }    
}