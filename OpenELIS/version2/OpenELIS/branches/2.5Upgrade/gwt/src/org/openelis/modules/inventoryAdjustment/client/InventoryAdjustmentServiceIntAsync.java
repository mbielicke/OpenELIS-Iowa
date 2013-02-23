package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryAdjustmentServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<InventoryAdjustmentManager> callback);

    void add(InventoryAdjustmentManager man, AsyncCallback<InventoryAdjustmentManager> callback);

    void fetchAdjustmentByInventoryAdjustmentId(Integer id,
                                                AsyncCallback<InventoryXAdjustManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<InventoryAdjustmentManager> callback);

    void fetchWithAdjustments(Integer id, AsyncCallback<InventoryAdjustmentManager> callback);

    void query(Query query, AsyncCallback<ArrayList<InventoryAdjustmentDO>> callback);

    void update(InventoryAdjustmentManager man, AsyncCallback<InventoryAdjustmentManager> callback);

}
