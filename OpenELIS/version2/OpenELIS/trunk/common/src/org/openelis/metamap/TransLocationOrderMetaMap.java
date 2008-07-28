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
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TransLocationOrderMeta;

public class TransLocationOrderMetaMap extends TransLocationOrderMeta implements MetaMap{
    public TransLocationOrderMetaMap() {
        super();
    }
    
    public TransLocationOrderMetaMap(String path){
        super(path);
        ORDER_ITEM_META = new OrderItemMetaMap(path + "orderItem.");
        INVENTORY_LOCATION_META = new InventoryLocationMetaMap(path + "inventoryLocation.");
    }
    
    public OrderItemMetaMap ORDER_ITEM_META; 
    public InventoryLocationMetaMap INVENTORY_LOCATION_META;
    
    public OrderItemMetaMap getOrderItem() {
        return ORDER_ITEM_META;
    }
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION_META;
    }

    public String buildFrom(String where) {
        return "TransLocationOrder ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"orderItem."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith(path+"toLocation."))
            return INVENTORY_LOCATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
}