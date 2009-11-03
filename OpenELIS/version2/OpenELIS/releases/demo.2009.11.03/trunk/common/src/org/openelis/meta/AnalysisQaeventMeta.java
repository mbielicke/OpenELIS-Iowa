
package org.openelis.meta;

/**
  * AnalysisQaevent META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class AnalysisQaeventMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "AnalysisQaevent";
	
	private static final String
              ID					="id",
              ANALYSIS_ID					="analysisId",
              QAEVENT_ID					="qaeventId",
              TYPE_ID					="typeId",
              IS_BILLABLE					="isBillable";

  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,QAEVENT_ID,TYPE_ID,IS_BILLABLE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AnalysisQaeventMeta() {
		init();        
    }
    
    public AnalysisQaeventMeta(String path) {
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

    public String getQaeventId() {
        return path + QAEVENT_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getIsBillable() {
        return path + IS_BILLABLE;
    } 

  
}   
