package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryItemMeta implements Meta{
	private String tableName = "inventoryItem";
	private String entityName = "InventoryItem";
	private boolean includeInFrom = true;
	
	public static final String
     ID               		= "inventoryItem.id",
     NAME					= "inventoryItem.name",
     DESCRIPTION 			= "inventoryItem.description",
     QUANITY_MIN_LEVEL 		= "inventoryItem.quantityMinLevel",
     QUANTITY_MAX_LEVEL 	= "inventoryItem.quantityMaxLevel",
     QUANTITY_TO_REORDER 	= "inventoryItem.quantityToReorder",
     UNIT_OF_MEASURE 		= "inventoryItem.unitOfMeasure",
     IS_REORDER_AUTO 		= "inventoryItem.isReorderAuto",
     IS_LOT_MAINTAINED		= "inventoryItem.isLotMaintained",
     IS_SERIAL_REQURIED 	= "inventoryItem.isSerialRequired",
     IS_ACTIVE 				= "inventoryItem.isActive",
     AVERAGE_LEAD_TIME 		= "inventoryItem.averageLeadTime",
     AVERAGE_COST 			= "inventoryItem.averageCost",
     AVERAGE_DAILY_USE 		= "inventoryItem.averageDailyUse",
     STORE 					= "inventoryItem.store",
	 PURCHASED_UNIT 		= "inventoryItem.purchasedUnit",
     DISPENSED_UNIT 		= "inventoryItem.dispensedUnit",
	 IS_BULK 				= "inventoryItem.isBulk",
	 IS_NOT_FOR_SALE 		= "inventoryItem.isNotForSale",
	 IS_SUB_ASSEMBLY 		= "inventoryItem.isSubAssembly",
	 IS_LABOR 				= "inventoryItem.isLabor",
	 CATEGORY				= "inventoryItem.category";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, NAME, DESCRIPTION, QUANITY_MIN_LEVEL, QUANTITY_MAX_LEVEL, QUANTITY_TO_REORDER, UNIT_OF_MEASURE,
	     IS_REORDER_AUTO, IS_LOT_MAINTAINED, IS_SERIAL_REQURIED, IS_ACTIVE, AVERAGE_LEAD_TIME, AVERAGE_COST, AVERAGE_DAILY_USE,
	     STORE, PURCHASED_UNIT, DISPENSED_UNIT, IS_BULK, IS_NOT_FOR_SALE, IS_SUB_ASSEMBLY, IS_LABOR, CATEGORY};
	
	private static HashMap<String,String> columnHashList;
	
	private static final InventoryItemMeta iventoryItemMeta = new InventoryItemMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(14), "");
	}

	private InventoryItemMeta() {

	}
	
	public static InventoryItemMeta getInstance(){
		return iventoryItemMeta;
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
	
	public static String name(){
		return columnNames[1];
	}
	
	public static String description(){
		return columnNames[2];
	}
	
	public static String quantityMinLevel(){
		return columnNames[3];
	}
	
	public static String quantityMaxLevel(){
		return columnNames[4];
	}
	
	public static String quantityToReorder(){
		return columnNames[5];
	}
	
	public static String unitOfMeasure(){
		return columnNames[6];
	}
	
	public static String isReorderAuto(){
		return columnNames[7];
	}
	
	public static String isLotMaintained(){
		return columnNames[8];
	}
	
	public static String isSerialRequired(){
		return columnNames[9];
	}
	
	public static String isActive(){
		return columnNames[10];
	}
	
	public static String averageLeadTime(){
		return columnNames[11];
	}
	
	public static String averageCost(){
		return columnNames[12];
	}
	
	public static String averageDailyUse(){
		return columnNames[13];
	}
	
	public static String store(){
		return columnNames[14];
	}
	
	public static String purchasedUnit(){
		return columnNames[15];
	}
	
	public static String dispensedUnit(){
		return columnNames[16];
	}
	
	public static String isBulk(){
		return columnNames[17];
	}
	
	public static String isNotForSale(){
		return columnNames[18];
	}
	
	public static String isSubAssembly(){
		return columnNames[19];
	}
	
	public static String isLabor(){
		return columnNames[20];
	}
	
	public static String category(){
		return columnNames[21];
	}
}