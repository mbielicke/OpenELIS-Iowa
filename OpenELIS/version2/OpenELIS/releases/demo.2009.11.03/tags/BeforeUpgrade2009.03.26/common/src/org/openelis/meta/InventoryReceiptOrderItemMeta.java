
package org.openelis.meta;

/**
  * InventoryReceiptOrderItem META Data
  */

import org.openelis.gwt.common.Meta;

import java.util.HashSet;

public class InventoryReceiptOrderItemMeta implements Meta {
  	private String path = "";
	private static final String entityName = "InventoryReceiptOrderItem";
	
	private static final String
              ID					        ="id",
              INVENTORY_RECEIPT_ID			="inventoryReceiptId",
              ORDER_ITEM_ID					="orderItemId";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_RECEIPT_ID,ORDER_ITEM_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryReceiptOrderItemMeta() {
		init();        
    }
    
    public InventoryReceiptOrderItemMeta(String path) {
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

    public String getInventoryReceiptId() {
        return path + INVENTORY_RECEIPT_ID;
    } 

    public String getOrderItemId() {
        return path + ORDER_ITEM_ID;
    } 

  
}   
