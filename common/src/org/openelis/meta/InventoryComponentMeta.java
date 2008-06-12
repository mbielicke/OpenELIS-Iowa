
package org.openelis.meta;

/**
  * InventoryComponent META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InventoryComponentMeta implements Meta {
  	private static final String tableName = "inventory_component";
	private static final String entityName = "inventory_item.inventoryComponent";
	private boolean includeInFrom = true;
	
	public static final String
              ID					= "inventory_component.id",
              INVENTORY_ITEM_ID     = "inventory_component.inventoryItemId",
              COMPONENT_ID			= "inventory_component.componentId",
              QUANTITY				= "inventory_component.quantity";


  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,COMPONENT_ID,QUANTITY};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InventoryComponentMeta inventory_componentMeta = new InventoryComponentMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InventoryComponentMeta() {
        
    }
    
    public static InventoryComponentMeta getInstance() {
        return inventory_componentMeta;
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

  public static String getInventoryItemId() {
    return INVENTORY_ITEM_ID;
  } 

  public static String getComponentId() {
    return COMPONENT_ID;
  } 

  public static String getQuantity() {
    return QUANTITY;
  } 

  
}   
