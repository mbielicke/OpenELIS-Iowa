package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.SampleDO;

@Local
public interface SampleLocal {
    public SampleDO fetchById(Integer sampleId) throws Exception;
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception;
    public ArrayList<Object[]> fetchSamplesForFinalReportBatch() throws Exception;
    public ArrayList<Object[]> fetchSamplesForFinalReportSingle(Integer sampleId) throws Exception; 
    public SampleDO add(SampleDO data) throws Exception;
    public SampleDO update(SampleDO data) throws Exception;

}
