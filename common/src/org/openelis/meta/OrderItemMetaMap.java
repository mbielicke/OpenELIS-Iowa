package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

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
