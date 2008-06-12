
package org.openelis.meta;

/**
  * TestWorksheetItem META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestWorksheetItemMeta implements Meta {
  	private static final String tableName = "test_worksheet_item";
	private static final String entityName = "TestWorksheetItem";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test_worksheet_item.id",
              TEST_WORKSHEET_ID					="test_worksheet_item.testWorksheetId",
              POSITION					="test_worksheet_item.position",
              TYPE_ID					="test_worksheet_item.typeId",
              QC_NAME					="test_worksheet_item.qcName";


  	private static final String[] columnNames = {
  	  ID,TEST_WORKSHEET_ID,POSITION,TYPE_ID,QC_NAME};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestWorksheetItemMeta test_worksheet_itemMeta = new TestWorksheetItemMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestWorksheetItemMeta() {
        
    }
    
    public static TestWorksheetItemMeta getInstance() {
        return test_worksheet_itemMeta;
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

  public static String getTestWorksheetId() {
    return TEST_WORKSHEET_ID;
  } 

  public static String getPosition() {
    return POSITION;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getQcName() {
    return QC_NAME;
  } 

  
}   
