
package org.openelis.meta;

/**
  * AnalysisQaevent META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AnalysisQaeventMeta implements Meta {
  	private static final String tableName = "analysis_qaevent";
	private static final String entityName = "AnalysisQaevent";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="analysis_qaevent.id",
              ANALYSIS_ID					="analysis_qaevent.analysisId",
              QAEVENT_ID					="analysis_qaevent.qaeventId";


  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,QAEVENT_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AnalysisQaeventMeta analysis_qaeventMeta = new AnalysisQaeventMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AnalysisQaeventMeta() {
        
    }
    
    public static AnalysisQaeventMeta getInstance() {
        return analysis_qaeventMeta;
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

  public static String getQaeventId() {
    return QAEVENT_ID;
  } 

  
}   
