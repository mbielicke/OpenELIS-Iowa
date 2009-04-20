package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public abstract class SampleManagerBase implements RPC {
    private static final long serialVersionUID = 1L;
    
    SampleDO sample;
    SampleItemsManager sampleItems;
    SampleOrganizationsManager organizations;
    SampleProjectsManager projects;
    QaEventsManager qaEvents;
    SampleDomainInt sampleDomain;
    
    public abstract void updateStatus();
    
    public abstract SampleDomainInt getAdditionalDomain();
    
    public void setSampleDomain(SampleDomainInt sampleDomain) {
        this.sampleDomain = sampleDomain;
    }
    
    //getters and setters
    /**
     * Returns the managed Sample object.
     */
    public SampleDO getSample() {
        return sample;
    }

    public SampleItemsManager getSampleItems() {
        if (sampleItems == null) {
            sampleItems = new SampleItemsManager();
            sampleItems.setParentId(sample.getId());
        }

        return sampleItems;
    }

    public void setSampleItems(SampleItemsManager sampleItems) {
        this.sampleItems = sampleItems;
    }

    public SampleOrganizationsManager getOrganizations() {
        if (organizations == null) {
            organizations = new SampleOrganizationsManager();
            organizations.setParentId(sample.getId());
        }

        return organizations;
    }

    public void setOrganizations(SampleOrganizationsManager organizations) {
        this.organizations = organizations;
    }

    public SampleProjectsManager getProjects() {
        if (projects == null) {
            projects = new SampleProjectsManager();
            projects.setParentId(sample.getId());
        }

        return projects;
    }

    public void setProjects(SampleProjectsManager projects) {
        this.projects = projects;
    }   
    
    public QaEventsManager getQaEvents() {
        if (qaEvents == null) {
            qaEvents = new QaEventsManager();
            qaEvents.setParentId(sample.getId());
        }

        return qaEvents;
    }

    public void setQaEvents(QaEventsManager qaEvents) {
        this.qaEvents = qaEvents;
    }
}
