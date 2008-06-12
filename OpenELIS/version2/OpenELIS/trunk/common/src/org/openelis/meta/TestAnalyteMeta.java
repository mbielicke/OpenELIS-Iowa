
package org.openelis.meta;

/**
  * TestAnalyte META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestAnalyteMeta implements Meta {
  	private static final String tableName = "test_analyte";
	private static final String entityName = "TestAnalyte";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test_analyte.id",
              TEST_ID					="test_analyte.testId",
              RESULT_GROUP_ID					="test_analyte.resultGroupId",
              SORT_ORDER_ID					="test_analyte.sortOrderId",
              TYPE_ID					="test_analyte.typeId",
              ANALYTE_ID					="test_analyte.analyteId",
              IS_REPORTABLE					="test_analyte.isReportable",
              SCRIPTLET_ID					="test_analyte.scriptletId";


  	private static final String[] columnNames = {
  	  ID,TEST_ID,RESULT_GROUP_ID,SORT_ORDER_ID,TYPE_ID,ANALYTE_ID,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestAnalyteMeta test_analyteMeta = new TestAnalyteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestAnalyteMeta() {
        
    }
    
    public static TestAnalyteMeta getInstance() {
        return test_analyteMeta;
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

  public static String getTestId() {
    return TEST_ID;
  } 

  public static String getResultGroupId() {
    return RESULT_GROUP_ID;
  } 

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getAnalyteId() {
    return ANALYTE_ID;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
