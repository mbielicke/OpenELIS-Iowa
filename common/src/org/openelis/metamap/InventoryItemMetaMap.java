package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.NoteMeta;

public class InventoryItemMetaMap extends InventoryItemMeta implements MetaMap{

    public InventoryItemMetaMap() {
        super("inventory_item.");
    }
    
    public NoteMeta ITEM_NOTE = new NoteMeta("note.");
    
    public InventoryComponentMetaMap INVENTORY_COMPONENT = new InventoryComponentMetaMap("inventory_component.");
    
    public InventoryLocationMetaMap INVENTORY_LOCATION = new InventoryLocationMetaMap("inventory_location.");  
    
    public NoteMeta getNote(){
        return ITEM_NOTE;
    }
    
    public InventoryComponentMetaMap getInventoryComponent(){
        return INVENTORY_COMPONENT;
    }
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION;
    }
    
    public static InventoryItemMetaMap getInstance() {
        return new InventoryItemMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("inventory_component."))
            return INVENTORY_COMPONENT.hasColumn(name);
        if(name.startsWith("inventory_location."))
            return INVENTORY_LOCATION.hasColumn(name);
        if(name.startsWith("note."))
            return ITEM_NOTE.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "InventoryItem inventory_item ";
        if(name.indexOf("note.") > -1)
            from += ", IN (inventory_item.note) note ";
        if(name.indexOf("inventory_component.") > -1)
            from += ", IN (inventory_item.inventoryComponent) inventory_component ";
        if(name.indexOf("inventory_location.") > -1)
            from += ", IN (inventory_item.inventoryLocation) inventory_location";
        return from;
    }
}
