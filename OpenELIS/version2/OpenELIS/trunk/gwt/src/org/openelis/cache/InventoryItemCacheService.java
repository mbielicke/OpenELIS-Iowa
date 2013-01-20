package org.openelis.cache;

import org.openelis.domain.InventoryItemDO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryItemCacheService implements InventoryItemCacheServiceInt,
                                      InventoryItemCacheServiceIntAsync {
    
    static InventoryItemCacheService instance;
    
    InventoryItemCacheServiceIntAsync service;
    
    public static InventoryItemCacheService get() {
        if(instance == null)
            instance = new InventoryItemCacheService();
        
        return instance;
    }
    
    private InventoryItemCacheService() {
        service = (InventoryItemCacheServiceIntAsync)GWT.create(InventoryItemCacheServiceInt.class);
    }

    @Override
    public void getById(Integer id, AsyncCallback<InventoryItemDO> callback) {
        service.getById(id, callback);
    }

    @Override
    public InventoryItemDO getById(Integer id) throws Exception {
        Callback<InventoryItemDO> callback;
        
        callback = new Callback<InventoryItemDO>();
        service.getById(id, callback);
        return callback.getResult();
    }

}
