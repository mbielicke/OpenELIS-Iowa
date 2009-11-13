
package org.openelis.meta;

/**
  * Worksheet META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class WorksheetMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Worksheet";
	
	private static final String
              ID					="id",
              CREATED_DATE					="createdDate",
              SYSTEM_USER_ID					="systemUserId",
              STATUS_ID					="statusId",
              FORMAT_ID					="formatId",
              TEST_ID					="testId";

  	private static final String[] columnNames = {
  	  ID,CREATED_DATE,SYSTEM_USER_ID,STATUS_ID,FORMAT_ID,TEST_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public WorksheetMeta() {
		init();        
    }
    
    public WorksheetMeta(String path) {
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

    public String getCreatedDate() {
        return path + CREATED_DATE;
    } 

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getFormatId() {
        return path + FORMAT_ID;
    } 

    public String getTestId() {
        return path + TEST_ID;
    } 

  
}   
