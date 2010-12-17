package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleEnvironmentalDO;

@Local
public interface SampleEnvironmentalLocal {
    public SampleEnvironmentalDO fetchBySampleId(Integer sampleId) throws Exception;
    
    public SampleEnvironmentalDO add(SampleEnvironmentalDO envSampleDO) throws Exception ;
    public SampleEnvironmentalDO update(SampleEnvironmentalDO envSampleDO) throws Exception;
}
