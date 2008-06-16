
package org.openelis.newmeta;

/**
  * AnalysisUser META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class AnalysisUserMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "AnalysisUser";
	
	private static final String
              ID					="id",
              ANALYSIS_ID					="analysisId",
              SYSTEM_USER_ID					="systemUserId",
              ACTION_ID					="actionId";

  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,SYSTEM_USER_ID,ACTION_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AnalysisUserMeta() {
		init();        
    }
    
    public AnalysisUserMeta(String path) {
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

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

    public String getActionId() {
        return path + ACTION_ID;
    } 

  
}   
