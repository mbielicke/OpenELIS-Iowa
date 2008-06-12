
package org.openelis.meta;

/**
  * SystemVariable META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SystemVariableMeta implements Meta {
  	private static final String tableName = "system_variable";
	private static final String entityName = "SystemVariable";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="system_variable.id",
              NAME					="system_variable.name",
              VALUE					="system_variable.value";


  	private static final String[] columnNames = {
  	  ID,NAME,VALUE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SystemVariableMeta system_variableMeta = new SystemVariableMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SystemVariableMeta() {
        
    }
    
    public static SystemVariableMeta getInstance() {
        return system_variableMeta;
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

  public static String getName() {
    return NAME;
  } 

  public static String getValue() {
    return VALUE;
  } 

  
}   
