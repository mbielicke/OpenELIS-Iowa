package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryLocationMeta implements Meta {
	private String tableName = "inventoryLocation";
	private String entityName = "inventoryItem.inventoryLocation";
	private boolean includeInFrom = true;
	
	public static final String
     ID               	= "inventoryLocation.id",
     INVENTORY_ITEM		= "inventoryLocation.inventoryItem",
     LOT_NUMBER 		= "inventoryLocation.lotNumber",
     STORAGE_LOCATION 	= "inventoryLocation.storageLocation",
     QUANTITY_ONHAND	= "inventoryLocation.quantityOnhand",
     EXPIRATION_DATE 	= "inventoryLocation.expirationDate";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, INVENTORY_ITEM, LOT_NUMBER, STORAGE_LOCATION, QUANTITY_ONHAND, EXPIRATION_DATE};
	
	private static HashMap<String,String> columnHashList;
	
	private static final InventoryLocationMeta inventoryLocationMeta = new InventoryLocationMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(18), "");
	}

	private InventoryLocationMeta() {

	}
	
	public static InventoryLocationMeta getInstance(){
		return inventoryLocationMeta;
	}
	
	public String[] getColumnList() {
		return columnNames;
	}

	public String getTable() {
		return tableName;
	}
	
	public String getEntity(){
		return entityName;
	}
	
	public boolean includeInFrom(){
		return includeInFrom;
	}
	
	public boolean hasColumn(String columnName){
		if(columnName == null || !columnName.startsWith(tableName))
			return false;
		String column = columnName.substring(tableName.length()+1);
		
		return columnHashList.containsKey(column);
	}
	
	public static String id(){
		return columnNames[0];
	}
	
	public static String inventoryItem(){
		return columnNames[1];
	}
	
	public static String lotNumber(){
		return columnNames[2];
	}
	
	public static String storageLocation(){
		return columnNames[3];
	}
	
	public static String quantityOnhand(){
		return columnNames[4];
	}
	
	public static String expirationDate(){
		return columnNames[5];
	}
}
