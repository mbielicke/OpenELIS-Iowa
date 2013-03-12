package org.openelis.modules.testTrailer.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TestTrailerServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<TestTrailerDO> callback);

    void add(TestTrailerDO data, AsyncCallback<TestTrailerDO> callback);

    void delete(TestTrailerDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<TestTrailerDO> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<TestTrailerDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(TestTrailerDO data, AsyncCallback<TestTrailerDO> callback);

}
