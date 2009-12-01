/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.meta;

/**
 * InventoryItem META Data
 */

import java.util.Arrays;
import java.util.HashSet;
import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class InventoryItemMeta implements Meta, MetaMap {
    private static final String   ID   = "_inventoryItem.id", 
                                  NAME = "_inventoryItem.name",
                                  DESCRIPTION = "_inventoryItem.description",
                                  CATEGORY_ID = "_inventoryItem.categoryId", 
                                  STORE_ID = "_inventoryItem.storeId",
                                  QUANTITY_MIN_LEVEL = "_inventoryItem.quantityMinLevel",
                                  QUANTITY_MAX_LEVEL = "_inventoryItem.quantityMaxLevel",
                                  QUANTITY_TO_REORDER = "_inventoryItem.quantityToReorder",
                                  DISPENSED_UNITS_ID = "_inventoryItem.dispensedUnitsId",
                                  IS_REORDER_AUTO = "_inventoryItem.isReorderAuto",
                                  IS_LOT_MAINTAINED = "_inventoryItem.isLotMaintained",
                                  IS_SERIAL_MAINTAINED = "_inventoryItem.isSerialMaintained",
                                  IS_ACTIVE = "_inventoryItem.isActive",
                                  IS_BULK = "_inventoryItem.isBulk",
                                  IS_NOT_FOR_SALE = "_inventoryItem.isNotForSale",
                                  IS_SUB_ASSEMBLY = "_inventoryItem.isSubAssembly",
                                  IS_LABOR = "_inventoryItem.isLabor",
                                  IS_NOT_INVENTORIED = "_inventoryItem.isNotInventoried",
                                  PRODUCT_URI = "_inventoryItem.productUri",
                                  AVERAGE_LEAD_TIME = "_inventoryItem.averageLeadTime",
                                  AVERAGE_COST = "_inventoryItem.averageCost",
                                  AVERAGE_DAILY_USE = "_inventoryItem.averageDailyUse",
                                  PARENT_INVENTORY_ITEM_ID = "_inventoryItem.parentInventoryItemId",
                                  PARENT_RATIO = "_inventoryItem.parentRatio",
                                  
                                  CMP_ID = "_inventoryComponent.id",
                                  CMP_INVENTORY_ITEM_ID = "_inventoryComponent.inventoryItemId",
                                  CMP_COMPONENT_ID = "_inventoryComponent.componentId",
                                  CMP_QUANTITY = "_inventoryComponent.quantity",
                                  
                                  LOC_ID = "_inventoryLocation.id",
                                  LOC_INVENTORY_ITEM_ID = "_inventoryLocation.inventoryItemId",
                                  LOC_LOT_NUMBER = "_inventoryLocation.lotNumber",
                                  LOC_STORAGE_LOCATION_ID = "_inventoryLocation.storageLocationId",
                                  LOC_QUANTITY_ONHAND = "_inventoryLocation.quantityOnhand",
                                  LOC_EXPIRATION_DATE = "_inventoryLocation.expirationDate",
                                  
                                  PARENT_INVENTORY_ITEM_NAME = "_inventoryItem.parentInventoryItem.name",
                                  CMP_COMPONENT_NAME = "_inventoryComponent.componentInventoryItem.name",
                                  CMP_COMPONENT_DESCRIPTION = "_inventoryComponent.componentInventoryItem.description",
                                  LOC_STORAGE_LOCATION_NAME = "_inventoryLocation.storageLocation.name";

    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, NAME, DESCRIPTION, CATEGORY_ID, STORE_ID,
                                                  QUANTITY_MIN_LEVEL, QUANTITY_MAX_LEVEL,
                                                  QUANTITY_TO_REORDER, DISPENSED_UNITS_ID,
                                                  IS_REORDER_AUTO, IS_LOT_MAINTAINED,
                                                  IS_SERIAL_MAINTAINED, IS_ACTIVE, IS_BULK,
                                                  IS_NOT_FOR_SALE, IS_SUB_ASSEMBLY, IS_LABOR,
                                                  IS_NOT_INVENTORIED, PRODUCT_URI,
                                                  AVERAGE_LEAD_TIME, AVERAGE_COST,
                                                  AVERAGE_DAILY_USE, PARENT_INVENTORY_ITEM_ID,
                                                  PARENT_RATIO,
                                                  CMP_ID, CMP_INVENTORY_ITEM_ID, CMP_COMPONENT_ID,
                                                  CMP_QUANTITY, LOC_ID,
                                                  LOC_INVENTORY_ITEM_ID, LOC_LOT_NUMBER,
                                                  LOC_STORAGE_LOCATION_ID, LOC_QUANTITY_ONHAND,
                                                  LOC_EXPIRATION_DATE,
                                                  PARENT_INVENTORY_ITEM_NAME, CMP_COMPONENT_NAME,
                                                  CMP_COMPONENT_DESCRIPTION, LOC_STORAGE_LOCATION_NAME));
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

    public static String getIsNotInventoried() {
        return IS_NOT_INVENTORIED;
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

    public static String getParentInventoryItemId() {
        return PARENT_INVENTORY_ITEM_ID;
    }

    public static String getParentInventoryItemName() {
        return PARENT_INVENTORY_ITEM_NAME;
    }

    public static String getParentRatio() {
        return PARENT_RATIO;
    }
    
    public static String getComponentId() {
        return CMP_ID;
    } 

    public static String getComponentInventoryItemId() {
        return CMP_INVENTORY_ITEM_ID;
    } 

    public static String getComponentComponentId() {
        return CMP_COMPONENT_ID;
    } 

    public static String getComponentName() {
        return CMP_COMPONENT_NAME;
    } 

    public static String getComponentDescription() {
        return CMP_COMPONENT_DESCRIPTION;
    } 

    public static String getComponentQuantity() {
        return CMP_QUANTITY;
    }

    public static String getLocationId() {
        return LOC_ID;
    }
    
    public static String getLocationInventoryItemId() {
        return LOC_INVENTORY_ITEM_ID;
    }
    
    public static String getLocationLotNumber() {
        return LOC_LOT_NUMBER;
    }

    public static String getLocationStorageLocationId() {
        return LOC_STORAGE_LOCATION_ID;
    }

    public static String getLocationStorageLocationName() {
        return LOC_STORAGE_LOCATION_NAME;
    }

    public static String getLocationQuantityOnhand() {
        return LOC_QUANTITY_ONHAND;
    }

    public static String getLocationExpirationDate() {
        return LOC_EXPIRATION_DATE;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "InventoryItem _inventoryItem ";
        if (where.indexOf("inventoryComponent.") > -1)
            from += ",IN (_inventoryItem.inventoryComponent) _inventoryComponent ";
        if (where.indexOf("inventoryLocation.") > -1)
            from += ",IN (_inventoryItem.inventoryLocation) _inventoryLocation ";

        return from;
    }
}
