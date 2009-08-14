package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleEnvironmentalDO;

@Local
public interface SampleEnvironmentalLocal {
    public SampleEnvironmentalDO fetchBySampleId(Integer sampleId) throws Exception;
    
    public void add(SampleEnvironmentalDO envSampleDO);
    public void update(SampleEnvironmentalDO envSampleDO);
}
