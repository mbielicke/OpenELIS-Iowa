
package org.openelis.meta;

/**
  * InventoryLocation META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InventoryLocationMeta implements Meta {
  	private static final String tableName = "inventory_location";
	private static final String entityName = "InventoryLocation";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="inventory_location.id",
              INVENTORY_ITEM_ID					="inventory_location.inventoryItemId",
              LOT_NUMBER					="inventory_location.lotNumber",
              STORAGE_LOCATION_ID					="inventory_location.storageLocationId",
              QUANTITY_ONHAND					="inventory_location.quantityOnhand",
              EXPIRATION_DATE					="inventory_location.expirationDate";


  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,LOT_NUMBER,STORAGE_LOCATION_ID,QUANTITY_ONHAND,EXPIRATION_DATE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InventoryLocationMeta inventory_locationMeta = new InventoryLocationMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InventoryLocationMeta() {
        
    }
    
    public static InventoryLocationMeta getInstance() {
        return inventory_locationMeta;
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

  public static String getInventoryItemId() {
    return INVENTORY_ITEM_ID;
  } 

  public static String getLotNumber() {
    return LOT_NUMBER;
  } 

  public static String getStorageLocationId() {
    return STORAGE_LOCATION_ID;
  } 

  public static String getQuantityOnhand() {
    return QUANTITY_ONHAND;
  } 

  public static String getExpirationDate() {
    return EXPIRATION_DATE;
  } 

  
}   
