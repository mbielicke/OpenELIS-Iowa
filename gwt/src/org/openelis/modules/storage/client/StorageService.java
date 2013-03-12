package org.openelis.modules.storage.client;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.StorageManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StorageService implements StorageServiceInt, StorageServiceIntAsync {
    
    static StorageService instance;
    
    StorageServiceIntAsync service;
    
    public static StorageService get() {
        if(instance == null)
            instance = new StorageService();
        
        return instance;
    }
    
    private StorageService() {
        service = (StorageServiceIntAsync)GWT.create(StorageServiceInt.class);
    }

    @Override
    public void fetchAvailableByName(String search,
                                     AsyncCallback<ArrayList<StorageLocationViewDO>> callback) {
        service.fetchAvailableByName(search, callback);
    }

    @Override
    public void fetchById(Query query, AsyncCallback<StorageManager> callback) {
        service.fetchById(query, callback);
    }

    @Override
    public void fetchCurrentByLocationId(Integer id, AsyncCallback<StorageManager> callback) {
        service.fetchCurrentByLocationId(id, callback);
    }

    @Override
    public void fetchHistoryByLocationId(Query query, AsyncCallback<StorageManager> callback) {
        service.fetchHistoryByLocationId(query, callback);
    }

    @Override
    public void update(StorageManager man, AsyncCallback<StorageManager> callback) {
        service.update(man, callback);
    }

    @Override
    public StorageManager fetchById(Query query) throws Exception {
        Callback<StorageManager> callback;
        
        callback = new Callback<StorageManager>();
        service.fetchById(query,callback);
        return callback.getResult();
    }

    @Override
    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {
        Callback<StorageManager> callback;
        
        callback = new Callback<StorageManager>();
        service.fetchCurrentByLocationId(id, callback);
        return callback.getResult();
    }

    @Override
    public StorageManager fetchHistoryByLocationId(Query query) throws Exception {
        Callback<StorageManager> callback;
        
        callback = new Callback<StorageManager>();
        service.fetchHistoryByLocationId(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<StorageLocationViewDO> fetchAvailableByName(String search) throws Exception {
        Callback<ArrayList<StorageLocationViewDO>> callback;
        
        callback = new Callback<ArrayList<StorageLocationViewDO>>();
        service.fetchAvailableByName(search, callback);
        return callback.getResult();
    }

    @Override
    public StorageManager update(StorageManager man) throws Exception {
        Callback<StorageManager> callback;
        
        callback = new Callback<StorageManager>();
        service.update(man, callback);
        return callback.getResult();
    }

}
