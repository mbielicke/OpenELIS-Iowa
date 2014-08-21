/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
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
              AVERAGE_DAILY_USE					="averageDailyUse",
              PARENT_INVENTORY_ITEM_ID             ="parentInventoryItemId",
              PARENT_RATIO                      ="parentRatio";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,CATEGORY_ID,STORE_ID,QUANTITY_MIN_LEVEL,QUANTITY_MAX_LEVEL,QUANTITY_TO_REORDER,DISPENSED_UNITS_ID,IS_REORDER_AUTO,
      IS_LOT_MAINTAINED,IS_SERIAL_MAINTAINED,IS_ACTIVE,IS_BULK,IS_NOT_FOR_SALE,IS_SUB_ASSEMBLY,IS_LABOR,IS_NO_INVENTORY,PRODUCT_URI,
      AVERAGE_LEAD_TIME,AVERAGE_COST,AVERAGE_DAILY_USE,PARENT_INVENTORY_ITEM_ID,PARENT_RATIO};
  	  
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
    
    public String getParentInventoryItemId() {
        return path + PARENT_INVENTORY_ITEM_ID;
    } 
    
    public String getParentRatio() {
        return path + PARENT_RATIO;
    }   
}   
