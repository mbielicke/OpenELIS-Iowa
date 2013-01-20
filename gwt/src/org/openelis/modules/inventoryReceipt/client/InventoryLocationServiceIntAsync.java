package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryLocationServiceIntAsync {

    void fetchById(Integer id, AsyncCallback<InventoryLocationViewDO> callback);

    void fetchByInventoryItemName(String search,
                                  AsyncCallback<ArrayList<InventoryLocationViewDO>> callback);

    void fetchByInventoryItemNameStoreId(Query query,
                                         AsyncCallback<ArrayList<InventoryLocationViewDO>> callback);

    void fetchByLocationNameInventoryItemId(Query query,
                                            AsyncCallback<ArrayList<InventoryLocationViewDO>> callback);

    void fetchByLocationNameInventoryItemIdStoreId(Query query,
                                                   AsyncCallback<ArrayList<InventoryLocationViewDO>> callback);

}
