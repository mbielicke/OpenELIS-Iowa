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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleServiceIntAsync {

    void abortUpdate(Integer sampleId, AsyncCallback<SampleManager> callback);

    void add(SampleManager man, AsyncCallback<SampleManager> callback);

    void fetchByAccessionNumber(Integer accessionNumber, AsyncCallback<SampleManager> callback);

    void fetchById(Integer sampleId, AsyncCallback<SampleManager> callback);

    void fetchBySampleId(Integer sampleId, AsyncCallback<SampleQaEventManager> callback);

    void fetchForUpdate(Integer sampleId, AsyncCallback<SampleManager> callback);

    void fetchPwsByPwsId(String number0, AsyncCallback<PWSDO> callback);

    void fetchSampleItemsBySampleId(Integer sampleId, AsyncCallback<SampleItemManager> callback);

    void fetchSampleOrganizationsBySampleId(Integer sampleId,
                                            AsyncCallback<SampleOrganizationManager> callback);

    void fetchSampleprojectsBySampleId(Integer sampleId,
                                       AsyncCallback<SampleProjectManager> callback);

    void fetchWithAllDataByAccessionNumber(Integer accessionNumber,
                                           AsyncCallback<SampleManager> callback);

    void fetchWithAllDataById(Integer sampleId, AsyncCallback<SampleManager> callback);

    void fetchWithItemsAnalyses(Integer sampleId, AsyncCallback<SampleManager> callback);

    void getNewAccessionNumber(AsyncCallback<Integer> callback);

    void query(Query query, AsyncCallback<ArrayList<IdAccessionVO>> callback);

    void update(SampleManager man, AsyncCallback<SampleManager> callback);

    void validateAccessionNumber(SampleDO sampleDO, AsyncCallback<SampleManager> callback);

}
