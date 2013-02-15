package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inventoryLocation")
public interface InventoryLocationServiceInt extends RemoteService {

    ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemId(Query query) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemIdStoreId(Query query) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByInventoryItemName(String search) throws Exception;

    ArrayList<InventoryLocationViewDO> fetchByInventoryItemNameStoreId(Query query) throws Exception;

    InventoryLocationViewDO fetchById(Integer id) throws Exception;

}