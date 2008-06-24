package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class StorageLocationMetaMap extends StorageLocationMeta implements MetaMap{
    private boolean child = false;
    
    public StorageLocationMetaMap() {
        super("storageLocation.");
        CHILD_STORAGE_LOCATION_META = new StorageLocationMetaMap("childStorageLocation.");
        STORAGE_UNIT_META = new StorageUnitMeta("storageUnit.");
    }
    
    public StorageLocationMetaMap(String path) {
        super(path);
        STORAGE_UNIT_META = new StorageUnitMeta("childUnit.");
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
        if(name.startsWith("childStorageLocation.") && !child)
            return CHILD_STORAGE_LOCATION_META.hasColumn(name);
        if(name.startsWith("storageUnit."))
            return STORAGE_UNIT_META.hasColumn(name);
        if(name.startsWith("childUnit."))
            return CHILD_STORAGE_LOCATION_META.STORAGE_UNIT_META.hasColumn(name);           
       
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
