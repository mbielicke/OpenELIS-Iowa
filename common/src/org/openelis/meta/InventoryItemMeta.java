
package org.openelis.meta;

/**
  * InventoryItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class InventoryItemMeta implements Meta {
  	private String path = "";
	private static final String entityName = "InventoryItem";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              CATEGORY_ID					="categoryId",
              STORE_ID					="storeId",
              QUANTITY_MIN_LEVEL					="quantityMinLevel",
              QUANTITY_MAX_LEVEL					="quantityMaxLevel",
              QUANTITY_TO_REORDER					="quantityToReorder",
              PURCHASED_UNITS_ID					="purchasedUnitsId",
              DISPENSED_UNITS_ID					="dispensedUnitsId",
              IS_REORDER_AUTO					="isReorderAuto",
              IS_LOT_MAINTAINED					="isLotMaintained",
              IS_SERIAL_MAINTAINED					="isSerialMaintained",
              IS_ACTIVE					="isActive",
              IS_BULK					="isBulk",
              IS_NOT_FOR_SALE					="isNotForSale",
              IS_SUB_ASSEMBLY					="isSubAssembly",
              IS_LABOR					="isLabor",
              IS_NO_INVENTORY					="isNoInventory",
              PRODUCT_URI					="productUri",
              AVERAGE_LEAD_TIME					="averageLeadTime",
              AVERAGE_COST					="averageCost",
              AVERAGE_DAILY_USE					="averageDailyUse";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,CATEGORY_ID,STORE_ID,QUANTITY_MIN_LEVEL,QUANTITY_MAX_LEVEL,QUANTITY_TO_REORDER,PURCHASED_UNITS_ID,DISPENSED_UNITS_ID,IS_REORDER_AUTO,IS_LOT_MAINTAINED,IS_SERIAL_MAINTAINED,IS_ACTIVE,IS_BULK,IS_NOT_FOR_SALE,IS_SUB_ASSEMBLY,IS_LABOR,IS_NO_INVENTORY,PRODUCT_URI,AVERAGE_LEAD_TIME,AVERAGE_COST,AVERAGE_DAILY_USE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryItemMeta() {
		init();        
    }
    
    public InventoryItemMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getCategoryId() {
        return path + CATEGORY_ID;
    } 

    public String getStoreId() {
        return path + STORE_ID;
    } 

    public String getQuantityMinLevel() {
        return path + QUANTITY_MIN_LEVEL;
    } 

    public String getQuantityMaxLevel() {
        return path + QUANTITY_MAX_LEVEL;
    } 

    public String getQuantityToReorder() {
        return path + QUANTITY_TO_REORDER;
    } 

    public String getPurchasedUnitsId() {
        return path + PURCHASED_UNITS_ID;
    } 

    public String getDispensedUnitsId() {
        return path + DISPENSED_UNITS_ID;
    } 

    public String getIsReorderAuto() {
        return path + IS_REORDER_AUTO;
    } 

    public String getIsLotMaintained() {
        return path + IS_LOT_MAINTAINED;
    } 

    public String getIsSerialMaintained() {
        return path + IS_SERIAL_MAINTAINED;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getIsBulk() {
        return path + IS_BULK;
    } 

    public String getIsNotForSale() {
        return path + IS_NOT_FOR_SALE;
    } 

    public String getIsSubAssembly() {
        return path + IS_SUB_ASSEMBLY;
    } 

    public String getIsLabor() {
        return path + IS_LABOR;
    } 

    public String getIsNoInventory() {
        return path + IS_NO_INVENTORY;
    } 

    public String getProductUri() {
        return path + PRODUCT_URI;
    } 

    public String getAverageLeadTime() {
        return path + AVERAGE_LEAD_TIME;
    } 

    public String getAverageCost() {
        return path + AVERAGE_COST;
    } 

    public String getAverageDailyUse() {
        return path + AVERAGE_DAILY_USE;
    } 

  
}   
