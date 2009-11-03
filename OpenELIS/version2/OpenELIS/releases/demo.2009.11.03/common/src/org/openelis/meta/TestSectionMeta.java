
package org.openelis.meta;

/**
  * TestSection META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestSectionMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestSection";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              SECTION_ID					="sectionId",
              FLAG_ID					="flagId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,SECTION_ID,FLAG_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestSectionMeta() {
		init();        
    }
    
    public TestSectionMeta(String path) {
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

    public String getSectionId() {
        return path + SECTION_ID;
    } 

    public String getFlagId() {
        return path + FLAG_ID;
    } 

  
}   
