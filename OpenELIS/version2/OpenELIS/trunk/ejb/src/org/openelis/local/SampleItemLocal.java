package org.openelis.local;

import java.util.List;

import javax.ejb.Local;

import org.openelis.domain.SampleItemDO;

@Local
public interface SampleItemLocal {
    public List<SampleItemDO> fetchBySampleId(Integer sampleId) throws Exception;
    
    public void add(SampleItemDO itemDO);
    public void update(SampleItemDO itemDO);
    public void delete(SampleItemDO itemDO);
}