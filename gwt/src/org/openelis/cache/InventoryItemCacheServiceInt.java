package org.openelis.cache;

import org.openelis.domain.InventoryItemDO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inventoryItemCache")
public interface InventoryItemCacheServiceInt extends RemoteService {

    InventoryItemDO getById(Integer id) throws Exception;

}