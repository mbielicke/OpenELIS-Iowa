
package org.openelis.meta;

/**
  * Dictionary META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class DictionaryMeta implements Meta {
  	private static final String tableName = "dictionary";
	private static final String entityName = "Dictionary";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="dictionary.id",
              CATEGORY_ID					="dictionary.categoryId",
              RELATED_ENTRY_ID					="dictionary.relatedEntryId",
              SYSTEM_NAME					="dictionary.systemName",
              IS_ACTIVE					="dictionary.isActive",
              LOCAL_ABBREV					="dictionary.localAbbrev",
              ENTRY					="dictionary.entry";


  	private static final String[] columnNames = {
  	  ID,CATEGORY_ID,RELATED_ENTRY_ID,SYSTEM_NAME,IS_ACTIVE,LOCAL_ABBREV,ENTRY};
  	  
	private static HashMap<String,String> columnHashList;

	private static final DictionaryMeta dictionaryMeta = new DictionaryMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private DictionaryMeta() {
        
    }
    
    public static DictionaryMeta getInstance() {
        return dictionaryMeta;
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getCategoryId() {
    return CATEGORY_ID;
  } 

  public static String getRelatedEntryId() {
    return RELATED_ENTRY_ID;
  } 

  public static String getSystemName() {
    return SYSTEM_NAME;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getLocalAbbrev() {
    return LOCAL_ABBREV;
  } 

  public static String getEntry() {
    return ENTRY;
  } 

  
}   
