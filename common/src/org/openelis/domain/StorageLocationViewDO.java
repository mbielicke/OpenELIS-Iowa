package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class StorageLocationViewDO extends StorageLocationDO {

    private static final long serialVersionUID = 1L;

    protected String          storageUnitDescription, parentStorageLocationName;

    public StorageLocationViewDO() {

    }

    public StorageLocationViewDO(Integer id, Integer sortOrder, String name, String location,
                                 Integer parentStorageLocationId, Integer storageUnitId,
                                 String isAvailable,String parentStorageLocationName,
                                 String storageUnitDescription) {
        super(id, sortOrder, name, location, parentStorageLocationId, storageUnitId, isAvailable);

        setParentStorageLocationName(parentStorageLocationName);
        setStorageUnitDescription(storageUnitDescription);
    }

    public String getStorageUnitDescription() {
        return storageUnitDescription;
    }

    public void setStorageUnitDescription(String storageUnitDescription) {
        this.storageUnitDescription = DataBaseUtil.trim(storageUnitDescription);
    }

    public String getParentStorageLocationName() {
        return parentStorageLocationName;
    }

    public void setParentStorageLocationName(String parentStorageLocationName) {
        this.parentStorageLocationName = DataBaseUtil.trim(parentStorageLocationName);
    }

}
