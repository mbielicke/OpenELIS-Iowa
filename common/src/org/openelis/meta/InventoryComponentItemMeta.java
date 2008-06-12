package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryComponentItemMeta implements Meta{
    private final static String tableName = "inventory_component.componentInventoryItem";
    private final static String entityName = "inventory_component.componentInventoryItem";
    private boolean includeInFrom = false;
    
    public static final String
     ID                     = "inventory_component.componentInventoryItem.id",
     NAME                   = "inventory_component.componentInventoryItem.name",
     DESCRIPTION            = "inventory_component.componentInventoryItem.description",
     CATEGORY_ID            = "inventory_component.componentInventoryItem.category",
     STORE_ID               = "inventory_component.componentInventoryItem.store",
     QUANTITY_MIN_LEVEL     = "inventory_component.componentInventoryItem.quantityMinLevel",
     QUANTITY_MAX_LEVEL     = "inventory_component.componentInventoryItem.quantityMaxLevel",
     QUANTITY_TO_REORDER    = "inventory_component.componentInventoryItem.quantityToReorder",
     PURCHASED_UNITS_ID     = "inventory_component.componentInventoryItem.purchasedUnits",
     DISPENSED_UNITS_ID     = "inventory_component.componentInventoryItem.dispensedUnits",
     IS_REORDER_AUTO        = "inventory_component.componentInventoryItem.isReorderAuto",
     IS_LOT_MAINTAINED      = "inventory_component.componentInventoryItem.isLotMaintained",
     IS_SERIAL_MAINTAINED   = "inventory_component.componentInventoryItem.isSerialMaintained",
     IS_ACTIVE              = "inventory_component.componentInventoryItem.isActive", 
     IS_BULK                = "inventory_component.componentInventoryItem.isBulk",
     IS_NOT_FOR_SALE        = "inventory_component.componentInventoryItem.isNotForSale",
     IS_SUB_ASSEMBLY        = "inventory_component.componentInventoryItem.isSubAssembly",
     IS_LABOR               = "inventory_component.componentInventoryItem.isLabor",
     IS_NO_INVENTORY        = "inventory_component.componentInventoryItem.isNoInventory",
     PRODUCT_URI            = "inventory_component.componentInventoryItem.productUri",
     AVERAGE_LEAD_TIME      = "inventory_component.componentInventoryItem.averageLeadTime",
     AVERAGE_COST           = "inventory_component.componentInventoryItem.averageCost",
     AVERAGE_DAILY_USE      = "inventory_component.componentInventoryItem.averageDailyUse";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, NAME, DESCRIPTION, CATEGORY_ID, STORE_ID, QUANTITY_MIN_LEVEL, QUANTITY_MAX_LEVEL, QUANTITY_TO_REORDER, PURCHASED_UNITS_ID,
         DISPENSED_UNITS_ID, IS_REORDER_AUTO, IS_LOT_MAINTAINED, IS_SERIAL_MAINTAINED, IS_ACTIVE, IS_BULK, IS_NOT_FOR_SALE, 
         IS_SUB_ASSEMBLY, IS_LABOR, IS_NO_INVENTORY, PRODUCT_URI, AVERAGE_LEAD_TIME, AVERAGE_COST, AVERAGE_DAILY_USE};
    
    private static HashMap<String,String> columnHashList;
    
    private static final InventoryComponentItemMeta inventoryComponentItemMeta = new InventoryComponentItemMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
    }

    private InventoryComponentItemMeta() {

    }
    
    public static InventoryComponentItemMeta getInstance(){
        return inventoryComponentItemMeta;
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
    
    public static String getId() {
        return ID;
      } 

      public static String getName() {
        return NAME;
      } 

      public static String getDescription() {
        return DESCRIPTION;
      } 

      public static String getCategoryId() {
        return CATEGORY_ID;
      } 

      public static String getStoreId() {
        return STORE_ID;
      } 

      public static String getQuantityMinLevel() {
        return QUANTITY_MIN_LEVEL;
      } 

      public static String getQuantityMaxLevel() {
        return QUANTITY_MAX_LEVEL;
      } 

      public static String getQuantityToReorder() {
        return QUANTITY_TO_REORDER;
      } 

      public static String getPurchasedUnitsId() {
        return PURCHASED_UNITS_ID;
      } 

      public static String getDispensedUnitsId() {
        return DISPENSED_UNITS_ID;
      } 

      public static String getIsReorderAuto() {
        return IS_REORDER_AUTO;
      } 

      public static String getIsLotMaintained() {
        return IS_LOT_MAINTAINED;
      } 

      public static String getIsSerialMaintained() {
        return IS_SERIAL_MAINTAINED;
      } 

      public static String getIsActive() {
        return IS_ACTIVE;
      } 

      public static String getIsBulk() {
        return IS_BULK;
      } 

      public static String getIsNotForSale() {
        return IS_NOT_FOR_SALE;
      } 

      public static String getIsSubAssembly() {
        return IS_SUB_ASSEMBLY;
      } 

      public static String getIsLabor() {
        return IS_LABOR;
      } 

      public static String getIsNoInventory() {
        return IS_NO_INVENTORY;
      } 

      public static String getProductUri() {
        return PRODUCT_URI;
      } 

      public static String getAverageLeadTime() {
        return AVERAGE_LEAD_TIME;
      } 

      public static String getAverageCost() {
        return AVERAGE_COST;
      } 

      public static String getAverageDailyUse() {
        return AVERAGE_DAILY_USE;
      } 
}
