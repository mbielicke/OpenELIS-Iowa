package org.openelis.cache;

import org.openelis.domain.InventoryItemDO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryItemCacheServiceIntAsync {

    void getById(Integer id, AsyncCallback<InventoryItemDO> callback);

}
