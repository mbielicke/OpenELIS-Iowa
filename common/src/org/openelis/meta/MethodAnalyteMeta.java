
package org.openelis.meta;

/**
  * MethodAnalyte META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class MethodAnalyteMeta implements Meta {
  	private static final String tableName = "method_analyte";
	private static final String entityName = "MethodAnalyte";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="method_analyte.id",
              METHOD_ID					="method_analyte.method_id",
              RESULT_GROUP_ID					="method_analyte.result_group_id",
              SORT_ORDER_ID					="method_analyte.sort_order_id",
              TYPE					="method_analyte.type",
              ANALYTE_ID					="method_analyte.analyte_id";


  	private static final String[] columnNames = {
  	  ID,METHOD_ID,RESULT_GROUP_ID,SORT_ORDER_ID,TYPE,ANALYTE_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final MethodAnalyteMeta method_analyteMeta = new MethodAnalyteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private MethodAnalyteMeta() {
        
    }
    
    public static MethodAnalyteMeta getInstance() {
        return method_analyteMeta;
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

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getType() {
    return TYPE;
  } 

  public static String getAnalyteId() {
    return ANALYTE_ID;
  } 

  
}   
