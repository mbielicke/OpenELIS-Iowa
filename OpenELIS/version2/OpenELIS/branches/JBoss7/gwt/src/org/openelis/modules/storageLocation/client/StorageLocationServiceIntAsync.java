package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StorageLocationServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<StorageLocationManager> callback);

    void add(StorageLocationManager man, AsyncCallback<StorageLocationManager> callback);

    void fetchById(Integer id, AsyncCallback<StorageLocationManager> callback);

    void fetchChildByParentStorageLocationId(Integer id,
                                             AsyncCallback<StorageLocationChildManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<StorageLocationManager> callback);

    void fetchWithChildren(Integer id, AsyncCallback<StorageLocationManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(StorageLocationManager man, AsyncCallback<StorageLocationManager> callback);

    void validateForDelete(StorageLocationViewDO data, AsyncCallback<StorageLocationViewDO> callback);

}
