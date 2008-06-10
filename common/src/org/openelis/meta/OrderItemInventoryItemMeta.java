package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderItemInventoryItemMeta implements Meta{
    private String tableName = "inventoryItem";
    private String entityName = "orderItem.inventoryItem";
    private boolean includeInFrom = true;
    
    public static final String
     ID                     = "inventoryItem.id",
     NAME                   = "inventoryItem.name",
     DESCRIPTION            = "inventoryItem.description",
     CATEGORY               = "inventoryItem.category",
     STORE                  = "inventoryItem.store",
     QUANITY_MIN_LEVEL      = "inventoryItem.quantityMinLevel",
     QUANTITY_MAX_LEVEL     = "inventoryItem.quantityMaxLevel",
     QUANTITY_TO_REORDER    = "inventoryItem.quantityToReorder",
     PURCHASED_UNITS        = "inventoryItem.purchasedUnits",
     DISPENSED_UNITS        = "inventoryItem.dispensedUnits",
     IS_REORDER_AUTO        = "inventoryItem.isReorderAuto",
     IS_LOT_MAINTAINED      = "inventoryItem.isLotMaintained",
     IS_SERIAL_MAINTAINED   = "inventoryItem.isSerialMaintained",
     IS_ACTIVE              = "inventoryItem.isActive", 
     IS_BULK                = "inventoryItem.isBulk",
     IS_NOT_FOR_SALE        = "inventoryItem.isNotForSale",
     IS_SUB_ASSEMBLY        = "inventoryItem.isSubAssembly",
     IS_LABOR               = "inventoryItem.isLabor",
     IS_NO_INVENTORY        = "inventoryItem.isNoInventory",
     PRODUCT_URI            = "inventoryItem.productUri",
     AVERAGE_LEAD_TIME      = "inventoryItem.averageLeadTime",
     AVERAGE_COST           = "inventoryItem.averageCost",
     AVERAGE_DAILY_USE      = "inventoryItem.averageDailyUse";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, NAME, DESCRIPTION, CATEGORY, STORE, QUANITY_MIN_LEVEL, QUANTITY_MAX_LEVEL, QUANTITY_TO_REORDER, PURCHASED_UNITS,
         DISPENSED_UNITS, IS_REORDER_AUTO, IS_LOT_MAINTAINED, IS_SERIAL_MAINTAINED, IS_ACTIVE, IS_BULK, IS_NOT_FOR_SALE, 
         IS_SUB_ASSEMBLY, IS_LABOR, IS_NO_INVENTORY, PRODUCT_URI, AVERAGE_LEAD_TIME, AVERAGE_COST, AVERAGE_DAILY_USE};
    
    private static HashMap<String,String> columnHashList;
    
    private static final OrderItemInventoryItemMeta orderItemInventoryItemMeta = new OrderItemInventoryItemMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(14), "");
    }

    private OrderItemInventoryItemMeta() {

    }
    
    public static OrderItemInventoryItemMeta getInstance(){
        return orderItemInventoryItemMeta;
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
    
    public static String name(){
        return columnNames[1];
    }
    
    public static String description(){
        return columnNames[2];
    }
    
    public static String category(){
        return columnNames[3];
    }
    
    public static String store(){
        return columnNames[4];
    }
        
    public static String quantityMinLevel(){
        return columnNames[5];
    }
    
    public static String quantityMaxLevel(){
        return columnNames[6];
    }
    
    public static String quantityToReorder(){
        return columnNames[7];
    }
    
    public static String purchasedUnits(){
        return columnNames[8];
    }
    
    public static String dispensedUnits(){
        return columnNames[9];
    }
    
    public static String isReorderAuto(){
        return columnNames[10];
    }
    
    public static String isLotMaintained(){
        return columnNames[11];
    }
    
    public static String isSerialMaintained(){
        return columnNames[12];
    }
    
    public static String isActive(){
        return columnNames[13];
    }
    
    public static String isBulk(){
        return columnNames[14];
    }
    
    public static String isNotForSale(){
        return columnNames[15];
    }
    
    public static String isSubAssembly(){
        return columnNames[16];
    }
    
    public static String isLabor(){
        return columnNames[17];
    }
    
    public static String isNoInventory(){
        return columnNames[18];
    }
    
    public static String productUri(){
        return columnNames[19];
    }
    
    public static String averageLeadTime(){
        return columnNames[20];
    }
    
    public static String averageCost(){
        return columnNames[21];
    }
    
    public static String averageDailyUse(){
        return columnNames[22];
    }
}
