
package org.openelis.newmeta;

/**
  * StorageLocation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class StorageLocationMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "StorageLocation";
	
	private static final String
              ID					="id",
              SORT_ORDER_ID					="sortOrderId",
              NAME					="name",
              LOCATION					="location",
              PARENT_STORAGE_LOCATION_ID					="parentStorageLocationId",
              STORAGE_UNIT_ID					="storageUnitId",
              IS_AVAILABLE					="isAvailable";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,NAME,LOCATION,PARENT_STORAGE_LOCATION_ID,STORAGE_UNIT_ID,IS_AVAILABLE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public StorageLocationMeta() {
		init();        
    }
    
    public StorageLocationMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getLocation() {
        return path + LOCATION;
    } 

    public String getParentStorageLocationId() {
        return path + PARENT_STORAGE_LOCATION_ID;
    } 

    public String getStorageUnitId() {
        return path + STORAGE_UNIT_ID;
    } 

    public String getIsAvailable() {
        return path + IS_AVAILABLE;
    } 

  
}   
