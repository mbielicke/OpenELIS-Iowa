package org.openelis.manager;

import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.RPC;

public class SampleManager implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected SampleDO sample;
    protected SampleItemsManager sampleItems;
    protected SampleOrganizationsManager organizations;
    protected SampleProjectsManager projects;
    protected QaEventsManager qaEvents;
    protected SampleDomainInt sampleDomain;
    
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
    
    public void setAdditonalDomain(SampleDomainInt sampleDomain) {
        this.sampleDomain = sampleDomain;
        
        if(sampleDomain instanceof SampleHumanManager)
            sample.setDomain("H");
        else if(sampleDomain instanceof SampleEnvironmentalManager)
            sample.setDomain("E");
    }
    
    public SampleDomainInt getAdditionalDomain() {
        if(sample.getDomain() == null)
            return null;
        
        if(sampleDomain == null){
            if("H".equals(sample.getDomain()))
                sampleDomain = SampleHumanManager.getInstance();
            else if("E".equals(sample.getDomain()))
                sampleDomain = SampleEnvironmentalManager.getInstance();
        }
        
        return sampleDomain;
    }

    public SampleItemsManager getSampleItems() {
        if (sampleItems == null) {
            sampleItems = SampleItemsManager.getInstance();
            sampleItems.setSampleId(sample.getId());
        }

        return sampleItems;
    }

    public SampleOrganizationsManager getOrganizations() {
        if (organizations == null) {
            organizations = SampleOrganizationsManager.getInstance();
            organizations.setSampleId(sample.getId());
        }

        return organizations;
    }

    public void setOrganizations(SampleOrganizationsManager organizations) {
        this.organizations = organizations;
    }

    public SampleProjectsManager getProjects() {
        if (projects == null) {
            projects = SampleProjectsManager.getInstance();
            projects.setSampleId(sample.getId());
        }

        return projects;
    }

    public void setProjects(SampleProjectsManager projects) {
        this.projects = projects;
    }   
    
    public QaEventsManager getQaEvents() {
        if (qaEvents == null) {
            qaEvents = QaEventsManager.getInstance();
            qaEvents.setSampleId(sample.getId());
        }

        return qaEvents;
    }

    public void setQaEvents(QaEventsManager qaEvents) {
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
    
    public void fetchByAccessionNumber() {
        sample = manager().fetchByAccessionNumber(sample.getAccessionNumber());
    }

    private SampleManagerIOInt manager(){
        if(manager == null)
            manager = ManagerFactory.getSampleManagerIO();
        
        return manager;
    }
}