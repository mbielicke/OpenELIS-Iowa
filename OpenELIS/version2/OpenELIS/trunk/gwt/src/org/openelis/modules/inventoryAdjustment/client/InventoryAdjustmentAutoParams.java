package org.openelis.modules.inventoryAdjustment.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AutoCompleteParamsInt;
import org.openelis.metamap.InventoryAdjustmentMetaMap;

public class InventoryAdjustmentAutoParams implements AutoCompleteParamsInt{

    InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();

    public HashMap getParams(FormRPC rpc) {
        HashMap params = new HashMap();
        params.put("storeId", 
                   rpc.getField(InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId()));
        
        return params;
    }
    
}
