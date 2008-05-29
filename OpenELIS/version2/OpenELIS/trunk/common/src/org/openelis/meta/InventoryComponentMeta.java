package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryComponentMeta implements Meta {
	private String tableName = "inventoryComponent";
	private String entityName = "inventoryItem.inventoryComponent";
	private boolean includeInFrom = true;
	
	public static final String
     ID               	= "inventoryComponent.id",
     INVENTORY_ITEM		= "inventoryComponent.inventoryItem",
     COMPONENT 			= "inventoryComponent.component",
     QUANTITY 			= "inventoryComponent.quantity";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, INVENTORY_ITEM, COMPONENT, QUANTITY};
	
	private static HashMap<String,String> columnHashList;
	
	private static final InventoryComponentMeta inventoryComponentMeta = new InventoryComponentMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(19), "");
	}

	private InventoryComponentMeta() {

	}
	
	public static InventoryComponentMeta getInstance(){
		return inventoryComponentMeta;
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
	
	public static String component(){
		return columnNames[2];
	}
	
	public static String quantity(){
		return columnNames[3];
	}
}
