package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryItemServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<InventoryItemManager> callback);

    void add(InventoryItemManager man, AsyncCallback<InventoryItemManager> callback);

    void fetchActiveByName(String search, AsyncCallback<ArrayList<InventoryItemDO>> callback);

    void fetchActiveByNameAndStore(Query query, AsyncCallback<ArrayList<InventoryItemDO>> callback);

    void fetchActiveByNameStoreAndParentInventoryItem(Query query,
                                                      AsyncCallback<ArrayList<InventoryItemDO>> callback);

    void fetchById(Integer id, AsyncCallback<InventoryItemManager> callback);

    void fetchComponentByInventoryItemId(Integer id,
                                         AsyncCallback<InventoryComponentManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<InventoryItemManager> callback);

    void fetchInventoryItemById(Integer id, AsyncCallback<InventoryItemViewDO> callback);

    void fetchLocationByInventoryItemId(Integer id, AsyncCallback<InventoryLocationManager> callback);

    void fetchWithComponents(Integer id, AsyncCallback<InventoryItemManager> callback);

    void fetchWithLocations(Integer id, AsyncCallback<InventoryItemManager> callback);

    void fetchWithManufacturing(Integer id, AsyncCallback<InventoryItemManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<InventoryItemManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameStoreVO>> callback);

    void update(InventoryItemManager man, AsyncCallback<InventoryItemManager> callback);

}
