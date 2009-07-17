package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleHumanDO;
import org.openelis.managerOld.SampleDomainInt;

@Local
public interface SampleHumanLocal {
    public SampleHumanDO getHumanBySampleId(Integer sampleId);
    
    public Integer update(SampleDomainInt sampleDomain);
}
