
package org.openelis.meta;

/**
  * TestWorksheet META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestWorksheetMeta implements Meta {
  	private static final String tableName = "test_worksheet";
	private static final String entityName = "TestWorksheet";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test_worksheet.id",
              TEST_ID					="test_worksheet.test_id",
              BATCH_CAPACITY					="test_worksheet.batch_capacity",
              TOTAL_CAPACITY					="test_worksheet.total_capacity",
              NUMBER_FORMAT_ID					="test_worksheet.number_format_id",
              SCRIPTLET_ID					="test_worksheet.scriptlet_id";


  	private static final String[] columnNames = {
  	  ID,TEST_ID,BATCH_CAPACITY,TOTAL_CAPACITY,NUMBER_FORMAT_ID,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestWorksheetMeta test_worksheetMeta = new TestWorksheetMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestWorksheetMeta() {
        
    }
    
    public static TestWorksheetMeta getInstance() {
        return test_worksheetMeta;
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

  public static String getBatchCapacity() {
    return BATCH_CAPACITY;
  } 

  public static String getTotalCapacity() {
    return TOTAL_CAPACITY;
  } 

  public static String getNumberFormatId() {
    return NUMBER_FORMAT_ID;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
