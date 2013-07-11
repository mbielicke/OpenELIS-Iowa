package org.openelis.modules.inventoryTransfer.client;

import org.openelis.manager.InventoryTransferManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("inventoryTransfer")
public interface InventoryTransferServiceInt extends XsrfProtectedService {

    InventoryTransferManager add(InventoryTransferManager man) throws Exception;

}