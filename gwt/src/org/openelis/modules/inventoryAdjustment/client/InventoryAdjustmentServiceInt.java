package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("inventoryAdjustment")
public interface InventoryAdjustmentServiceInt extends XsrfProtectedService {

    InventoryAdjustmentManager fetchWithAdjustments(Integer id) throws Exception;

    ArrayList<InventoryAdjustmentDO> query(Query query) throws Exception;

    InventoryAdjustmentManager add(InventoryAdjustmentManager man) throws Exception;

    InventoryAdjustmentManager update(InventoryAdjustmentManager man) throws Exception;

    InventoryAdjustmentManager fetchForUpdate(Integer id) throws Exception;

    InventoryAdjustmentManager abortUpdate(Integer id) throws Exception;

    InventoryXAdjustManager fetchAdjustmentByInventoryAdjustmentId(Integer id) throws Exception;

}