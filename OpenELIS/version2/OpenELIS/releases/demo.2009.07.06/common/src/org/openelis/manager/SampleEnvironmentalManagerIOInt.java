package org.openelis.manager;

import org.openelis.domain.AddressDO;
import org.openelis.domain.SampleEnvironmentalDO;

public interface SampleEnvironmentalManagerIOInt {
    public Integer update(SampleDomainInt domainManager);
    public SampleEnvironmentalDO fetch(Integer sampleId);
    public AddressDO getAddressById(Integer id);

}
