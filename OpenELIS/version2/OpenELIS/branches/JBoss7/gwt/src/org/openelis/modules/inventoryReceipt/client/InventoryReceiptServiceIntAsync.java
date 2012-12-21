package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryReceiptManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InventoryReceiptServiceIntAsync {

    void abortUpdate(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback);

    void add(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback);

    void fetchByUpc(String search, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchForUpdate(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback);

    void query(Query query, AsyncCallback<ArrayList<InventoryReceiptManager>> callback);

    void update(InventoryReceiptManager man, AsyncCallback<InventoryReceiptManager> callback);

}
