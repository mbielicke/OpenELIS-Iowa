
package org.openelis.meta;

/**
  * WorksheetQc META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class WorksheetQcMeta implements Meta {
  	private String path = "";
	private static final String entityName = "WorksheetQc";
	
	private static final String
              ID					="id",
              WORKSHEET_ANALYSIS_ID					="worksheetAnalysisId",
              SORT_ORDER					="sortOrder",
              QC_ANALYTE_ID					="qcAnalyteId",
              TYPE_ID					="typeId",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,WORKSHEET_ANALYSIS_ID,SORT_ORDER,QC_ANALYTE_ID,TYPE_ID,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public WorksheetQcMeta() {
		init();        
    }
    
    public WorksheetQcMeta(String path) {
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

    public String getQcAnalyteId() {
        return path + QC_ANALYTE_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
