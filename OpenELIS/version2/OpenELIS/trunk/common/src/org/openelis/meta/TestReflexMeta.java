
package org.openelis.meta;

/**
  * TestReflex META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestReflexMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestReflex";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              TEST_ANALYTE_ID					="testAnalyteId",
              TEST_RESULT_ID					="testResultId",
              FLAGS_ID					="flagsId",
              ADD_TEST_ID					="addTestId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,TEST_ANALYTE_ID,TEST_RESULT_ID,FLAGS_ID,ADD_TEST_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestReflexMeta() {
		init();        
    }
    
    public TestReflexMeta(String path) {
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

    public String getTestAnalyteId() {
        return path + TEST_ANALYTE_ID;
    } 

    public String getTestResultId() {
        return path + TEST_RESULT_ID;
    } 

    public String getFlagsId() {
        return path + FLAGS_ID;
    } 

    public String getAddTestId() {
        return path + ADD_TEST_ID;
    } 

  
}   
