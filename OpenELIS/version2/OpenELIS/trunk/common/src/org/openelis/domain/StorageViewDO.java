package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;

public class StorageViewDO extends StorageDO {
    private static final long serialVersionUID = 1L;
    
    protected String storageLocation, userName, itemDescription;

    public StorageViewDO(){
        
    }
    
    public StorageViewDO(Integer id, Integer referenceId, Integer referenceTableId,
                         Integer storageLocationId, Date checkin, Date checkout, Integer systemUserId,
                         String storageLocName, String StorageLocLocation, String parentLocName, String unitDesc){
      
        super(id, referenceId, referenceTableId, storageLocationId, checkin, checkout, systemUserId);
      
//        setStorageLocation(DataBaseUtil.formatStorageLocation(storageLocName, StorageLocLocation, unitDesc, parentLocName));        
    }
    
    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
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
        this.itemDescription = itemDescription;
    }
}
