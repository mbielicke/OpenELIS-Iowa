package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageLocationParentMeta implements Meta{
	private String tableName = "parentStorageLocation";
	private String entityName = "storageLocation.parentStorageLocation";
	private boolean includeInFrom = true;
	
	public static final String
     ID               			= "parentStorageLocation.id",
     SORT_ORDER					= "parentStorageLocation.sortOrder",
     NAME   					= "parentStorageLocation.name",
     LOCATION  					= "parentStorageLocation.location",
     PARENT_STORAGE_LOCATION	= "parentStorageLocation.parentStorageLocationId",
     STORAGE_UNIT 				= "parentStorageLocation.storageUnitId",
     IS_AVAILABLE 				= "parentStorageLocation.isAvailable";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, SORT_ORDER, NAME, LOCATION, PARENT_STORAGE_LOCATION, STORAGE_UNIT, IS_AVAILABLE};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageLocationParentMeta storageLocationParentMeta = new StorageLocationParentMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(22), "");
	}

	private StorageLocationParentMeta() {

	}
	
	public static StorageLocationParentMeta getInstance(){
		return storageLocationParentMeta;
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
}
