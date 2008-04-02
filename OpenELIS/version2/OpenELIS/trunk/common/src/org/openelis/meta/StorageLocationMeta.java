package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageLocationMeta implements Meta{
	private String tableName = "storageLocation";
	private String entityName = "StorageLocation";
	private boolean includeInFrom = true;
	
	public static final String
     ID               			= "storageLocation.id",
     SORT_ORDER					= "storageLocation.sortOrder",
     NAME   					= "storageLocation.name",
     LOCATION  					= "storageLocation.location",
     PARENT_STORAGE_LOCATION	= "storageLocation.parentStorageLocation",
     STORAGE_UNIT 				= "storageLocation.storageUnit",
     IS_AVAILABLE 				= "storageLocation.isAvailable";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, SORT_ORDER, NAME, LOCATION, PARENT_STORAGE_LOCATION, STORAGE_UNIT, IS_AVAILABLE};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageLocationMeta storageLocationMeta = new StorageLocationMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(16), "");
	}

	private StorageLocationMeta() {

	}
	
	public static StorageLocationMeta getInstance(){
		return storageLocationMeta;
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
