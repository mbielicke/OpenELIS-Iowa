
package org.openelis.newmeta;

/**
  * MethodAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class MethodAnalyteMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "MethodAnalyte";
	
	private static final String
              ID					="id",
              METHOD_ID					="methodId",
              RESULT_GROUP_ID					="resultGroupId",
              SORT_ORDER_ID					="sortOrderId",
              TYPE					="type",
              ANALYTE_ID					="analyteId";

  	private static final String[] columnNames = {
  	  ID,METHOD_ID,RESULT_GROUP_ID,SORT_ORDER_ID,TYPE,ANALYTE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public MethodAnalyteMeta() {
		init();        
    }
    
    public MethodAnalyteMeta(String path) {
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

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getType() {
        return path + TYPE;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

  
}   
