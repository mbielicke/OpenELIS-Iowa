
package org.openelis.meta;

/**
  * WorksheetAnalysis META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class WorksheetAnalysisMeta implements Meta {
  	private String path = "";
	private static final String entityName = "WorksheetAnalysis";
	
	private static final String
              ID					="id",
              WORKSHEET_ITEM_ID					="worksheetItemId",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId";

  	private static final String[] columnNames = {
  	  ID,WORKSHEET_ITEM_ID,REFERENCE_ID,REFERENCE_TABLE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public WorksheetAnalysisMeta() {
		init();        
    }
    
    public WorksheetAnalysisMeta(String path) {
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

    public String getWorksheetItemId() {
        return path + WORKSHEET_ITEM_ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

  
}   
