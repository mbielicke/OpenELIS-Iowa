/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.OrderItemMeta;
import org.openelis.meta.OrderMeta;

public class OrderItemMetaMap extends OrderItemMeta implements MetaMap{

    public OrderItemMetaMap() {
        super();
    }
    
    public OrderItemMetaMap(String path){
        super(path);
        INVENTORY_ITEM_META = new InventoryItemMeta(path + "inventoryItem.");
        ORDER_META = new OrderMetaMap(path+"order.", false);
    }
    
    public InventoryItemMeta INVENTORY_ITEM_META;
    
    public OrderMetaMap ORDER_META;
    
    public InventoryItemMeta getInventoryItem() {
        return INVENTORY_ITEM_META;
    }
    
    public OrderMetaMap getOrder(){
        return ORDER_META;
    }

    public String buildFrom(String where) {
        return "OrderItem ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"inventoryItem."))
            return INVENTORY_ITEM_META.hasColumn(name);
        if(name.startsWith(path+"order."))
            return ORDER_META.hasColumn(name);
        return super.hasColumn(name);
    }

}
