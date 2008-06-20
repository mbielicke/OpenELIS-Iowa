package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderItemInventoryItemMeta implements Meta{
    private String tableName = "inventoryItem";
    private String entityName = "order_item.inventoryItem";
    private boolean includeInFrom = true;
    
    public static final String
     ID                     = "inventoryItem.id",
     NAME                   = "inventoryItem.name",
     DESCRIPTION            = "inventoryItem.description",
     CATEGORY_ID            = "inventoryItem.categoryId",
     STORE_ID               = "inventoryItem.storeId",
     QUANITY_MIN_LEVEL      = "inventoryItem.quantityMinLevel",
     QUANTITY_MAX_LEVEL     = "inventoryItem.quantityMaxLevel",
     QUANTITY_TO_REORDER    = "inventoryItem.quantityToReorder",
     PURCHASED_UNITS_ID     = "inventoryItem.purchasedUnitsId",
     DISPENSED_UNITS_ID     = "inventoryItem.dispensedUnitsId",
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
         ID, NAME, DESCRIPTION, CATEGORY_ID, STORE_ID, QUANITY_MIN_LEVEL, QUANTITY_MAX_LEVEL, QUANTITY_TO_REORDER, PURCHASED_UNITS_ID,
         DISPENSED_UNITS_ID, IS_REORDER_AUTO, IS_LOT_MAINTAINED, IS_SERIAL_MAINTAINED, IS_ACTIVE, IS_BULK, IS_NOT_FOR_SALE, 
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
    
    public static String getId(){
        return columnNames[0];
    }
    
    public static String getName(){
        return columnNames[1];
    }
    
    public static String getDescription(){
        return columnNames[2];
    }
    
    public static String getCategoryId(){
        return columnNames[3];
    }
    
    public static String getStoreId(){
        return columnNames[4];
    }
        
    public static String getQuantityMinLevel(){
        return columnNames[5];
    }
    
    public static String getQuantityMaxLevel(){
        return columnNames[6];
    }
    
    public static String getQuantityToReorder(){
        return columnNames[7];
    }
    
    public static String getPurchasedUnitsId(){
        return columnNames[8];
    }
    
    public static String getDispensedUnitsId(){
        return columnNames[9];
    }
    
    public static String getIsReorderAuto(){
        return columnNames[10];
    }
    
    public static String getIsLotMaintained(){
        return columnNames[11];
    }
    
    public static String getIsSerialMaintained(){
        return columnNames[12];
    }
    
    public static String getIsActive(){
        return columnNames[13];
    }
    
    public static String getIsBulk(){
        return columnNames[14];
    }
    
    public static String getIsNotForSale(){
        return columnNames[15];
    }
    
    public static String getIsSubAssembly(){
        return columnNames[16];
    }
    
    public static String getIsLabor(){
        return columnNames[17];
    }
    
    public static String getIsNoInventory(){
        return columnNames[18];
    }
    
    public static String getProductUri(){
        return columnNames[19];
    }
    
    public static String getAverageLeadTime(){
        return columnNames[20];
    }
    
    public static String getAverageCost(){
        return columnNames[21];
    }
    
    public static String getAverageDailyUse(){
        return columnNames[22];
    }
}