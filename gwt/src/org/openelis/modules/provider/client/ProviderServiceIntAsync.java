package org.openelis.modules.provider.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<ProviderManager> callback);

    void add(ProviderManager man, AsyncCallback<ProviderManager> callback);

    void fetchById(Integer id, AsyncCallback<ProviderManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ProviderManager> callback);

    void fetchLocationByProviderId(Integer id, AsyncCallback<ProviderLocationManager> callback);

    void fetchWithLocations(Integer id, AsyncCallback<ProviderManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<ProviderManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdFirstLastNameVO>> callback);

    void update(ProviderManager man, AsyncCallback<ProviderManager> callback);

}
