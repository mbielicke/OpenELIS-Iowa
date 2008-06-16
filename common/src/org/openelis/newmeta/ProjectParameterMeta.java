
package org.openelis.newmeta;

/**
  * ProjectParameter META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class ProjectParameterMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "ProjectParameter";
	
	private static final String
              ID					="id",
              PROJECT_ID					="projectId",
              PARAMETER					="parameter",
              OPERATION					="operation",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,PROJECT_ID,PARAMETER,OPERATION,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ProjectParameterMeta() {
		init();        
    }
    
    public ProjectParameterMeta(String path) {
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

    public String getProjectId() {
        return path + PROJECT_ID;
    } 

    public String getParameter() {
        return path + PARAMETER;
    } 

    public String getOperation() {
        return path + OPERATION;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
