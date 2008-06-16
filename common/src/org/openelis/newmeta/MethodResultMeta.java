
package org.openelis.newmeta;

/**
  * MethodResult META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class MethodResultMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "MethodResult";
	
	private static final String
              ID					="id",
              METHOD_ID					="methodId",
              RESULT_GROUP_ID					="resultGroupId",
              FLAGS					="flags",
              TYPE					="type",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,METHOD_ID,RESULT_GROUP_ID,FLAGS,TYPE,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public MethodResultMeta() {
		init();        
    }
    
    public MethodResultMeta(String path) {
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

    public String getMethodId() {
        return path + METHOD_ID;
    } 

    public String getResultGroupId() {
        return path + RESULT_GROUP_ID;
    } 

    public String getFlags() {
        return path + FLAGS;
    } 

    public String getType() {
        return path + TYPE;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
