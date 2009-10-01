
package org.openelis.meta;

/**
  * WorksheetAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class WorksheetAnalyteMeta implements Meta {
  	private String path = "";
	private static final String entityName = "WorksheetAnalyte";
	
	private static final String
              ID					="id",
              WORKSHEET_ANALYSIS_ID					="worksheetAnalysisId",
              SORT_ORDER					="sortOrder",
              ANALYTE_ID					="analyteId",
              VALUE					="value",
              RESULT_ID					="resultId";

  	private static final String[] columnNames = {
  	  ID,WORKSHEET_ANALYSIS_ID,SORT_ORDER,ANALYTE_ID,VALUE,RESULT_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public WorksheetAnalyteMeta() {
		init();        
    }
    
    public WorksheetAnalyteMeta(String path) {
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

    public String getWorksheetAnalysisId() {
        return path + WORKSHEET_ANALYSIS_ID;
    } 

    public String getSortOrder() {
        return path + SORT_ORDER;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

    public String getResultId() {
        return path + RESULT_ID;
    } 

  
}   
