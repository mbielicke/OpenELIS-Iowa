
package org.openelis.meta;

/**
  * InventoryAdjustment META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class InventoryAdjustmentMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InventoryAdjustment";
	
	private static final String
              ID					            ="id",
              DESCRIPTION					    ="description",
              SYSTEM_USER_ID					="systemUserId",
              ADJUSTMENT_DATE					="adjustmentDate";

  	private static final String[] columnNames = {
  	  ID,DESCRIPTION,SYSTEM_USER_ID,ADJUSTMENT_DATE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryAdjustmentMeta() {
		init();        
    }
    
    public InventoryAdjustmentMeta(String path) {
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

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

    public String getAdjustmentDate() {
        return path + ADJUSTMENT_DATE;
    } 

  
}   
