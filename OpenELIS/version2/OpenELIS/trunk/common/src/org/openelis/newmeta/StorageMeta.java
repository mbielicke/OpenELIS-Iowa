
package org.openelis.newmeta;

/**
  * Storage META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class StorageMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Storage";
	
	private static final String
              ID					="id",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId",
              STORAGE_LOCATION_ID					="storageLocationId",
              CHECKIN					="checkin",
              CHECKOUT					="checkout";

  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,STORAGE_LOCATION_ID,CHECKIN,CHECKOUT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public StorageMeta() {
		init();        
    }
    
    public StorageMeta(String path) {
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

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getStorageLocationId() {
        return path + STORAGE_LOCATION_ID;
    } 

    public String getCheckin() {
        return path + CHECKIN;
    } 

    public String getCheckout() {
        return path + CHECKOUT;
    } 

  
}   
