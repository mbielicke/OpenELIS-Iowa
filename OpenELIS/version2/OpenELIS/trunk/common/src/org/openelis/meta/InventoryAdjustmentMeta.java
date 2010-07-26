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
 * InventoryAdjustment META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class InventoryAdjustmentMeta implements Meta, MetaMap {

    private static final String    ID = "_inventoryAdjustment.id",
                                   DESCRIPTION = "_inventoryAdjustment.description",
                                   SYSTEM_USER_ID = "_inventoryAdjustment.systemUserId",
                                   ADJUSTMENT_DATE = "_inventoryAdjustment.adjustmentDate",
            
                                   INVENTORY_X_ADJUST_ID = " _inventoryXAdjust.id",
                                   INVENTORY_X_ADJUST_INVENTORY_ADJUSTMENT_ID = "_inventoryXAdjust.inventoryAdjustmentId",
                                   INVENTORY_X_ADJUST_INVENTORY_LOCATION_ID = "_inventoryXAdjust.inventoryLocationId",
                                   INVENTORY_X_ADJUST_QUANTITY = "_inventoryXAdjust.quantity",
                                   INVENTORY_X_ADJUST_PHYSICAL_COUNT = "_inventoryXAdjust.physicalCount",
    
                                   INV_LOC_ID = "_inventoryLocation.id",
                                   INV_LOC_INVENTORY_ITEM_ID = "_inventoryLocation.inventoryItemId",
                                   INV_LOC_LOT_NUMBER = "_inventoryLocation.lotNumber",
                                   INV_LOC_STORAGE_LOCATION_ID = "_inventoryLocation.storageLocationId",
                                   INV_LOC_QUANTITY_ON_HAND = "_inventoryLocation.quantityOnhand",
                                   INV_LOC_EXPIRATION_DATE = "_inventoryLocation.expirationDate",
                                   
                                   INV_LOC_INV_ITEM_ID   = "_inventoryLocation.inventoryItem.id", 
                                   INV_LOC_INV_ITEM_NAME = "_inventoryLocation.inventoryItem.name",
                                   INV_LOC_INV_ITEM_DESCRIPTION = "_inventoryLocation.inventoryItem.description",
                                   INV_LOC_INV_ITEM_CATEGORY_ID = "_inventoryLocation.inventoryItem.categoryId", 
                                   INV_LOC_INV_ITEM_STORE_ID = "_inventoryLocation.inventoryItem.storeId",
                                   INV_LOC_INV_ITEM_QUANTITY_MIN_LEVEL = "_inventoryLocation.inventoryItem.quantityMinLevel",
                                   INV_LOC_INV_ITEM_QUANTITY_MAX_LEVEL = "_inventoryLocation.inventoryItem.quantityMaxLevel",
                                   INV_LOC_INV_ITEM_QUANTITY_TO_REORDER = "_inventoryLocation.inventoryItem.quantityToReorder",
                                   INV_LOC_INV_ITEM_DISPENSED_UNITS_ID = "_inventoryLocation.inventoryItem.dispensedUnitsId",
                                   INV_LOC_INV_ITEM_IS_REORDER_AUTO = "_inventoryLocation.inventoryItem.isReorderAuto",
                                   INV_LOC_INV_ITEM_IS_LOT_MAINTAINED = "_inventoryLocation.inventoryItem.isLotMaintained",
                                   INV_LOC_INV_ITEM_IS_SERIAL_MAINTAINED = "_inventoryLocation.inventoryItem.isSerialMaintained",
                                   INV_LOC_INV_ITEM_IS_ACTIVE = "_inventoryLocation.inventoryItem.isActive",
                                   INV_LOC_INV_ITEM_IS_BULK = "_inventoryLocation.inventoryItem.isBulk",
                                   INV_LOC_INV_ITEM_IS_NOT_FOR_SALE = "_inventoryLocation.inventoryItem.isNotForSale",
                                   INV_LOC_INV_ITEM_IS_SUB_ASSEMBLY = "_inventoryLocation.inventoryItem.isSubAssembly",
                                   INV_LOC_INV_ITEM_IS_LABOR = "_inventoryLocation.inventoryItem.isLabor",
                                   INV_LOC_INV_ITEM_IS_NOT_INVENTORIED = "_inventoryLocation.inventoryItem.isNotInventoried",
                                   INV_LOC_INV_ITEM_PRODUCT_URI = "_inventoryLocation.inventoryItem.productUri",
                                   INV_LOC_INV_ITEM_AVERAGE_LEAD_TIME = "_inventoryLocation.inventoryItem.averageLeadTime",
                                   INV_LOC_INV_ITEM_AVERAGE_COST = "_inventoryLocation.inventoryItem.averageCost",
                                   INV_LOC_INV_ITEM_AVERAGE_DAILY_USE = "_inventoryLocation.inventoryItem.averageDailyUse",
                                   INV_LOC_INV_ITEM_PARENT_INVENTORY_ITEM_ID = "_inventoryLocation.inventoryItem.parentInventoryItemId",
                                   INV_LOC_INV_ITEM_PARENT_RATIO = "_inventoryLocation.inventoryItem.parentRatio";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, DESCRIPTION, SYSTEM_USER_ID, ADJUSTMENT_DATE,
                                                  INVENTORY_X_ADJUST_ID, INVENTORY_X_ADJUST_INVENTORY_ADJUSTMENT_ID,
                                                  INVENTORY_X_ADJUST_INVENTORY_LOCATION_ID, INVENTORY_X_ADJUST_QUANTITY,
                                                  INVENTORY_X_ADJUST_PHYSICAL_COUNT, INV_LOC_ID,
                                                  INV_LOC_INVENTORY_ITEM_ID, INV_LOC_LOT_NUMBER,
                                                  INV_LOC_STORAGE_LOCATION_ID, INV_LOC_QUANTITY_ON_HAND,
                                                  INV_LOC_EXPIRATION_DATE, INV_LOC_INV_ITEM_ID, INV_LOC_INV_ITEM_NAME,
                                                  INV_LOC_INV_ITEM_DESCRIPTION, INV_LOC_INV_ITEM_CATEGORY_ID, INV_LOC_INV_ITEM_STORE_ID,
                                                  INV_LOC_INV_ITEM_QUANTITY_MIN_LEVEL, INV_LOC_INV_ITEM_QUANTITY_MAX_LEVEL,
                                                  INV_LOC_INV_ITEM_QUANTITY_TO_REORDER, INV_LOC_INV_ITEM_DISPENSED_UNITS_ID,
                                                  INV_LOC_INV_ITEM_IS_REORDER_AUTO, INV_LOC_INV_ITEM_IS_LOT_MAINTAINED,
                                                  INV_LOC_INV_ITEM_IS_SERIAL_MAINTAINED, INV_LOC_INV_ITEM_IS_ACTIVE, INV_LOC_INV_ITEM_IS_BULK,
                                                  INV_LOC_INV_ITEM_IS_NOT_FOR_SALE, INV_LOC_INV_ITEM_IS_SUB_ASSEMBLY, INV_LOC_INV_ITEM_IS_LABOR,
                                                  INV_LOC_INV_ITEM_IS_NOT_INVENTORIED, INV_LOC_INV_ITEM_PRODUCT_URI,
                                                  INV_LOC_INV_ITEM_AVERAGE_LEAD_TIME, INV_LOC_INV_ITEM_AVERAGE_COST,
                                                  INV_LOC_INV_ITEM_AVERAGE_DAILY_USE, INV_LOC_INV_ITEM_PARENT_INVENTORY_ITEM_ID,
                                                  INV_LOC_INV_ITEM_PARENT_RATIO));

    }

    public static String getId() {
        return ID;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }
    
    public static String getAdjustmentDate() {
        return ADJUSTMENT_DATE;
    } 

    public static String getInventoryXAdjustId() {
        return INVENTORY_X_ADJUST_ID;
    }
    
    public static String getInventoryXAdjustInventoryAdjustmentId() {
        return INVENTORY_X_ADJUST_INVENTORY_ADJUSTMENT_ID;
    }
    
    public static String getInventoryXAdjustInventoryLocationId() {
        return INVENTORY_X_ADJUST_INVENTORY_LOCATION_ID;
    }
    
    public static String getInventoryXAdjustQuantity() {
        return INVENTORY_X_ADJUST_QUANTITY;
    }
    
    public static String getInventoryXAdjustPhysicalCount() {
        return INVENTORY_X_ADJUST_PHYSICAL_COUNT;
    }
    
    public static String getInventoryLocationId() {
        return INV_LOC_ID;
    }
    
    public static String getInventoryLocationInventoryItemId() {
        return INV_LOC_INVENTORY_ITEM_ID;
    }
    
    public static String getInventoryLocationLotNumber() {
        return INV_LOC_LOT_NUMBER;
    }

    public static String getInventoryLocationStorageLocationId() {
        return INV_LOC_STORAGE_LOCATION_ID;
    }

    public static String getInventoryLocationQuantityOnhand() {
        return INV_LOC_QUANTITY_ON_HAND;
    }

    public static String getInventoryLocationExpirationDate() {
        return INV_LOC_EXPIRATION_DATE;
    }   

    public static String getInventoryLocationInventoryItemName() {
        return INV_LOC_INV_ITEM_NAME;
    }

    public static String getInventoryLocationInventoryItemDescription() {
        return INV_LOC_INV_ITEM_DESCRIPTION;
    }

    public static String getInventoryLocationInventoryItemCategoryId() {
        return INV_LOC_INV_ITEM_CATEGORY_ID;
    }

    public static String getInventoryLocationInventoryItemStoreId() {
        return INV_LOC_INV_ITEM_STORE_ID;
    }

    public static String getInventoryLocationInventoryItemQuantityMinLevel() {
        return INV_LOC_INV_ITEM_QUANTITY_MIN_LEVEL;
    }

    public static String getInventoryLocationInventoryItemQuantityMaxLevel() {
        return INV_LOC_INV_ITEM_QUANTITY_MAX_LEVEL;
    }

    public static String getInventoryLocationInventoryItemQuantityToReorder() {
        return INV_LOC_INV_ITEM_QUANTITY_TO_REORDER;
    }

    public static String getInventoryLocationInventoryItemDispensedUnitsId() {
        return INV_LOC_INV_ITEM_DISPENSED_UNITS_ID;
    }

    public static String getInventoryLocationInventoryItemIsReorderAuto() {
        return INV_LOC_INV_ITEM_IS_REORDER_AUTO;
    }

    public static String getInventoryLocationInventoryItemIsLotMaintained() {
        return INV_LOC_INV_ITEM_IS_LOT_MAINTAINED;
    }

    public static String getInventoryLocationInventoryItemIsSerialMaintained() {
        return INV_LOC_INV_ITEM_IS_SERIAL_MAINTAINED;
    }

    public static String getInventoryLocationInventoryItemIsActive() {
        return INV_LOC_INV_ITEM_IS_ACTIVE;
    }

    public static String getInventoryLocationInventoryItemIsBulk() {
        return INV_LOC_INV_ITEM_IS_BULK;
    }

    public static String getInventoryLocationInventoryItemIsNotForSale() {
        return INV_LOC_INV_ITEM_IS_NOT_FOR_SALE;
    }

    public static String getInventoryLocationInventoryItemIsSubAssembly() {
        return INV_LOC_INV_ITEM_IS_SUB_ASSEMBLY;
    }

    public static String getInventoryLocationInventoryItemIsLabor() {
        return INV_LOC_INV_ITEM_IS_LABOR;
    }

    public static String getInventoryLocationInventoryItemIsNotInventoried() {
        return INV_LOC_INV_ITEM_IS_NOT_INVENTORIED;
    }

    public static String getInventoryLocationInventoryItemProductUri() {
        return INV_LOC_INV_ITEM_PRODUCT_URI;
    }

    public static String getInventoryLocationInventoryItemAverageLeadTime() {
        return INV_LOC_INV_ITEM_AVERAGE_LEAD_TIME;
    }

    public static String getInventoryLocationInventoryItemAverageCost() {
        return INV_LOC_INV_ITEM_AVERAGE_COST;
    }

    public static String getInventoryLocationInventoryItemAverageDailyUse() {
        return INV_LOC_INV_ITEM_AVERAGE_DAILY_USE;
    }

    public static String getInventoryLocationInventoryItemParentInventoryItemId() {
        return INV_LOC_INV_ITEM_PARENT_INVENTORY_ITEM_ID;
    }

    public static String getInventoryLocationInventoryItemParentRatio() {
        return INV_LOC_INV_ITEM_PARENT_RATIO;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from, transFrom, invLocFrom;       
        
        transFrom = ", IN (_inventoryAdjustment.transAdjustmentLocation) _transAdjustmentLocation ";
        invLocFrom = ", IN (_transAdjustmentLocation.inventoryLocation) _inventoryLocation ";
        
        from = "InventoryAdjustment _inventoryAdjustment ";
        if (where.indexOf("transAdjustmentLocation.") > -1)
            from += transFrom;
        if (where.indexOf("inventoryLocation.") > -1) {
            if (from.indexOf(transFrom) < 0)
                from += transFrom + invLocFrom;
            else
                from += invLocFrom;
        }            

        return from;
    }

}
