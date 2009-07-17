package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.managerOld.SampleDomainInt;

@Local
public interface SampleEnvironmentalLocal {
    public SampleEnvironmentalDO getEnvBySampleId(Integer sampleId);
    
    public Integer update(SampleDomainInt sampleDomain);
}
