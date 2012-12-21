package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inventoryItem")
public interface InventoryItemServiceInt extends RemoteService {

    InventoryItemManager fetchById(Integer id) throws Exception;

    ArrayList<InventoryItemDO> fetchActiveByName(String search) throws Exception;

    ArrayList<InventoryItemDO> fetchActiveByNameAndStore(Query query) throws Exception;

    ArrayList<InventoryItemDO> fetchActiveByNameStoreAndParentInventoryItem(Query query) throws Exception;

    InventoryItemViewDO fetchInventoryItemById(Integer id) throws Exception;

    InventoryItemManager fetchWithComponents(Integer id) throws Exception;

    InventoryItemManager fetchWithLocations(Integer id) throws Exception;

    InventoryItemManager fetchWithManufacturing(Integer id) throws Exception;

    InventoryItemManager fetchWithNotes(Integer id) throws Exception;

    ArrayList<IdNameStoreVO> query(Query query) throws Exception;

    InventoryItemManager add(InventoryItemManager man) throws Exception;

    InventoryItemManager update(InventoryItemManager man) throws Exception;

    InventoryItemManager fetchForUpdate(Integer id) throws Exception;

    InventoryItemManager abortUpdate(Integer id) throws Exception;

    //
    // support for InventoryComponentManager and InventoryLocationManager
    //
    InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception;

    InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception;

}