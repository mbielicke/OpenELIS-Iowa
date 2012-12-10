package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;

@Local
public interface SampleItemLocal {
    public SampleItemViewDO fetchById(Integer id) throws Exception;    
    public ArrayList<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception;
    public ArrayList<SampleItemViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds);
    public ArrayList<SampleItemViewDO> fetchByAnalysisIds(ArrayList<Integer> analysisIds);
    public SampleItemDO add(SampleItemDO data);
    public SampleItemDO update(SampleItemDO data);
    public void delete(SampleItemDO data);
}