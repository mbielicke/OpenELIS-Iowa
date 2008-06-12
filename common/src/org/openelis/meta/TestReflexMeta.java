
package org.openelis.meta;

/**
  * TestReflex META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestReflexMeta implements Meta {
  	private static final String tableName = "test_reflex";
	private static final String entityName = "TestReflex";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test_reflex.id",
              TEST_ID					="test_reflex.testId",
              TEST_ANALYTE_ID					="test_reflex.testAnalyteId",
              TEST_RESULT_ID					="test_reflex.testResultId",
              FLAGS_ID					="test_reflex.flagsId",
              ADD_TEST_ID					="test_reflex.addTestId";


  	private static final String[] columnNames = {
  	  ID,TEST_ID,TEST_ANALYTE_ID,TEST_RESULT_ID,FLAGS_ID,ADD_TEST_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestReflexMeta test_reflexMeta = new TestReflexMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestReflexMeta() {
        
    }
    
    public static TestReflexMeta getInstance() {
        return test_reflexMeta;
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

  public static String getTestAnalyteId() {
    return TEST_ANALYTE_ID;
  } 

  public static String getTestResultId() {
    return TEST_RESULT_ID;
  } 

  public static String getFlagsId() {
    return FLAGS_ID;
  } 

  public static String getAddTestId() {
    return ADD_TEST_ID;
  } 

  
}   
