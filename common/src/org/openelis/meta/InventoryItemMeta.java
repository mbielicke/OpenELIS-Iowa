
package org.openelis.meta;

/**
  * InventoryItem META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InventoryItemMeta implements Meta {
  	private static final String tableName = "inventory_item";
	private static final String entityName = "InventoryItem";
	private boolean includeInFrom = true;
	
	public static final String
              ID					= "inventory_item.id",
              NAME					= "inventory_item.name",
              DESCRIPTION			= "inventory_item.description",
              CATEGORY_ID			= "inventory_item.category_id",
              STORE_ID				= "inventory_item.store_id",
              QUANTITY_MIN_LEVEL	= "inventory_item.quantity_min_level",
              QUANTITY_MAX_LEVEL	= "inventory_item.quantity_max_level",
              QUANTITY_TO_REORDER	= "inventory_item.quantity_to_reorder",
              PURCHASED_UNITS_ID	= "inventory_item.purchased_units_id",
              DISPENSED_UNITS_ID	= "inventory_item.dispensed_units_id",
              IS_REORDER_AUTO		= "inventory_item.is_reorder_auto",
              IS_LOT_MAINTAINED		= "inventory_item.is_lot_maintained",
              IS_SERIAL_MAINTAINED  = "inventory_item.is_serial_maintained",
              IS_ACTIVE				= "inventory_item.is_active",
              IS_BULK			    = "inventory_item.is_bulk",
              IS_NOT_FOR_SALE		= "inventory_item.is_not_for_sale",
              IS_SUB_ASSEMBLY		= "inventory_item.is_sub_assembly",
              IS_LABOR				= "inventory_item.is_labor",
              IS_NO_INVENTORY		= "inventory_item.is_no_inventory",
              PRODUCT_URI			= "inventory_item.product_uri",
              AVERAGE_LEAD_TIME		= "inventory_item.average_lead_time",
              AVERAGE_COST			= "inventory_item.average_cost",
              AVERAGE_DAILY_USE		= "inventory_item.average_daily_use";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,CATEGORY_ID,STORE_ID,QUANTITY_MIN_LEVEL,QUANTITY_MAX_LEVEL,QUANTITY_TO_REORDER,PURCHASED_UNITS_ID,DISPENSED_UNITS_ID,IS_REORDER_AUTO,IS_LOT_MAINTAINED,IS_SERIAL_MAINTAINED,IS_ACTIVE,IS_BULK,IS_NOT_FOR_SALE,IS_SUB_ASSEMBLY,IS_LABOR,IS_NO_INVENTORY,PRODUCT_URI,AVERAGE_LEAD_TIME,AVERAGE_COST,AVERAGE_DAILY_USE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InventoryItemMeta inventory_itemMeta = new InventoryItemMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InventoryItemMeta() {
        
    }
    
    public static InventoryItemMeta getInstance() {
        return inventory_itemMeta;
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
