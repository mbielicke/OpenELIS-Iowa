
package org.openelis.newmeta;

/**
  * TestAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class TestAnalyteMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "TestAnalyte";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              RESULT_GROUP_ID					="resultGroupId",
              SORT_ORDER_ID					="sortOrderId",
              TYPE_ID					="typeId",
              ANALYTE_ID					="analyteId",
              IS_REPORTABLE					="isReportable",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,RESULT_GROUP_ID,SORT_ORDER_ID,TYPE_ID,ANALYTE_ID,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestAnalyteMeta() {
		init();        
    }
    
    public TestAnalyteMeta(String path) {
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

    public String getResultGroupId() {
        return path + RESULT_GROUP_ID;
    } 

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
