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
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.NoteMeta;

public class InventoryItemMetaMap extends InventoryItemMeta implements MetaMap{

    public InventoryItemMetaMap() {
        super("inventory_item.");
        ITEM_NOTE = new NoteMeta("note.");
        INVENTORY_COMPONENT = new InventoryComponentMetaMap("inventory_component.");
        INVENTORY_LOCATION = new InventoryLocationMetaMap("inventory_location.");  
        DICTIONARY_STORE_META = new DictionaryMeta("dictStore.");
        PARENT_INVENTORY_ITEM = new InventoryItemMeta("inventory_item.parentInventoryItem.");
    }
    
    //we can assume this is for components so we dont set the component variable
    public InventoryItemMetaMap(String path) {
        super(path);
        ITEM_NOTE = new NoteMeta(path+"note.");
        INVENTORY_LOCATION = new InventoryLocationMetaMap(path+"inventory_location.");  
        DICTIONARY_STORE_META = new DictionaryMeta(path+"dictStore.");
        PARENT_INVENTORY_ITEM = new InventoryItemMeta(path+"parentInventoryItem.");
    }
    
    public NoteMeta ITEM_NOTE;
    public InventoryComponentMetaMap INVENTORY_COMPONENT;
    public InventoryLocationMetaMap INVENTORY_LOCATION;  
    public DictionaryMeta DICTIONARY_STORE_META;
    public InventoryItemMeta PARENT_INVENTORY_ITEM;
    
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
    
    public InventoryItemMeta getParentInventoryItem(){
        return PARENT_INVENTORY_ITEM;
    }
    
    public static InventoryItemMetaMap getInstance() {
        return new InventoryItemMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("inventory_component.") && INVENTORY_COMPONENT != null)
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
