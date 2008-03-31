package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class DictionaryMeta implements Meta{
	private String tableName = "dictionary";
	private String entityName = "category.dictionary";
	private boolean includeInFrom = true;
	
	public static final String
     ID            	= "dictionary.id",
     CATEGORY		= "dictionary.category",
     RELATED_ENTRY	= "dictionary.relatedEntry",
     SYSTEM_NAME  	= "dictionary.systemName",
     IS_ACTIVE		= "dictionary.isActive",
	 LOCAL_ABBREV 	= "dictionary.localAbbrev",
	 ENTRY 			= "dictionary.entry";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, CATEGORY, RELATED_ENTRY, SYSTEM_NAME, IS_ACTIVE, LOCAL_ABBREV, ENTRY};
	
	private static HashMap<String,String> columnHashList;
	
	private static final DictionaryMeta dictionaryMeta = new DictionaryMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(11), "");
	}

	private DictionaryMeta() {

	}
	
	public static DictionaryMeta getInstance(){
		return dictionaryMeta;
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
