
package org.openelis.meta;

/**
  * TestResult META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestResultMeta implements Meta {
  	private static final String tableName = "test_result";
	private static final String entityName = "TestResult";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test_result.id",
              TEST_ID					="test_result.testId",
              RESULT_GROUP_ID					="test_result.resultGroupId",
              FLAG_ID					="test_result.flagId",
              TYPE_ID					="test_result.typeId",
              VALUE					="test_result.value",
              SIGNIFICANT_DIGITS					="test_result.significantDigits",
              QUANT_LIMIT					="test_result.quantLimit",
              CONT_LEVEL					="test_result.contLevel";


  	private static final String[] columnNames = {
  	  ID,TEST_ID,RESULT_GROUP_ID,FLAG_ID,TYPE_ID,VALUE,SIGNIFICANT_DIGITS,QUANT_LIMIT,CONT_LEVEL};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestResultMeta test_resultMeta = new TestResultMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestResultMeta() {
        
    }
    
    public static TestResultMeta getInstance() {
        return test_resultMeta;
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

  public static String getFlagId() {
    return FLAG_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getValue() {
    return VALUE;
  } 

  public static String getSignificantDigits() {
    return SIGNIFICANT_DIGITS;
  } 

  public static String getQuantLimit() {
    return QUANT_LIMIT;
  } 

  public static String getContLevel() {
    return CONT_LEVEL;
  } 

  
}   
