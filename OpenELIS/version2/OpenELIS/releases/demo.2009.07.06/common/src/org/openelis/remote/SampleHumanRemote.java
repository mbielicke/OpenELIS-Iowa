package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.SampleHumanDO;
import org.openelis.manager.SampleDomainInt;

@Remote
public interface SampleHumanRemote {
    public SampleHumanDO getHumanBySampleId(Integer sampleId);
    
    public Integer update(SampleDomainInt sampleDomain);
}
