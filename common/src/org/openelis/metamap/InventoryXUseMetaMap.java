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
import org.openelis.meta.InventoryXUseMeta;

public class InventoryXUseMetaMap extends InventoryXUseMeta implements MetaMap{
    public InventoryXUseMetaMap() {
        super();
    }
    
    public InventoryXUseMetaMap(String path){
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
        return "InventoryXUse ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"orderItem."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith(path+"toLocation."))
            return INVENTORY_LOCATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
}
