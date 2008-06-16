
package org.openelis.newmeta;

/**
  * PanelItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class PanelItemMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "PanelItem";
	
	private static final String
              ID					="id",
              PANEL_ID					="panelId",
              SORT_ORDER_ID					="sortOrderId",
              TEST_NAME					="testName",
              METHOD_NAME					="methodName";

  	private static final String[] columnNames = {
  	  ID,PANEL_ID,SORT_ORDER_ID,TEST_NAME,METHOD_NAME};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PanelItemMeta() {
		init();        
    }
    
    public PanelItemMeta(String path) {
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

    public String getPanelId() {
        return path + PANEL_ID;
    } 

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getTestName() {
        return path + TEST_NAME;
    } 

    public String getMethodName() {
        return path + METHOD_NAME;
    } 

  
}   
