package org.openelis.manager;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.gwt.common.RPC;

public class SampleEnvironmentalManager implements RPC, SampleDomainInt{

    private static final long serialVersionUID = 1L;
    protected Integer sampleId;
    protected SampleEnvironmentalDO environmental;
    
    protected transient SampleEnvironmentalManagerIOInt manager;
    
    /**
     * Creates a new instance of this object.
     */
    public static SampleEnvironmentalManager getInstance() {
        SampleEnvironmentalManager sem;

        sem = new SampleEnvironmentalManager();
        sem.environmental = new SampleEnvironmentalDO();

        return sem;
    }

    //setters/getters
    public void setSampleId(Integer id) {
        sampleId = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public SampleEnvironmentalDO getEnvironmental() {
        return environmental;
    }

    //manager methods
    public Integer update() {
        Integer newId = manager().update(this);
        environmental.setId(newId);
        
        return newId;
    }
    
    public SampleEnvironmentalDO fetch(){
        environmental = manager().fetch(sampleId);
        
        return environmental;
    }
    
    private SampleEnvironmentalManagerIOInt manager(){
        if(manager == null)
            manager = ManagerFactory.getSampleEnvironmentalManagerIO();
        
        return manager;
    }
}
