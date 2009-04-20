package org.openelis.manager;

import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.SampleRemote;

public class SampleManager extends SampleManagerBase {
    private static final long serialVersionUID = 1L;

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
    public static SampleManager findSampleById(Integer id) {
        SampleManager sm = new SampleManager();
        
        sm.sample = new SampleDO();
        sm.sample.setId(id);
        sm.fetch();
        
        return sm;
    }
    
    /**
     * Creates a new instance of this object with the specified Specimen. Use this function to load an instance of this object from database.
     */
    public static SampleManager findSampleByAccessionLabNumber(Integer accessionNumber) {
        SampleManager sm = new SampleManager();
        
        SampleRemote remote = (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
        sm.sample = remote.getSampleByAccessionLabNumber(accessionNumber);

        return sm;
    }

    public void fetch() {
        SampleRemote remote = (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
        sample = remote.getSampleById(sample.id);
        
        sampleItems = null;
        organizations = null;
        projects = null;
        sampleDomain = null;
    }

    public void fetchForUpdate() {
        SampleRemote remote = (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
        sample = remote.getSampleByIdAndLock(sample.id);
        
        sampleItems = null;
        organizations = null;
        projects = null;
        sampleDomain = null;
    }
    
    public SampleDomainInt getAdditionalDomain() {
        if(sampleDomain == null && sample.getDomainId() != null){
            CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
            String systemName = remote.getSystemNameForEntryId(sample.getDomainId());
            
            if(systemName.equals("enviromental")){
                sampleDomain = new SampleEnvironmentalManager();
                sampleDomain.setSampleId(sample.getId());
            }else if(""){
                
            }
        }
        
        return sampleDomain;
    }

    public void validate() {

    }

    public void update() {
        SampleRemote remote = (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
        remote.update(this);
        
        //somehow need to update the ids
    }

    public void unrelease() {

    }

    public void updateStatus() {
        // TODO Auto-generated method stub
        
    }
}
