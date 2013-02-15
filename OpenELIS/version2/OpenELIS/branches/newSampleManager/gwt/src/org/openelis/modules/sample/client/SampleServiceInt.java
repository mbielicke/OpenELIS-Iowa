package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sample")
public interface SampleServiceInt extends RemoteService {

    SampleManager fetchById(Integer sampleId) throws Exception;

    SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception;

    SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception;

    SampleManager fetchWithAllDataById(Integer sampleId) throws Exception;

    SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception;

    // sample methods
    ArrayList<IdAccessionVO> query(Query query) throws Exception;

    SampleManager add(SampleManager man) throws Exception;

    SampleManager update(SampleManager man) throws Exception;

    SampleManager fetchForUpdate(Integer sampleId) throws Exception;

    SampleManager abortUpdate(Integer sampleId) throws Exception;

    // sample org methods
    SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId) throws Exception;

    // sample project methods
    SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception;

    // sample item methods
    SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception;

    // sample qa method
    SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception;

    SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception;

    Integer getNewAccessionNumber() throws Exception;

    PWSDO fetchPwsByPwsId(String number0) throws Exception;

}