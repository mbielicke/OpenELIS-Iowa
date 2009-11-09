package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.SampleDO;

@Local
public interface SampleLocal {
    public SampleDO fetchById(Integer sampleId) throws Exception;
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception;
    
    public SampleDO add(SampleDO data);
    public SampleDO update(SampleDO data) throws Exception;

}
