package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleDO;
import org.openelis.manager.SampleManager;

@Local
public interface SampleLocal {
    //return sample by primary key
    public SampleDO getSampleById(Integer sampleId);
    
    //return sample by primary key and lock
    public SampleDO getSampleByIdAndLock(Integer sampleId) throws Exception;
    
    public SampleDO getSampleByIdAndUnlock(Integer sampleId);
    
    //return sample by accession number
    public SampleDO getSampleByAccessionLabNumber(Integer accessionLabNumber); 
    
    public Integer update(SampleManager sample);
}
