package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class DictionaryRelatedEntryMeta implements Meta{
	private String tableName = "dictionary.relatedEntry";
	private String entityName = "dictionary.relatedEntry";
	private boolean includeInFrom = false;
	
	public static final String
     ID            	= "dictionary.relatedEntry.id",
     /*CATEGORY		= "dictionary.relatedEntry.category",
     RELATED_ENTRY	= "dictionary.relatedEntry.relatedEntry",
     SYSTEM_NAME  	= "dictionary.relatedEntry.systemName",
     IS_ACTIVE		= "dictionary.relatedEntry.isActive",
	 LOCAL_ABBREV 	= "dictionary.relatedEntry.localAbbrev",*/
	 ENTRY 			= "dictionary.relatedEntry.entry";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, ENTRY};
        // CATEGORY, RELATED_ENTRY, SYSTEM_NAME, IS_ACTIVE, LOCAL_ABBREV
	
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
        
    public static String id(){
        return columnNames[0];
    }
    
   /* public static String category(){
        return columnNames[1];
    }
    
    public static String relatedEntry(){
        return columnNames[2];
    }

    public static String systemName(){
        return columnNames[3];
    }
    
    public static String isActive(){
        return columnNames[4];
    }
    
    public static String localAbbrev(){
        return columnNames[5];
    } */
    
    public static String entry(){
        //return columnNames[6];
        return columnNames[1];
    }        
    
}
