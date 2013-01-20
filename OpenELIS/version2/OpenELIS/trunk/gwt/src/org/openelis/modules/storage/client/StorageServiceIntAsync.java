package org.openelis.modules.storage.client;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StorageServiceIntAsync {

    void fetchAvailableByName(String search,
                              AsyncCallback<ArrayList<StorageLocationViewDO>> callback);

    void fetchById(Query query, AsyncCallback<StorageManager> callback);

    void fetchCurrentByLocationId(Integer id, AsyncCallback<StorageManager> callback);

    void fetchHistoryByLocationId(Query query, AsyncCallback<StorageManager> callback);

    void update(StorageManager man, AsyncCallback<StorageManager> callback);

}
