
package org.openelis.meta;

/**
  * InventoryComponent META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class InventoryComponentMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InventoryComponent";
	
	private static final String
              ID					="id",
              INVENTORY_ITEM_ID					="inventoryItemId",
              COMPONENT_ID					="componentId",
              QUANTITY					="quantity";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,COMPONENT_ID,QUANTITY};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryComponentMeta() {
		init();        
    }
    
    public InventoryComponentMeta(String path) {
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

    public String getInventoryItemId() {
        return path + INVENTORY_ITEM_ID;
    } 

    public String getComponentId() {
        return path + COMPONENT_ID;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

  
}   
