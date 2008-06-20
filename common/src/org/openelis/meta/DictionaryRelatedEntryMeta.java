package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class DictionaryRelatedEntryMeta implements Meta{
	private String tableName = "dictionary.relatedEntry";
	private String entityName = "dictionary.relatedEntry";
	private boolean includeInFrom = false;
	
	public static final String
     ID            	= "dictionary.relatedEntry.id",
	 ENTRY 			= "dictionary.relatedEntry.entry";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, ENTRY};        
	
	private static HashMap<String,String> columnHashList;
	
	private static final DictionaryRelatedEntryMeta dictionaryRelatedEntryMeta = new DictionaryRelatedEntryMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(24), "");
	}

	private DictionaryRelatedEntryMeta() {

	}
	
	public static DictionaryRelatedEntryMeta getInstance(){
		return dictionaryRelatedEntryMeta;
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
       
    
    public static String getEntry(){
        return columnNames[1];
    }        
    
}
