package org.openelis.modules.storageunit.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageUnitDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StorageUnitService implements StorageUnitServiceInt, StorageUnitServiceIntAsync {
    
    static StorageUnitService instance;
    
    StorageUnitServiceIntAsync service;
    
    public static StorageUnitService get() {
        if(instance == null)
            instance = new StorageUnitService();
        
        return instance;
    }
    
    private StorageUnitService() {
        service = (StorageUnitServiceIntAsync)GWT.create(StorageUnitServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<StorageUnitDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(StorageUnitDO data, AsyncCallback<StorageUnitDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(StorageUnitDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByDescription(search, callback);
        
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<StorageUnitDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<StorageUnitDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(StorageUnitDO data, AsyncCallback<StorageUnitDO> callback) {
        service.update(data, callback);
    }

    @Override
    public StorageUnitDO fetchById(Integer id) throws Exception {
        Callback<StorageUnitDO> callback;
        
        callback = new Callback<StorageUnitDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByDescription(search, callback);
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
    public StorageUnitDO add(StorageUnitDO data) throws Exception {
        Callback<StorageUnitDO> callback;
        
        callback = new Callback<StorageUnitDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public StorageUnitDO update(StorageUnitDO data) throws Exception {
        Callback<StorageUnitDO> callback;
        
        callback = new Callback<StorageUnitDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public StorageUnitDO fetchForUpdate(Integer id) throws Exception {
        Callback<StorageUnitDO> callback;
        
        callback = new Callback<StorageUnitDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(StorageUnitDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
        callback.getResult();
    }

    @Override
    public StorageUnitDO abortUpdate(Integer id) throws Exception {
        Callback<StorageUnitDO> callback;
        
        callback = new Callback<StorageUnitDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
