
package org.openelis.meta;

/**
  * Lock META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class LockMeta implements Meta {
  	private static final String tableName = "lock";
	private static final String entityName = "Lock";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="lock.id",
              REFERENCE_TABLE_ID					="lock.referenceTableId",
              REFERENCE_ID					="lock.referenceId",
              EXPIRES					="lock.expires",
              SYSTEM_USER_ID					="lock.systemUserId";


  	private static final String[] columnNames = {
  	  ID,REFERENCE_TABLE_ID,REFERENCE_ID,EXPIRES,SYSTEM_USER_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final LockMeta lockMeta = new LockMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private LockMeta() {
        
    }
    
    public static LockMeta getInstance() {
        return lockMeta;
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

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getReferenceId() {
    return REFERENCE_ID;
  } 

  public static String getExpires() {
    return EXPIRES;
  } 

  public static String getSystemUserId() {
    return SYSTEM_USER_ID;
  } 

  
}   
