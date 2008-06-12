
package org.openelis.meta;

/**
  * ProjectParameter META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ProjectParameterMeta implements Meta {
  	private static final String tableName = "project_parameter";
	private static final String entityName = "ProjectParameter";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="project_parameter.id",
              PROJECT_ID					="project_parameter.projectId",
              PARAMETER					="project_parameter.parameter",
              OPERATION					="project_parameter.operation",
              VALUE					="project_parameter.value";


  	private static final String[] columnNames = {
  	  ID,PROJECT_ID,PARAMETER,OPERATION,VALUE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ProjectParameterMeta project_parameterMeta = new ProjectParameterMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ProjectParameterMeta() {
        
    }
    
    public static ProjectParameterMeta getInstance() {
        return project_parameterMeta;
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

  public static String getProjectId() {
    return PROJECT_ID;
  } 

  public static String getParameter() {
    return PARAMETER;
  } 

  public static String getOperation() {
    return OPERATION;
  } 

  public static String getValue() {
    return VALUE;
  } 

  
}   
