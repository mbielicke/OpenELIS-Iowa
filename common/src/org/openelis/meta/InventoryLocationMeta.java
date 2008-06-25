
package org.openelis.meta;

/**
  * InventoryLocation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class InventoryLocationMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InventoryLocation";
	
	private static final String
              ID					="id",
              INVENTORY_ITEM_ID					="inventoryItemId",
              LOT_NUMBER					="lotNumber",
              STORAGE_LOCATION_ID					="storageLocationId",
              QUANTITY_ONHAND					="quantityOnhand",
              EXPIRATION_DATE					="expirationDate";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,LOT_NUMBER,STORAGE_LOCATION_ID,QUANTITY_ONHAND,EXPIRATION_DATE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryLocationMeta() {
		init();        
    }
    
    public InventoryLocationMeta(String path) {
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

    public String getInventoryItemId() {
        return path + INVENTORY_ITEM_ID;
    } 

    public String getLotNumber() {
        return path + LOT_NUMBER;
    } 

    public String getStorageLocationId() {
        return path + STORAGE_LOCATION_ID;
    } 

    public String getQuantityOnhand() {
        return path + QUANTITY_ONHAND;
    } 

    public String getExpirationDate() {
        return path + EXPIRATION_DATE;
    } 

  
}   
