package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageLocationChildMeta implements Meta{
	private String tableName = "childStorageLocation";
	private String entityName = "storage_location.childLocations";
	private boolean includeInFrom = true;
	
	public static final String
     ID               			= "childStorageLocation.id",
     SORT_ORDER_ID				= "childStorageLocation.sortOrderId",
     NAME   					= "childStorageLocation.name",
     LOCATION  					= "childStorageLocation.location",
     PARENT_STORAGE_LOCATION_ID	= "childStorageLocation.parentStorageLocationId",
     STORAGE_UNIT_ID 				= "childStorageLocation.storageUnitId",
     IS_AVAILABLE 				= "childStorageLocation.isAvailable";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, SORT_ORDER_ID, NAME, LOCATION, PARENT_STORAGE_LOCATION_ID, STORAGE_UNIT_ID, IS_AVAILABLE};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageLocationChildMeta storageLocationChildMeta = new StorageLocationChildMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(21), "");
	}

	private StorageLocationChildMeta() {

	}
	
	public static StorageLocationChildMeta getInstance(){
		return storageLocationChildMeta;
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
	
	public static String getId(){
		return columnNames[0];
	}
	
	public static String getSortOrderId(){
		return columnNames[1];
	}
	
	public static String getName(){
		return columnNames[2];
	}
	
	public static String getLocation(){
		return columnNames[3];
	}
	
	public static String getParentStorageLocationId(){
		return columnNames[4];
	}
	
	public static String getStorageUnitId(){
		return columnNames[5];
	}
	
	public static String getIsAvailable(){
		return columnNames[6];
	}
}
