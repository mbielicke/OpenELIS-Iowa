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

import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.RPC;

public class SampleManager implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected SampleDO sample;
    protected SampleItemsManager sampleItems;
    protected SampleOrganizationsManager organizations;
    protected SampleProjectsManager projects;
    protected SampleQaEventsManager qaEvents;
    protected SampleDomainInt sampleDomain;
    
    public static final String  ENVIRONMENTAL_DOMAIN_FLAG   = "E",
                                HUMAN_DOMAIN_FLAG           = "H",
                                ANIMAL_DOMAIN_FLAG          = "A",
                                NEWBORN_DOMAIN_FLAG         = "N";
    
    protected transient SampleManagerIOInt manager;
    
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
    public static SampleManager findById(Integer id) {
        SampleManager sm = new SampleManager();
        
        sm.sample = new SampleDO();
        sm.sample.setId(id);
        sm.fetch();
        
        return sm;
    }
    
    /**
     * Creates a new instance of this object with the specified Specimen. Use this function to load an instance of this object from database.
     */
    public static SampleManager findByAccessionNumber(Integer accessionNumber) {
        SampleManager sm = new SampleManager();
        sm.sample = new SampleDO();
        sm.sample.setAccessionNumber(accessionNumber);
        sm.fetchByAccessionNumber();

        return sm;
    }
    
    public void validate() {

    }

    public void unrelease() {

    }

    public void updateStatus() {
        
    }
    
    //getters and setters
    /**
     * Returns the managed Sample object.
     */
    public SampleDO getSample() {
        return sample;
    }
    
    public void setAdditonalDomainManager(SampleDomainInt sampleDomain) {
        this.sampleDomain = sampleDomain;
        
        if(sampleDomain instanceof SampleHumanManager)
            sample.setDomain("H");  //getDictIdFromSystemName("Human")
        else if(sampleDomain instanceof SampleEnvironmentalManager)
            sample.setDomain("E");
    }
    
    public SampleDomainInt getAdditionalDomainManager() {
        //getSystemNameByDictId(sample.getDomain();)
        if(sample.getDomain() == null)
            return null;
        
        if(sampleDomain == null){
            if("H".equals(sample.getDomain()))
                sampleDomain = SampleHumanManager.getInstance();
            else if("E".equals(sample.getDomain()))
                sampleDomain = SampleEnvironmentalManager.getInstance();
            
            sampleDomain.setSampleId(sample.getId());
        }
        
        return sampleDomain;
    }

    public void setSampleItemsManager(SampleItemsManager sampleItems) {
        this.sampleItems = sampleItems;
    }
    
    public SampleItemsManager getSampleItemsManager() {
        if (sampleItems == null) {
            sampleItems = SampleItemsManager.getInstance();
            sampleItems.setSampleId(sample.getId());
        }

        return sampleItems;
    }

    public SampleOrganizationsManager getOrganizationsManager() {
        if (organizations == null) {
            organizations = SampleOrganizationsManager.getInstance();
            organizations.setSampleId(sample.getId());
        }

        return organizations;
    }

    public void setOrganizationsManager(SampleOrganizationsManager organizations) {
        this.organizations = organizations;
    }

    public SampleProjectsManager getProjectsManager() {
        if (projects == null) {
            projects = SampleProjectsManager.getInstance();
            projects.setSampleId(sample.getId());
        }

        return projects;
    }

    public void setProjectsManager(SampleProjectsManager projects) {
        this.projects = projects;
    }   
    
    public SampleQaEventsManager getQaEventsManager() {
        if (qaEvents == null) {
            qaEvents = SampleQaEventsManager.getInstance();
            qaEvents.setSampleId(sample.getId());
        }

        return qaEvents;
    }

    public void setQaEventsManager(SampleQaEventsManager qaEvents) {
        this.qaEvents = qaEvents;
    }
    
    public SampleManagerIOInt getManager() {
        return manager;
    }

    public void setManager(SampleManagerIOInt manager) {
        this.manager = manager;
    }
    
    //manager methods
    public Integer update() {
        Integer newSampleId = manager().update(this);
        sample.setId(newSampleId);
        
        return newSampleId;
    }
    
    public void fetch() {
        sample = manager().fetch(sample.getId());
    }
    
    public void fetchForUpdate() throws Exception{
        sample = manager().fetchForUpdate(sample.getId());
    }
    
    public void fetchAndUnlock(){
        sample = manager().fetchAndUnlock(sample.getId());
    }
    
    protected void fetchByAccessionNumber() {
        sample = manager().fetchByAccessionNumber(sample.getAccessionNumber());
    }

    private SampleManagerIOInt manager(){
        if(manager == null)
            manager = ManagerFactory.getSampleManagerIO();
        
        return manager;
    }
}