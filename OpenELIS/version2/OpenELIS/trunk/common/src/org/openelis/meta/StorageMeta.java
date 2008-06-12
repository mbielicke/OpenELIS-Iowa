
package org.openelis.meta;

/**
  * Storage META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class StorageMeta implements Meta {
  	private static final String tableName = "storage";
	private static final String entityName = "Storage";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="storage.id",
              REFERENCE_ID					="storage.referenceId",
              REFERENCE_TABLE_ID					="storage.referenceTableId",
              STORAGE_LOCATION_ID					="storage.storageLocationId",
              CHECKIN					="storage.checkin",
              CHECKOUT					="storage.checkout";


  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,STORAGE_LOCATION_ID,CHECKIN,CHECKOUT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final StorageMeta storageMeta = new StorageMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private StorageMeta() {
        
    }
    
    public static StorageMeta getInstance() {
        return storageMeta;
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

  public static String getReferenceId() {
    return REFERENCE_ID;
  } 

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getStorageLocationId() {
    return STORAGE_LOCATION_ID;
  } 

  public static String getCheckin() {
    return CHECKIN;
  } 

  public static String getCheckout() {
    return CHECKOUT;
  } 

  
}   
