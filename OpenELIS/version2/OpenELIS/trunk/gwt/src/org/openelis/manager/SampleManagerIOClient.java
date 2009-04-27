package org.openelis.manager;

import org.openelis.domain.SampleDO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleRemote;

public class SampleManagerIOClient implements SampleManagerIOInt {

    public SampleDO fetch(Integer sampleId) {
        SampleRemote remote = getSampleRemote();
        return remote.getSampleById(sampleId);
    }
    
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) {
        SampleRemote remote = getSampleRemote();
        return remote.getSampleByAccessionLabNumber(accessionNumber);
    }

    public SampleDO fetchForUpdate(Integer sampleId) throws Exception {
        SampleRemote remote = getSampleRemote();
        return remote.getSampleByIdAndLock(sampleId);
    }

    public Integer update(SampleManager sample) {
        SampleRemote remote = getSampleRemote();
        return remote.update(sample);
    }
    
    private SampleRemote getSampleRemote(){
        return (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
    }
}
