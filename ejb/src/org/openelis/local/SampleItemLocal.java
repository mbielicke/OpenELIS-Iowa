package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.SampleItemViewDO;

@Local
public interface SampleItemLocal {
    public SampleItemViewDO fetchById(Integer id) throws Exception;    
    public ArrayList<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception;
    public ArrayList<SampleItemViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds);
    public SampleItemViewDO add(SampleItemViewDO data);
    public SampleItemViewDO update(SampleItemViewDO data);
    public void delete(SampleItemViewDO data);
}