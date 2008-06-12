
package org.openelis.meta;

/**
  * MethodResult META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class MethodResultMeta implements Meta {
  	private static final String tableName = "method_result";
	private static final String entityName = "MethodResult";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="method_result.id",
              METHOD_ID					="method_result.methodId",
              RESULT_GROUP_ID					="method_result.resultGroupId",
              FLAGS					="method_result.flags",
              TYPE					="method_result.type",
              VALUE					="method_result.value";


  	private static final String[] columnNames = {
  	  ID,METHOD_ID,RESULT_GROUP_ID,FLAGS,TYPE,VALUE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final MethodResultMeta method_resultMeta = new MethodResultMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private MethodResultMeta() {
        
    }
    
    public static MethodResultMeta getInstance() {
        return method_resultMeta;
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

  public static String getMethodId() {
    return METHOD_ID;
  } 

  public static String getResultGroupId() {
    return RESULT_GROUP_ID;
  } 

  public static String getFlags() {
    return FLAGS;
  } 

  public static String getType() {
    return TYPE;
  } 

  public static String getValue() {
    return VALUE;
  } 

  
}   
