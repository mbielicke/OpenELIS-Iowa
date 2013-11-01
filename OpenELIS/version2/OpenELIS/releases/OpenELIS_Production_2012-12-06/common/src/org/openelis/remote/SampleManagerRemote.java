package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.SampleDO;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;

@Remote
public interface SampleManagerRemote {
    public SampleManager update(SampleManager man) throws Exception;
    public SampleManager add(SampleManager man) throws Exception;
    public SampleManager fetchById(Integer sampleId) throws Exception;
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception;
    public SampleManager fetchWithItemsAnalysis(Integer sampleId) throws Exception;
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception;
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception;
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception;
    public SampleManager abortUpdate(Integer sampleId) throws Exception;
    
    public SampleOrganizationManager fetchSampleOrgsBySampleId(Integer sampleId) throws Exception;
    public SampleProjectManager fetchSampleProjectsBySampleId(Integer sampleId) throws Exception;
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception;
    public SampleManager validateAccessionNumber(SampleDO data) throws Exception;    
}