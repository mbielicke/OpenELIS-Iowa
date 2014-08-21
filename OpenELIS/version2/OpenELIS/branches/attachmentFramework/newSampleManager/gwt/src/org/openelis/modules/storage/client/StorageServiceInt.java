package org.openelis.modules.storage.client;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.manager.StorageManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("storage")
public interface StorageServiceInt extends XsrfProtectedService {

    StorageManager fetchById(Query query) throws Exception;

    StorageManager fetchCurrentByLocationId(Integer id) throws Exception;

    StorageManager fetchHistoryByLocationId(Query query) throws Exception;

    ArrayList<StorageLocationViewDO> fetchAvailableByName(String search) throws Exception;

    StorageManager update(StorageManager man) throws Exception;

}