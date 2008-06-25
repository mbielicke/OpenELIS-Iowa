package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

public class StorageUnitMetaMap extends StorageUnitMeta implements MetaMap{
    
    public StorageUnitMetaMap() {
        super("storageUnit.");
    }

    public boolean hasColumn(String name){
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        return "StorageUnit storageUnit ";
    }
}
