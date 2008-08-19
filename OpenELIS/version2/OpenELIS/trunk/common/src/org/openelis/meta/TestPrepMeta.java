
package org.openelis.meta;

/**
  * TestPrep META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestPrepMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "TestPrep";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              PREP_TEST_ID					="prepTestId",
              IS_OPTIONAL					="isOptional";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,PREP_TEST_ID,IS_OPTIONAL};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestPrepMeta() {
		init();        
    }
    
    public TestPrepMeta(String path) {
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

    public String getPrepTestId() {
        return path + PREP_TEST_ID;
    } 

    public String getIsOptional() {
        return path + IS_OPTIONAL;
    } 

  
}   
