package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageLocationChildStorageUnitMeta implements Meta{
	private String tableName = "childStorageLocation.storageUnit";
	private String entityName = "childStorageLocation.storageUnit";
	private boolean includeInFrom = false;
	
	public static final String
     ID             = "childStorageLocation.storageUnit.id",
     CATEGORY		= "childStorageLocation.storageUnit.category",
     DESCRIPTION   	= "childStorageLocation.storageUnit.description",
     IS_SINGULAR  	= "childStorageLocation.storageUnit.isSingular";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, CATEGORY, DESCRIPTION, IS_SINGULAR};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageLocationChildStorageUnitMeta storageLocationChildStorageUnitMeta = new StorageLocationChildStorageUnitMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(33), "");
	}

	private StorageLocationChildStorageUnitMeta() {

	}
	
	public static StorageLocationChildStorageUnitMeta getInstance(){
		return storageLocationChildStorageUnitMeta;
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
	
	public static String getCategory(){
		return columnNames[1];
	}
	
	public static String getDescription(){
		return columnNames[2];
	}
	
	public static String getIsSingular(){
		return columnNames[3];
	}

}
