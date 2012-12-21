package org.openelis.modules.storageunit.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageUnitDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StorageUnitServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<StorageUnitDO> callback);

    void add(StorageUnitDO data, AsyncCallback<StorageUnitDO> callback);

    void delete(StorageUnitDO data, AsyncCallback<Void> callback);

    void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchById(Integer id, AsyncCallback<StorageUnitDO> callback);

    void fetchForUpdate(Integer id, AsyncCallback<StorageUnitDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(StorageUnitDO data, AsyncCallback<StorageUnitDO> callback);

}
