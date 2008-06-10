package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderItemMeta implements Meta{
    private String tableName = "orderItem";
    private String entityName = "ordr.orderItem";
    private boolean includeInFrom = true;
    
    public static final String
     ID                     = "orderItem.id",
     ORDER                  = "orderItem.order",
     INVENTORY_LOCATION     = "orderItem.inventoryLocation",
     QUANTITY_REQUESTED     = "orderItem.quantityRequested",
     QUANTITY_RECEIVED      = "orderItem.quantityReceived";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, ORDER, INVENTORY_LOCATION, QUANTITY_REQUESTED, QUANTITY_RECEIVED};
    
    private static HashMap<String,String> columnHashList;
    
    private static final OrderItemMeta orderItemMeta = new OrderItemMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(10), "");
    }

    private OrderItemMeta() {

    }
    
    public static OrderItemMeta getInstance(){
        return orderItemMeta;
    }
    
    public String[] getColumnList() {
        return columnNames;
    }

    public String getTable() {
        return tableName;
    }
    
    public String getEntity(){
        return entityName;
    }
    
    public boolean includeInFrom(){
        return includeInFrom;
    }
    
    public boolean hasColumn(String columnName){
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String order(){
        return columnNames[1];
    }
    
    public static String inventoryLocation(){
        return columnNames[2];
    }
    
    public static String quantityRequested(){
        return columnNames[3];
    }
    
    public static String quantityReceived(){
        return columnNames[4];
    }
}
