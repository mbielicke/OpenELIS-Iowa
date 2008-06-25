
package org.openelis.meta;

/**
  * OrderItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrderItemMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "OrderItem";
	
	private static final String
              ID					="id",
              ORDER_ID					="orderId",
              INVENTORY_ITEM_ID					="inventoryItemId",
              QUANTITY_REQUESTED					="quantityRequested";

  	private static final String[] columnNames = {
  	  ID,ORDER_ID,INVENTORY_ITEM_ID,QUANTITY_REQUESTED};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrderItemMeta() {
		init();        
    }
    
    public OrderItemMeta(String path) {
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

    public String getOrderId() {
        return path + ORDER_ID;
    } 

    public String getInventoryItemId() {
        return path + INVENTORY_ITEM_ID;
    } 

    public String getQuantityRequested() {
        return path + QUANTITY_REQUESTED;
    } 

  
}   
