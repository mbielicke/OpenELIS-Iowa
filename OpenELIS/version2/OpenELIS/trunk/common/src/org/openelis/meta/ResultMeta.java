
package org.openelis.meta;

/**
  * Result META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ResultMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Result";
	
	private static final String
              ID					="id",
              ANALYSIS_ID					="analysisId",
              TEST_ANALYTE_ID					="testAnalyteId",
              TEST_RESULT_ID					="testResultId",
              IS_COLUMN					="isColumn",
              SORT_ORDER					="sortOrder",
              IS_REPORTABLE					="isReportable",
              ANALYTE_ID					="analyteId",
              TYPE_ID					="typeId",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,TEST_ANALYTE_ID,TEST_RESULT_ID,IS_COLUMN,SORT_ORDER,IS_REPORTABLE,ANALYTE_ID,TYPE_ID,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ResultMeta() {
		init();        
    }
    
    public ResultMeta(String path) {
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

    public String getAnalysisId() {
        return path + ANALYSIS_ID;
    } 

    public String getTestAnalyteId() {
        return path + TEST_ANALYTE_ID;
    } 

    public String getTestResultId() {
        return path + TEST_RESULT_ID;
    } 

    public String getIsColumn() {
        return path + IS_COLUMN;
    } 

    public String getSortOrder() {
        return path + SORT_ORDER;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
