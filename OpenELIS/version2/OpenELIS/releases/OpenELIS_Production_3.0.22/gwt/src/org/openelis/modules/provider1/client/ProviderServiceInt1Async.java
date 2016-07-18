package org.openelis.modules.provider1.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServiceInt1Async {

    void getInstance(AsyncCallback<ProviderManager1> callback);

    void fetchById(Integer id, AsyncCallback<ProviderManager1> callback);

    void fetchByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<ProviderManager1>> callback);

    void fetchByLastNameNpiExternalId(String search, AsyncCallback<ArrayList<ProviderDO>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdFirstLastNameVO>> callback);

    void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                      AsyncCallback<ArrayList<ProviderManager1>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ProviderManager1> callback);

    void fetchForUpdate(ArrayList<Integer> ids, AsyncCallback<ArrayList<ProviderManager1>> callback);

    void unlock(Integer providerId, AsyncCallback<ProviderManager1> callback);

    void unlock(ArrayList<Integer> providerId, AsyncCallback<ArrayList<ProviderManager1>> callback);

    void update(ProviderManager1 man, AsyncCallback<ProviderManager1> callback);

    void update(ProviderManager1 man, boolean ignoreWarnings,
                AsyncCallback<ProviderManager1> callback);
}
