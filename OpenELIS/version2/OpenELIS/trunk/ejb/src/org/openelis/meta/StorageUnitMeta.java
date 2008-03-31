package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StorageUnitMeta implements Meta{
	private String tableName = "storageUnit";
	private String entityName = "StorageUnit";
	private boolean includeInFrom = true;
	
	public static final String
     ID             = "storageUnit.id",
     CATEGORY		= "storageUnit.category",
     DESCRIPTION   	= "storageUnit.description",
     IS_SINGULAR  	= "storageUnit.isSingular";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, CATEGORY, DESCRIPTION, IS_SINGULAR};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StorageUnitMeta storageUnitMeta = new StorageUnitMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(12), "");
	}

	private StorageUnitMeta() {

	}
	
	public static StorageUnitMeta getInstance(){
		return storageUnitMeta;
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
