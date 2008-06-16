
package org.openelis.newmeta;

/**
  * Lock META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class LockMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Lock";
	
	private static final String
              ID					="id",
              REFERENCE_TABLE_ID					="referenceTableId",
              REFERENCE_ID					="referenceId",
              EXPIRES					="expires",
              SYSTEM_USER_ID					="systemUserId";

  	private static final String[] columnNames = {
  	  ID,REFERENCE_TABLE_ID,REFERENCE_ID,EXPIRES,SYSTEM_USER_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public LockMeta() {
		init();        
    }
    
    public LockMeta(String path) {
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

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getExpires() {
        return path + EXPIRES;
    } 

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

  
}   
