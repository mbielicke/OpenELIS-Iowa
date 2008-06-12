
package org.openelis.meta;

/**
  * Analyte META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AnalyteMeta implements Meta {
  	private static final String tableName = "analyte";
	private static final String entityName = "Analyte";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="analyte.id",
              NAME					="analyte.name",
              IS_ACTIVE					="analyte.is_active",
              ANALYTE_GROUP_ID					="analyte.analyte_group_id",
              PARENT_ANALYTE_ID					="analyte.parent_analyte_id",
              EXTERNAL_ID					="analyte.external_id";


  	private static final String[] columnNames = {
  	  ID,NAME,IS_ACTIVE,ANALYTE_GROUP_ID,PARENT_ANALYTE_ID,EXTERNAL_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AnalyteMeta analyteMeta = new AnalyteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AnalyteMeta() {
        
    }
    
    public static AnalyteMeta getInstance() {
        return analyteMeta;
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

  public static String getName() {
    return NAME;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getAnalyteGroupId() {
    return ANALYTE_GROUP_ID;
  } 

  public static String getParentAnalyteId() {
    return PARENT_ANALYTE_ID;
  } 

  public static String getExternalId() {
    return EXTERNAL_ID;
  } 

  
}   
