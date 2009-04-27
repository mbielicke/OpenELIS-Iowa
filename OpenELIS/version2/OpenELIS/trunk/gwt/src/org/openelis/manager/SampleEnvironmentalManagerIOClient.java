package org.openelis.manager;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleEnvironmentalRemote;

public class SampleEnvironmentalManagerIOClient implements SampleEnvironmentalManagerIOInt {

    public SampleEnvironmentalDO fetch(Integer sampleId) {
        SampleEnvironmentalRemote ser = getSampleEnvironmentalRemote();
        return ser.getEnvBySampleId(sampleId);
    }

    public Integer update(SampleDomainInt domainManager) {
        SampleEnvironmentalRemote ser = getSampleEnvironmentalRemote();
        return ser.update(domainManager);
    }
    
    private SampleEnvironmentalRemote getSampleEnvironmentalRemote(){
        return (SampleEnvironmentalRemote)EJBFactory.lookup("openelis/SampleEnvironmentalBean/remote");
    }
}
