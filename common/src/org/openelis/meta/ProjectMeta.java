
package org.openelis.meta;

/**
  * Project META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ProjectMeta implements Meta {
  	private static final String tableName = "project";
	private static final String entityName = "Project";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="project.id",
              NAME					="project.name",
              DESCRIPTION					="project.description",
              STARTED_DATE					="project.started_date",
              COMPLETED_DATE					="project.completed_date",
              IS_ACTIVE					="project.is_active",
              REFERENCE_TO					="project.reference_to",
              OWNER_ID					="project.owner_id",
              SCRIPTLET_ID					="project.scriptlet_id";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,STARTED_DATE,COMPLETED_DATE,IS_ACTIVE,REFERENCE_TO,OWNER_ID,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ProjectMeta projectMeta = new ProjectMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ProjectMeta() {
        
    }
    
    public static ProjectMeta getInstance() {
        return projectMeta;
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

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getStartedDate() {
    return STARTED_DATE;
  } 

  public static String getCompletedDate() {
    return COMPLETED_DATE;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getReferenceTo() {
    return REFERENCE_TO;
  } 

  public static String getOwnerId() {
    return OWNER_ID;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
