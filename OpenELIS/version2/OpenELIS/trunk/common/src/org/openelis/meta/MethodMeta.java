
package org.openelis.meta;

/**
  * Method META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class MethodMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Method";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              REPORTING_DESCRIPTION					="reportingDescription",
              IS_ACTIVE					="isActive",
              ACTIVE_BEGIN					="activeBegin",
              ACTIVE_END					="activeEnd";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public MethodMeta() {
		init();        
    }
    
    public MethodMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getReportingDescription() {
        return path + REPORTING_DESCRIPTION;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getActiveBegin() {
        return path + ACTIVE_BEGIN;
    } 

    public String getActiveEnd() {
        return path + ACTIVE_END;
    } 

  
}   
