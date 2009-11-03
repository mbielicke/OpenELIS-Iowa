package org.openelis.domain;

public class StorageLocationViewDO extends StorageLocationDO {

    private static final long serialVersionUID = 1L;

    protected String storageUnitDescription;
    
    public StorageLocationViewDO(){
        
    }
    
    public StorageLocationViewDO(Integer id, Integer sortOrder, String name, String location,
                                 Integer parentStorageLocationId, Integer storageUnitId,
                                 String isAvailable, String storageUnitDescription){
        super(id, sortOrder, name, location, parentStorageLocationId, storageUnitId, isAvailable);
        
        setStorageUnitDescription(storageUnitDescription);
        
    }

    public String getStorageUnitDescription() {
        return storageUnitDescription;
    }

    public void setStorageUnitDescription(String storageUnitDescription) {
        this.storageUnitDescription = storageUnitDescription;
    }
}
