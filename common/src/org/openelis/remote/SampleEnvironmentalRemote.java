package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.manager.SampleDomainInt;

@Remote
public interface SampleEnvironmentalRemote {
    public SampleEnvironmentalDO getEnvBySampleId(Integer sampleId);
    
    public Integer update(SampleDomainInt sampleDomain);

}
