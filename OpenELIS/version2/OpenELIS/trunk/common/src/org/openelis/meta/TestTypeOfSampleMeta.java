
package org.openelis.meta;

/**
  * TestTypeOfSample META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestTypeOfSampleMeta implements Meta {
    protected String path = "";
	private static final String entityName = "TestTypeOfSample";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              TYPE_OF_SAMPLE_ID					="typeOfSampleId",
              UNIT_OF_MEASURE_ID					="unitOfMeasureId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,TYPE_OF_SAMPLE_ID,UNIT_OF_MEASURE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestTypeOfSampleMeta() {
		init();        
    }
    
    public TestTypeOfSampleMeta(String path) {
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

    public String getTypeOfSampleId() {
        return path + TYPE_OF_SAMPLE_ID;
    } 

    public String getUnitOfMeasureId() {
        return path + UNIT_OF_MEASURE_ID;
    } 

  
}   
