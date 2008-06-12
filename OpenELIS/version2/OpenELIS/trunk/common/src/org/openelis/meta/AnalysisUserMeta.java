
package org.openelis.meta;

/**
  * AnalysisUser META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AnalysisUserMeta implements Meta {
  	private static final String tableName = "analysis_user";
	private static final String entityName = "AnalysisUser";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="analysis_user.id",
              ANALYSIS_ID					="analysis_user.analysis_id",
              SYSTEM_USER_ID					="analysis_user.system_user_id",
              ACTION_ID					="analysis_user.action_id";


  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,SYSTEM_USER_ID,ACTION_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AnalysisUserMeta analysis_userMeta = new AnalysisUserMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AnalysisUserMeta() {
        
    }
    
    public static AnalysisUserMeta getInstance() {
        return analysis_userMeta;
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getAnalysisId() {
    return ANALYSIS_ID;
  } 

  public static String getSystemUserId() {
    return SYSTEM_USER_ID;
  } 

  public static String getActionId() {
    return ACTION_ID;
  } 

  
}   
