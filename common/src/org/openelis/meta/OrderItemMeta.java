
package org.openelis.meta;

/**
  * OrderItem META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class OrderItemMeta implements Meta {
  	private static final String tableName = "order_item";
	private static final String entityName = "ordr.OrderItem";
	private boolean includeInFrom = true;
	
	public static final String
              ID					= "order_item.id",
              ORDER_ID				= "order_item.orderId",
              INVENTORY_ITEM_ID		= "order_item.inventoryItemId",
              QUANTITY_REQUESTED    = "order_item.quantityRequested";


  	private static final String[] columnNames = {
  	  ID,ORDER_ID,INVENTORY_ITEM_ID,QUANTITY_REQUESTED};
  	  
	private static HashMap<String,String> columnHashList;

	private static final OrderItemMeta order_itemMeta = new OrderItemMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private OrderItemMeta() {
        
    }
    
    public static OrderItemMeta getInstance() {
        return order_itemMeta;
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

  public static String getOrderId() {
    return ORDER_ID;
  } 

  public static String getInventoryItemId() {
    return INVENTORY_ITEM_ID;
  } 

  public static String getQuantityRequested() {
    return QUANTITY_REQUESTED;
  } 

  
}   
