package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("inventoryLocation")
public interface InventoryLocationServiceInt extends XsrfProtectedService {

    ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemId(Query query) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemIdStoreId(Query query) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByInventoryItemName(String search) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByInventoryItemNameStoreId(Query query) throws Exception;

    InventoryLocationViewDO fetchById(Integer id) throws Exception;

}