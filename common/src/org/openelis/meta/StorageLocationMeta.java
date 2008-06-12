
package org.openelis.meta;

/**
  * StorageLocation META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class StorageLocationMeta implements Meta {
  	private static final String tableName = "storage_location";
	private static final String entityName = "StorageLocation";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="storage_location.id",
              SORT_ORDER_ID					="storage_location.sortOrderId",
              NAME					="storage_location.name",
              LOCATION					="storage_location.location",
              PARENT_STORAGE_LOCATION_ID					="storage_location.parentStorageLocationId",
              STORAGE_UNIT_ID					="storage_location.storageUnitId",
              IS_AVAILABLE					="storage_location.isAvailable";


  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,NAME,LOCATION,PARENT_STORAGE_LOCATION_ID,STORAGE_UNIT_ID,IS_AVAILABLE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final StorageLocationMeta storage_locationMeta = new StorageLocationMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private StorageLocationMeta() {
        
    }
    
    public static StorageLocationMeta getInstance() {
        return storage_locationMeta;
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getLocation() {
    return LOCATION;
  } 

  public static String getParentStorageLocationId() {
    return PARENT_STORAGE_LOCATION_ID;
  } 

  public static String getStorageUnitId() {
    return STORAGE_UNIT_ID;
  } 

  public static String getIsAvailable() {
    return IS_AVAILABLE;
  } 

  
}   
