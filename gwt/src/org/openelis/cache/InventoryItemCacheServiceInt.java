package org.openelis.cache;

import org.openelis.domain.InventoryItemDO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("inventoryItemCache")
public interface InventoryItemCacheServiceInt extends XsrfProtectedService {

    InventoryItemDO getById(Integer id) throws Exception;

}