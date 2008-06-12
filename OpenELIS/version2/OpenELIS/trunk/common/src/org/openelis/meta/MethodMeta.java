
package org.openelis.meta;

/**
  * Method META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class MethodMeta implements Meta {
  	private static final String tableName = "method";
	private static final String entityName = "Method";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="method.id",
              NAME					="method.name",
              DESCRIPTION					="method.description",
              REPORTING_DESCRIPTION					="method.reportingDescription",
              IS_ACTIVE					="method.isActive",
              ACTIVE_BEGIN					="method.activeBegin",
              ACTIVE_END					="method.activeEnd";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END};
  	  
	private static HashMap<String,String> columnHashList;

	private static final MethodMeta methodMeta = new MethodMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private MethodMeta() {
        
    }
    
    public static MethodMeta getInstance() {
        return methodMeta;
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

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getReportingDescription() {
    return REPORTING_DESCRIPTION;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getActiveBegin() {
    return ACTIVE_BEGIN;
  } 

  public static String getActiveEnd() {
    return ACTIVE_END;
  } 

  
}   
