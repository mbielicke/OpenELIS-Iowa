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
import org.openelis.meta.InventoryComponentMeta;
import org.openelis.meta.InventoryItemMeta;

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
