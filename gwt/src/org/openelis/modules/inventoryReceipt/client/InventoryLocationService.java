package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryLocationService implements InventoryLocationServiceInt, InventoryLocationServiceIntAsync {
    
    static InventoryLocationService instance;
    
    InventoryLocationServiceIntAsync service;
    
    public static InventoryLocationService get() {
        if (instance == null)
            instance = new InventoryLocationService();
        
        return instance;
    }
    
    private InventoryLocationService() {
        service = (InventoryLocationServiceIntAsync)GWT.create(InventoryLocationServiceInt.class);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<InventoryLocationViewDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByInventoryItemName(String search,
                                         AsyncCallback<ArrayList<InventoryLocationViewDO>> callback) {
        service.fetchByInventoryItemName(search, callback);
    }

    @Override
    public void fetchByInventoryItemNameStoreId(Query query,
                                                AsyncCallback<ArrayList<InventoryLocationViewDO>> callback) {
        service.fetchByInventoryItemNameStoreId(query, callback);
    }

    @Override
    public void fetchByLocationNameInventoryItemId(Query query,
                                                   AsyncCallback<ArrayList<InventoryLocationViewDO>> callback) {
        service.fetchByLocationNameInventoryItemId(query, callback);
    }

    @Override
    public void fetchByLocationNameInventoryItemIdStoreId(Query query,
                                                          AsyncCallback<ArrayList<InventoryLocationViewDO>> callback) {
        service.fetchByLocationNameInventoryItemIdStoreId(query, callback);
    }

    @Override
    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemId(Query query) throws Exception {
        Callback<ArrayList<InventoryLocationViewDO>> callback;
        
        callback = new Callback<ArrayList<InventoryLocationViewDO>>();
        service.fetchByLocationNameInventoryItemId(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemIdStoreId(Query query) throws Exception {
        Callback<ArrayList<InventoryLocationViewDO>> callback;
        
        callback = new Callback<ArrayList<InventoryLocationViewDO>>();
        service.fetchByLocationNameInventoryItemIdStoreId(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemName(String search) throws Exception {
        Callback<ArrayList<InventoryLocationViewDO>> callback;
        
        callback = new Callback<ArrayList<InventoryLocationViewDO>>();
        service.fetchByInventoryItemName(search, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemNameStoreId(Query query) throws Exception {
        Callback<ArrayList<InventoryLocationViewDO>> callback;
        
        callback = new Callback<ArrayList<InventoryLocationViewDO>>();
        service.fetchByInventoryItemNameStoreId(query, callback);
        return callback.getResult();
    }

    @Override
    public InventoryLocationViewDO fetchById(Integer id) throws Exception {
        Callback<InventoryLocationViewDO> callback;
        
        callback = new Callback<InventoryLocationViewDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }
    
}
