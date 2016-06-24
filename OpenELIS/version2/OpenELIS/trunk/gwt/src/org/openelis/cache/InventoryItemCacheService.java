package org.openelis.cache;

import org.openelis.domain.InventoryItemDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class InventoryItemCacheService implements InventoryItemCacheServiceInt,
                                      InventoryItemCacheServiceIntAsync {

    private static InventoryItemCacheService  instance;

    private InventoryItemCacheServiceIntAsync service;

    private InventoryItemCacheService() {
        service = (InventoryItemCacheServiceIntAsync)GWT.create(InventoryItemCacheServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    static InventoryItemCacheService get() {
        if (instance == null)
            instance = new InventoryItemCacheService();

        return instance;
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