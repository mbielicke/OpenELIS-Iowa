package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class CategoryMeta implements Meta{
	private String tableName = "category";
	private String entityName = "Category";
	private boolean includeInFrom = true;
	private boolean collection = false;
	
	public static final String
     ID            	= "category.id",
     SYSTEM_NAME	= "category.systemName",
     NAME   		= "category.name",
     DESCRIPTION  	= "category.description",
     SECTION		= "category.section";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, SYSTEM_NAME, NAME, DESCRIPTION, SECTION};
	
	private static HashMap<String,String> columnHashList;
	
	private static final CategoryMeta categoryMeta = new CategoryMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(9), "");
	}

	private CategoryMeta() {

	}
	
	public static CategoryMeta getInstance(){
		return categoryMeta;
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
	
	public boolean isCollection(){
		return collection;
	}
	
	public boolean hasColumn(String columnName){
		if(columnName == null || !columnName.startsWith(tableName))
			return false;
		String column = columnName.substring(tableName.length()+1);
		
		return columnHashList.containsKey(column);
	}
}
