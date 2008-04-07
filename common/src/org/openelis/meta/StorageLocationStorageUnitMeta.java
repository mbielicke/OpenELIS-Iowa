package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageLocationStorageUnitMeta implements Meta{
	private String tableName = "storageLocation.storageUnit";
	private String entityName = "storageLocation.storageUnit";
	private boolean includeInFrom = false;
	
	public static final String
     ID             = "storageLocation.storageUnit.id",
     CATEGORY		= "storageLocation.storageUnit.category",
     DESCRIPTION   	= "storageLocation.storageUnit.description",
     IS_SINGULAR  	= "storageLocation.storageUnit.isSingular";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, CATEGORY, DESCRIPTION, IS_SINGULAR};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageLocationStorageUnitMeta storageLocationStorageUnitMeta = new StorageLocationStorageUnitMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(28), "");
	}

	private StorageLocationStorageUnitMeta() {

	}
	
	public static StorageLocationStorageUnitMeta getInstance(){
		return storageLocationStorageUnitMeta;
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
	
	public static String category(){
		return columnNames[1];
	}
	
	public static String description(){
		return columnNames[2];
	}
	
	public static String isSingular(){
		return columnNames[3];
	}
}
