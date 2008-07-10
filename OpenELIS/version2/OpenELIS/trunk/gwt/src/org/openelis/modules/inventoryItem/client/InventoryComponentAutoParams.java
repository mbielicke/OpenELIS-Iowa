/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
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
