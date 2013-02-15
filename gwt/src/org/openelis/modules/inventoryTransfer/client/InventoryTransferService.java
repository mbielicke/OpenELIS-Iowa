package org.openelis.modules.inventoryTransfer.client;

import org.openelis.gwt.screen.Callback;
import org.openelis.manager.InventoryTransferManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryTransferService implements InventoryTransferServiceInt, InventoryTransferServiceIntAsync {
    
    static InventoryTransferService instance;
    
    InventoryTransferServiceIntAsync service;
    
    public static InventoryTransferService get() {
        if(instance == null)
            instance = new InventoryTransferService();
        
        return instance;
    }
    
    private InventoryTransferService() {
        service = (InventoryTransferServiceIntAsync)GWT.create(InventoryTransferServiceInt.class);
    }

    @Override
    public void add(InventoryTransferManager man, AsyncCallback<InventoryTransferManager> callback) {
        service.add(man, callback);
    }

    @Override
    public InventoryTransferManager add(InventoryTransferManager man) throws Exception {
        Callback<InventoryTransferManager> callback;
        
        callback = new Callback<InventoryTransferManager>();
        service.add(man, callback);
        return callback.getResult();
    }
    
    

}
