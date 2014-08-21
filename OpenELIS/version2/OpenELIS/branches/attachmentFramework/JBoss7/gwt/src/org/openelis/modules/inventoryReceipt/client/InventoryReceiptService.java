package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.InventoryReceiptManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryReceiptService implements InventoryReceiptServiceInt, InventoryReceiptServiceIntAsync {
    
    static InventoryReceiptService instance;
    
    InventoryReceiptServiceIntAsync service;
    
    public static InventoryReceiptService get() {
        if (instance == null) 
            instance = new InventoryReceiptService();
        
        return instance;
    }
    
    private InventoryReceiptService() {
        service = (InventoryReceiptServiceIntAsync)GWT.create(InventoryReceiptServiceInt.class);
    }

    @Override
    public void abortUpdate(InventoryReceiptManager man,
                            AsyncCallback<InventoryReceiptManager> callback) {
        service.abortUpdate(man, callback);
    }

    @Override
    public void add(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchByUpc(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByUpc(search, callback);
    }

    @Override
    public void fetchForUpdate(InventoryReceiptManager man,
                               AsyncCallback<InventoryReceiptManager> callback) {
        service.fetchForUpdate(man, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<InventoryReceiptManager>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchByUpc(String search) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByUpc(search, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryReceiptManager> query(Query query) throws Exception {
        Callback<ArrayList<InventoryReceiptManager>> callback;
        
        callback = new Callback<ArrayList<InventoryReceiptManager>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        Callback<InventoryReceiptManager> callback;
        
        callback = new Callback<InventoryReceiptManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        Callback<InventoryReceiptManager> callback;
        
        callback = new Callback<InventoryReceiptManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        Callback<InventoryReceiptManager> callback;
        
        callback = new Callback<InventoryReceiptManager>();
        service.fetchForUpdate(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        Callback<InventoryReceiptManager> callback;
        
        callback = new Callback<InventoryReceiptManager>();
        service.abortUpdate(man, callback);
        return callback.getResult();
    }
    
}
