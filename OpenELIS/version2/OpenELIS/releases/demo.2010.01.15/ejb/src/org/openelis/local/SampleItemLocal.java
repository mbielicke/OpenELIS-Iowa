package org.openelis.local;

import java.util.List;

import javax.ejb.Local;

import org.openelis.domain.SampleItemViewDO;

@Local
public interface SampleItemLocal {
    public List<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception;
    
    public SampleItemViewDO add(SampleItemViewDO data);
    public SampleItemViewDO update(SampleItemViewDO data);
    public void delete(SampleItemViewDO data);
}