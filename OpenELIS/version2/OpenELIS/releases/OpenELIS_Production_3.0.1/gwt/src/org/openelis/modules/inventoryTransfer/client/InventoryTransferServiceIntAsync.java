package org.openelis.modules.inventoryTransfer.client;

import org.openelis.manager.InventoryTransferManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryTransferServiceIntAsync {

    void add(InventoryTransferManager man, AsyncCallback<InventoryTransferManager> callback);

}
