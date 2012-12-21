package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StorageLocationService implements StorageLocationServiceInt,
                                   StorageLocationServiceIntAsync {
    
    static StorageLocationService instance;
    
    StorageLocationServiceIntAsync service;
    
    public static StorageLocationService get() {
        if(instance == null)
            instance = new StorageLocationService();
        
        return instance;
    }
    
    private StorageLocationService() {
        service = (StorageLocationServiceIntAsync)GWT.create(StorageLocationServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<StorageLocationManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(StorageLocationManager man, AsyncCallback<StorageLocationManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<StorageLocationManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchChildByParentStorageLocationId(Integer id,
                                                    AsyncCallback<StorageLocationChildManager> callback) {
        service.fetchChildByParentStorageLocationId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<StorageLocationManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchWithChildren(Integer id, AsyncCallback<StorageLocationManager> callback) {
        service.fetchWithChildren(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(StorageLocationManager man, AsyncCallback<StorageLocationManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void validateForDelete(StorageLocationViewDO data,
                                  AsyncCallback<StorageLocationViewDO> callback) {
        service.validateForDelete(data, callback);
    }

    @Override
    public StorageLocationManager fetchById(Integer id) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.fetchWithChildren(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        Callback<StorageLocationManager> callback;
        
        callback = new Callback<StorageLocationManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationChildManager fetchChildByParentStorageLocationId(Integer id) throws Exception {
        Callback<StorageLocationChildManager> callback;
        
        callback = new Callback<StorageLocationChildManager>();
        service.fetchChildByParentStorageLocationId(id, callback);
        return callback.getResult();
    }

    @Override
    public StorageLocationViewDO validateForDelete(StorageLocationViewDO data) throws Exception {
        Callback<StorageLocationViewDO> callback;
        
        callback = new Callback<StorageLocationViewDO>();
        service.validateForDelete(data, callback);
        return callback.getResult();
    }

}
