
package org.openelis.meta;

/**
  * WorksheetItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class WorksheetItemMeta implements Meta {
  	private String path = "";
	private static final String entityName = "WorksheetItem";
	
	private static final String
              ID					="id",
              WORKSHEET_ID					="worksheetId",
              POSITION					="position";

  	private static final String[] columnNames = {
  	  ID,WORKSHEET_ID,POSITION};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public WorksheetItemMeta() {
		init();        
    }
    
    public WorksheetItemMeta(String path) {
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

    public String getWorksheetId() {
        return path + WORKSHEET_ID;
    } 

    public String getPosition() {
        return path + POSITION;
    } 

  
}   
