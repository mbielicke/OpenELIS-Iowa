package org.openelis.local;

import java.util.List;

import javax.ejb.Local;

import org.openelis.domain.SampleItemViewDO;

@Local
public interface SampleItemLocal {
    public List<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception;
    
    public void add(SampleItemViewDO itemDO);
    public void update(SampleItemViewDO itemDO);
    public void delete(SampleItemViewDO itemDO);
}