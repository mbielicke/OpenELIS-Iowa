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
