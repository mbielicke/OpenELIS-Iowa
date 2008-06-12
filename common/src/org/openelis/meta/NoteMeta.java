
package org.openelis.meta;

/**
  * Note META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class NoteMeta implements Meta {
  	private static final String tableName = "note";
	private static final String entityName = "Note";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="note.id",
              REFERENCE_ID					="note.referenceId",
              REFERENCE_TABLE_ID					="note.referenceTableId",
              TIMESTAMP					="note.timestamp",
              IS_EXTERNAL					="note.isExternal",
              SYSTEM_USER_ID					="note.systemUserId",
              SUBJECT					="note.subject",
              TEXT					="note.text";


  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,TIMESTAMP,IS_EXTERNAL,SYSTEM_USER_ID,SUBJECT,TEXT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final NoteMeta noteMeta = new NoteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private NoteMeta() {
        
    }
    
    public static NoteMeta getInstance() {
        return noteMeta;
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

  public static String getReferenceId() {
    return REFERENCE_ID;
  } 

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getTimestamp() {
    return TIMESTAMP;
  } 

  public static String getIsExternal() {
    return IS_EXTERNAL;
  } 

  public static String getSystemUserId() {
    return SYSTEM_USER_ID;
  } 

  public static String getSubject() {
    return SUBJECT;
  } 

  public static String getText() {
    return TEXT;
  } 

  
}   
