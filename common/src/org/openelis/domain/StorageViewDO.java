package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;

public class StorageViewDO extends StorageDO {
    private static final long serialVersionUID = 1L;

    protected String          storageLocationName, storageLocationLocation,
                              storageLocationParentStorageLocationName, storageUnitDescription,
                              userName, itemDescription;

    public StorageViewDO(){
        
    }
    
    public StorageViewDO(Integer id, Integer referenceId, Integer referenceTableId,
                         Integer storageLocationId, Date checkin, Date checkout, Integer systemUserId,
                         String storageLocationName, String storageLocationLocation, 
                         String storageLocationParentStorageLocationName, String storageUnitDescription){
      
        super(id, referenceId, referenceTableId, storageLocationId, checkin, checkout, systemUserId);
        setStorageLocationName(storageLocationName);
        setStorageLocationLocation(storageLocationLocation);
        setStorageLocationParentStorageLocationName(storageLocationParentStorageLocationName);
        setStorageUnitDescription(storageUnitDescription);
    }
    
    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = DataBaseUtil.trim(storageLocationName);
    }
    
    public String getStorageLocationLocation() {
        return storageLocationLocation;
    }

    public void setStorageLocationLocation(String storageLocationLocation) {
        this.storageLocationLocation = DataBaseUtil.trim(storageLocationLocation);
    }
    
    public String getStorageUnitDescription() {
        return storageUnitDescription;
    }

    public void setStorageUnitDescription(String storageUnitDescription) {
        this.storageUnitDescription = DataBaseUtil.trim(storageUnitDescription);
    }

    public String getStorageLocationParentStorageLocationName() {
        return storageLocationParentStorageLocationName;
    }

    public void setStorageLocationParentStorageLocationName(String storageLocationParentStorageLocationName) {
        this.storageLocationParentStorageLocationName = DataBaseUtil.trim(storageLocationParentStorageLocationName);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = DataBaseUtil.trim(userName);
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = DataBaseUtil.trim(itemDescription);
    }
}
