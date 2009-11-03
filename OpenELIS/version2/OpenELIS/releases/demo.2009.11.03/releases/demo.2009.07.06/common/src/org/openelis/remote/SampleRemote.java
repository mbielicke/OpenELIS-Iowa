package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.SampleDO;
import org.openelis.manager.SampleManager;

@Remote
public interface SampleRemote {
    //return sample by primary key
    public SampleDO getSampleById(Integer sampleId);
    
    //return sample by primary key and lock
    public SampleDO getSampleByIdAndLock(Integer sampleId) throws Exception;
    
  //return sample by primary key and unlock
    public SampleDO getSampleByIdAndUnlock(Integer sampleId);
    
    //return sample by accession number
    public SampleDO getSampleByAccessionLabNumber(Integer accessionLabNumber); 
    
    public Integer update(SampleManager sample);
}
