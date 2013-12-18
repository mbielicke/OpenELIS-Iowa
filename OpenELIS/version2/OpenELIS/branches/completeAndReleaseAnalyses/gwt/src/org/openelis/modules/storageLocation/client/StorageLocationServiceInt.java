package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("storageLocation")
public interface StorageLocationServiceInt extends XsrfProtectedService {

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