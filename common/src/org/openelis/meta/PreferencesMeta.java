
package org.openelis.meta;

/**
  * Preferences META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PreferencesMeta implements Meta {
  	private static final String tableName = "preferences";
	private static final String entityName = "Preferences";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="preferences.id",
              SYSTEM_USER_ID					="preferences.systemUserId",
              KEY					="preferences.key",
              TEXT					="preferences.text";


  	private static final String[] columnNames = {
  	  ID,SYSTEM_USER_ID,KEY,TEXT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PreferencesMeta preferencesMeta = new PreferencesMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PreferencesMeta() {
        
    }
    
    public static PreferencesMeta getInstance() {
        return preferencesMeta;
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

  public static String getSystemUserId() {
    return SYSTEM_USER_ID;
  } 

  public static String getKey() {
    return KEY;
  } 

  public static String getText() {
    return TEXT;
  } 

  
}   
