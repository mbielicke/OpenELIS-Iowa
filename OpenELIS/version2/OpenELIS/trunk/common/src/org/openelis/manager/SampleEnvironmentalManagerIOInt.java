package org.openelis.manager;

import java.util.List;

import org.openelis.domain.SampleEnvironmentalDO;

public interface SampleEnvironmentalManagerIOInt {
    public Integer update(SampleDomainInt domainManager);
    public SampleEnvironmentalDO fetch(Integer sampleId);

}
