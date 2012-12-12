package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.SampleEnvironmentalDO;

@Local
public interface SampleEnvironmentalLocal {
    public SampleEnvironmentalDO fetchBySampleId(Integer sampleId) throws Exception;    
    public ArrayList<SampleEnvironmentalDO> fetchBySampleIds(ArrayList<Integer> sampleIds);
    public SampleEnvironmentalDO add(SampleEnvironmentalDO data) throws Exception ;
    public SampleEnvironmentalDO update(SampleEnvironmentalDO data) throws Exception;    
    public void delete(SampleEnvironmentalDO data) throws Exception;    
}
