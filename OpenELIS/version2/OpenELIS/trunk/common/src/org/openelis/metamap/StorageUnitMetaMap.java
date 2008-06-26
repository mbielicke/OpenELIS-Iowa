package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.StorageUnitMeta;

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
