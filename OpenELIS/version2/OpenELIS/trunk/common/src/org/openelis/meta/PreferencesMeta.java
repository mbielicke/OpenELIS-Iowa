
package org.openelis.meta;

/**
  * Preferences META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PreferencesMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Preferences";
	
	private static final String
              ID					="id",
              SYSTEM_USER_ID					="systemUserId",
              KEY					="key",
              TEXT					="text";

  	private static final String[] columnNames = {
  	  ID,SYSTEM_USER_ID,KEY,TEXT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PreferencesMeta() {
		init();        
    }
    
    public PreferencesMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

    public String getKey() {
        return path + KEY;
    } 

    public String getText() {
        return path + TEXT;
    } 

  
}   
