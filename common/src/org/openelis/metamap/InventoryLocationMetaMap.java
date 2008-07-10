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
import org.openelis.meta.InventoryLocationMeta;
import org.openelis.meta.StorageLocationMeta;

public class InventoryLocationMetaMap extends InventoryLocationMeta implements MetaMap{

    public InventoryLocationMetaMap() {
        super();
    }
    
    public InventoryLocationMetaMap(String path){
        super(path);
        
        INVENTORY_LOCATION_STORAGE_LOCATION = new StorageLocationMeta(path + "storageLocation.");
    }
    
    public StorageLocationMeta INVENTORY_LOCATION_STORAGE_LOCATION;
    
    public StorageLocationMeta getStorageLocation() {
        return INVENTORY_LOCATION_STORAGE_LOCATION;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"storageLocation."))
            return INVENTORY_LOCATION_STORAGE_LOCATION.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        return "InventoryLocation ";
    }

}
