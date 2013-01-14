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

    public SampleItemDO add(SampleItemDO data) throws Exception;

    public SampleItemDO update(SampleItemDO data) throws Exception;

    public void delete(SampleItemDO data) throws Exception;

    public void validate(SampleItemViewDO data, Integer accession) throws Exception;
}