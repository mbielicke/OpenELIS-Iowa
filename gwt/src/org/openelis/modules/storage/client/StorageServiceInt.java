package org.openelis.modules.storage.client;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("storage")
public interface StorageServiceInt extends RemoteService {

    StorageManager fetchById(Query query) throws Exception;

    StorageManager fetchCurrentByLocationId(Integer id) throws Exception;

    StorageManager fetchHistoryByLocationId(Query query) throws Exception;

    ArrayList<StorageLocationViewDO> fetchAvailableByName(String search) throws Exception;

    StorageManager update(StorageManager man) throws Exception;

}