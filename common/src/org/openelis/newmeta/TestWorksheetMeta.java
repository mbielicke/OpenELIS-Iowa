
package org.openelis.newmeta;

/**
  * TestWorksheet META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class TestWorksheetMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "TestWorksheet";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              BATCH_CAPACITY					="batchCapacity",
              TOTAL_CAPACITY					="totalCapacity",
              NUMBER_FORMAT_ID					="numberFormatId",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,BATCH_CAPACITY,TOTAL_CAPACITY,NUMBER_FORMAT_ID,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestWorksheetMeta() {
		init();        
    }
    
    public TestWorksheetMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getTestId() {
        return path + TEST_ID;
    } 

    public String getBatchCapacity() {
        return path + BATCH_CAPACITY;
    } 

    public String getTotalCapacity() {
        return path + TOTAL_CAPACITY;
    } 

    public String getNumberFormatId() {
        return path + NUMBER_FORMAT_ID;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
