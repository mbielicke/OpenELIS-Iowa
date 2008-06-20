package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class InventoryComponentMetaMap extends InventoryComponentMeta implements MetaMap{

    public InventoryComponentMetaMap() {
        super();
    }
    
    public InventoryComponentMetaMap(String path){
        super(path);
       
        INVENTORY_COMPONENT_ITEM = new InventoryItemMeta(path + "componentInventoryItem.");
    }
    
    public InventoryItemMeta INVENTORY_COMPONENT_ITEM;
    
    public InventoryItemMeta getInventoryItem(){
        return INVENTORY_COMPONENT_ITEM;
    }
    
    public String buildFrom(String where) {
        return "InventoryComponent ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"componentInventoryItem."))
            return INVENTORY_COMPONENT_ITEM.hasColumn(name);
        return super.hasColumn(name);
    }
}
