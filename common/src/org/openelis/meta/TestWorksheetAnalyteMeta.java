
package org.openelis.meta;

/**
  * TestWorksheetAnalyte META Data
  */

import org.openelis.gwt.common.Meta;

import java.util.HashSet;

public class TestWorksheetAnalyteMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestWorksheetAnalyte";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              ANALYTE_ID					="analyteId",
              REPEAT					="repeat",
              FLAG_ID					="flagId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,ANALYTE_ID,REPEAT,FLAG_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestWorksheetAnalyteMeta() {
		init();        
    }
    
    public TestWorksheetAnalyteMeta(String path) {
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

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getRepeat() {
        return path + REPEAT;
    } 

    public String getFlagId() {
        return path + FLAG_ID;
    } 

  
}   
