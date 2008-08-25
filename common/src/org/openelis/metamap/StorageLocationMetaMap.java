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
import org.openelis.meta.StorageLocationMeta;
import org.openelis.meta.StorageUnitMeta;

public class StorageLocationMetaMap extends StorageLocationMeta implements MetaMap{
    private boolean child = false;
    
    public StorageLocationMetaMap() {
        super("storageLocation.");
        CHILD_STORAGE_LOCATION_META = new StorageLocationMetaMap("childStorageLocation.");
        STORAGE_UNIT_META = new StorageUnitMeta("storageUnit.");
    }
    
    public StorageLocationMetaMap(String path) {
        super(path);
        STORAGE_UNIT_META = new StorageUnitMeta(path+"storageUnit.");
        child = true;
    }
    
    public StorageLocationMetaMap CHILD_STORAGE_LOCATION_META;    
    public StorageUnitMeta STORAGE_UNIT_META;
    
    public StorageLocationMetaMap getChildStorageLocation() {
        return CHILD_STORAGE_LOCATION_META;
    }

    public StorageUnitMeta getStorageUnit() {
        return STORAGE_UNIT_META;
    }
    
    public static StorageLocationMetaMap getInstance() {
        return new StorageLocationMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("childStorageLocation.storageUnit.description"))
            return CHILD_STORAGE_LOCATION_META.STORAGE_UNIT_META.hasColumn(name);  
        if(name.startsWith("childStorageLocation.") && !child)
            return CHILD_STORAGE_LOCATION_META.hasColumn(name);
        if(name.startsWith("storageUnit."))
            return STORAGE_UNIT_META.hasColumn(name);
                 
       
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "StorageLocation storageLocation ";
        if(name.indexOf("childStorageLocation.") > -1)
            from += ", IN (storageLocation.childLocations) childStorageLocation ";
        if(name.indexOf("storageUnit.") > -1)
            from += ", IN (storageLocation.storageUnit) storageUnit "; 
        if(name.indexOf("childStorageLocation.") > -1 || name.indexOf("childUnit.") > -1)
            from += ", IN (storageLocation.childLocations) childStorageLocation";
        if(name.indexOf("childUnit.") > -1)
            from += ", IN (childStorageLocation.storageUnit) childUnit "; 
        return from;
    }
}
