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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.OrderItemMeta;

public class OrderItemMetaMap extends OrderItemMeta implements MetaMap{

    public OrderItemMetaMap() {
        super();
    }
    
    public OrderItemMetaMap(String path){
        super(path);
        INVENTORY_ITEM_META = new InventoryItemMeta(path + "inventoryItem.");
    }
    
    public InventoryItemMeta INVENTORY_ITEM_META; 
    
    public InventoryItemMeta getInventoryItem() {
        return INVENTORY_ITEM_META;
    }

    public String buildFrom(String where) {
        return "OrderItem ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"inventoryItem."))
            return INVENTORY_ITEM_META.hasColumn(name);
        return super.hasColumn(name);
    }

}
