package org.openelis.modules.inventoryItem.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AutoCompleteParamsInt;

public class InventoryComponentAutoParams implements AutoCompleteParamsInt{

    public HashMap getParams(FormRPC rpc) {
        HashMap params = new HashMap();
        params.put("id", rpc.getField("inventory_item.id"));
        params.put("name", rpc.getField("inventory_item.name"));
        params.put("store", rpc.getField("inventory_item.storeId"));
        
        return params;
    }
}
