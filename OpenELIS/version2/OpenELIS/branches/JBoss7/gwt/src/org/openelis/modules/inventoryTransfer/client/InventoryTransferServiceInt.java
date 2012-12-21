package org.openelis.modules.inventoryTransfer.client;

import org.openelis.manager.InventoryTransferManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inventoryTransfer")
public interface InventoryTransferServiceInt extends RemoteService {

    InventoryTransferManager add(InventoryTransferManager man) throws Exception;

}