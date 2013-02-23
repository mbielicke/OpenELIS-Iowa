package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryReceiptManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inventoryReceipt")
public interface InventoryReceiptServiceInt extends RemoteService {

    ArrayList<IdNameVO> fetchByUpc(String search) throws Exception;

    ArrayList<InventoryReceiptManager> query(Query query) throws Exception;

    InventoryReceiptManager add(InventoryReceiptManager man) throws Exception;

    InventoryReceiptManager update(InventoryReceiptManager man) throws Exception;

    InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception;

    InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception;

}