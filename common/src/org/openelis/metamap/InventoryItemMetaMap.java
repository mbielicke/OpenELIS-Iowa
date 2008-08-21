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
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.NoteMeta;

public class InventoryItemMetaMap extends InventoryItemMeta implements MetaMap{

    public InventoryItemMetaMap() {
        super("inventory_item.");
    }
    
    public NoteMeta ITEM_NOTE = new NoteMeta("note.");
    
    public InventoryComponentMetaMap INVENTORY_COMPONENT = new InventoryComponentMetaMap("inventory_component.");
    
    public InventoryLocationMetaMap INVENTORY_LOCATION = new InventoryLocationMetaMap("inventory_location.");  
    
    public DictionaryMeta DICTIONARY_STORE_META = new DictionaryMeta("dictStore.");
    
    public NoteMeta getNote(){
        return ITEM_NOTE;
    }
    
    public InventoryComponentMetaMap getInventoryComponent(){
        return INVENTORY_COMPONENT;
    }
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION;
    }
    
    public DictionaryMeta getStoreDict(){
        return DICTIONARY_STORE_META;
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
        if(name.startsWith("dictStore."))
            return DICTIONARY_STORE_META.hasColumn(name);
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
        from += ", Dictionary dictStore ";
        return from;
    }
}
