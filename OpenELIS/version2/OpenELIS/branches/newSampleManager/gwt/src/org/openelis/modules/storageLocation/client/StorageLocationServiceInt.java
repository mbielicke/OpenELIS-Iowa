package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("storageLocation")
public interface StorageLocationServiceInt extends RemoteService {

    StorageLocationManager fetchById(Integer id) throws Exception;

    StorageLocationManager fetchWithChildren(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    StorageLocationManager add(StorageLocationManager man) throws Exception;

    StorageLocationManager update(StorageLocationManager man) throws Exception;

    StorageLocationManager fetchForUpdate(Integer id) throws Exception;

    StorageLocationManager abortUpdate(Integer id) throws Exception;

    //
    // support for StorageLocationChildManager
    //
    StorageLocationChildManager fetchChildByParentStorageLocationId(Integer id) throws Exception;

    StorageLocationViewDO validateForDelete(StorageLocationViewDO data) throws Exception;

}