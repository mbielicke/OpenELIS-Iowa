package org.openelis.manager;

import org.openelis.domain.SampleDO;

public interface SampleManagerIOInt {
    public Integer update(SampleManager sample);
    public SampleDO fetch(Integer sampleId);
    public SampleDO fetchByAccessionNumber(Integer accessionNumber);
    public SampleDO fetchForUpdate(Integer sampleId) throws Exception;
    public SampleDO fetchAndUnlock(Integer sampleId);
}
