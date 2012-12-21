package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryAdjustmentService implements InventoryAdjustmentServiceInt, InventoryAdjustmentServiceIntAsync{
    
    static InventoryAdjustmentService instance;
    
    InventoryAdjustmentServiceIntAsync service;
    
    public static InventoryAdjustmentService get() {
        if (instance == null)
            instance = new InventoryAdjustmentService();
        
        return instance;
    }
    
    private InventoryAdjustmentService() {
        service = (InventoryAdjustmentServiceIntAsync)GWT.create(InventoryAdjustmentServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<InventoryAdjustmentManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(InventoryAdjustmentManager man,
                    AsyncCallback<InventoryAdjustmentManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchAdjustmentByInventoryAdjustmentId(Integer id,
                                                       AsyncCallback<InventoryXAdjustManager> callback) {
        service.fetchAdjustmentByInventoryAdjustmentId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<InventoryAdjustmentManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchWithAdjustments(Integer id, AsyncCallback<InventoryAdjustmentManager> callback) {
        service.fetchWithAdjustments(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<InventoryAdjustmentDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(InventoryAdjustmentManager man,
                       AsyncCallback<InventoryAdjustmentManager> callback) {
        service.update(man, callback);
    }

    @Override
    public InventoryAdjustmentManager fetchWithAdjustments(Integer id) throws Exception {
        Callback<InventoryAdjustmentManager> callback;
        
        callback = new Callback<InventoryAdjustmentManager>();
        service.fetchWithAdjustments(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryAdjustmentDO> query(Query query) throws Exception {
        Callback<ArrayList<InventoryAdjustmentDO>> callback;
        
        callback = new Callback<ArrayList<InventoryAdjustmentDO>>();
        service.query(query, callback);
        return callback.getResult();    }

    @Override
    public InventoryAdjustmentManager add(InventoryAdjustmentManager man) throws Exception {
        Callback<InventoryAdjustmentManager> callback;
        
        callback = new Callback<InventoryAdjustmentManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryAdjustmentManager update(InventoryAdjustmentManager man) throws Exception {
        Callback<InventoryAdjustmentManager> callback;
        
        callback = new Callback<InventoryAdjustmentManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryAdjustmentManager fetchForUpdate(Integer id) throws Exception {
        Callback<InventoryAdjustmentManager> callback;
        
        callback = new Callback<InventoryAdjustmentManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryAdjustmentManager abortUpdate(Integer id) throws Exception {
        Callback<InventoryAdjustmentManager> callback;
        
        callback = new Callback<InventoryAdjustmentManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryXAdjustManager fetchAdjustmentByInventoryAdjustmentId(Integer id) throws Exception {
        Callback<InventoryXAdjustManager> callback;
        
        callback = new Callback<InventoryXAdjustManager>();
        service.fetchAdjustmentByInventoryAdjustmentId(id, callback);
        return callback.getResult();
    }
    
    

}
