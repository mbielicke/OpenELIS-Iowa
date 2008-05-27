package org.openelis.modules.inventoryItem.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AutoCompleteParamsInt;

public class InventoryComponentAutoParams implements AutoCompleteParamsInt{

    public HashMap getParams(FormRPC rpc) {
        HashMap params = new HashMap();
        params.put("id", rpc.getField("inventoryItem.id"));
        params.put("name", rpc.getField("inventoryItem.name"));
        params.put("store", rpc.getField("inventoryItem.store"));
        
        return params;
    }
}
