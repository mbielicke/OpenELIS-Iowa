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
 * InventoryReceipt META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class InventoryReceiptMeta implements Meta, MetaMap {

    private static final String  ID = "_inventoryReceipt.id", 
                                 INVENTORY_ITEM_ID = "_inventoryReceipt.inventoryItemId",                                 
                                 ORGANIZATION_ID = "_inventoryReceipt.organizationId",
                                 RECEIVED_DATE = "_inventoryReceipt.receivedDate", 
                                 QUANTITY_RECEIVED = "_inventoryReceipt.quantityReceived",
                                 UNIT_COST = "_inventoryReceipt.unitCost", 
                                 QC_REFERENCE = "_inventoryReceipt.qcReference",
                                 EXTERNAL_REFERENCE = "_inventoryReceipt.externalReference",
                                 UPC = "_inventoryReceipt.upc",
                                 
                                 INVENTORY_ITEM_NAME = "_inventoryReceipt.inventoryItem.name",
                                 INVENTORY_ITEM_DESCRIPTION = "_inventoryReceipt.inventoryItem.description",
                                 INVENTORY_ITEM_CATEGORY_ID = "_inventoryReceipt.inventoryItem.categoryId", 
                                 INVENTORY_ITEM_STORE_ID = "_inventoryReceipt.inventoryItem.storeId",
                                 INVENTORY_ITEM_QUANTITY_MIN_LEVEL = "_inventoryReceipt.inventoryItem.quantityMinLevel",
                                 INVENTORY_ITEM_QUANTITY_MAX_LEVEL = "_inventoryReceipt.inventoryItem.quantityMaxLevel",
                                 INVENTORY_ITEM_QUANTITY_TO_REORDER = "_inventoryReceipt.inventoryItem.quantityToReorder",
                                 INVENTORY_ITEM_DISPENSED_UNITS_ID = "_inventoryReceipt.inventoryItem.dispensedUnitsId",
                                 INVENTORY_ITEM_IS_REORDER_AUTO = "_inventoryReceipt.inventoryItem.isReorderAuto",
                                 INVENTORY_ITEM_IS_LOT_MAINTAINED = "_inventoryReceipt.inventoryItem.isLotMaintained",
                                 INVENTORY_ITEM_IS_SERIAL_MAINTAINED = "_inventoryReceipt.inventoryItem.isSerialMaintained",
                                 INVENTORY_ITEM_IS_ACTIVE = "_inventoryReceipt.inventoryItem.isActive",
                                 INVENTORY_ITEM_IS_BULK = "_inventoryReceipt.inventoryItem.isBulk",
                                 INVENTORY_ITEM_IS_NOT_FOR_SALE = "_inventoryReceipt.inventoryItem.isNotForSale",
                                 INVENTORY_ITEM_IS_SUB_ASSEMBLY = "_inventoryReceipt.inventoryItem.isSubAssembly",
                                 INVENTORY_ITEM_IS_LABOR = "_inventoryReceipt.inventoryItem.isLabor",
                                 INVENTORY_ITEM_IS_NOT_INVENTORIED = "_inventoryReceipt.inventoryItem.isNotInventoried",
                                 INVENTORY_ITEM_PRODUCT_URI = "_inventoryReceipt.inventoryItem.productUri",
                                 INVENTORY_ITEM_AVERAGE_LEAD_TIME = "_inventoryReceipt.inventoryItem.averageLeadTime",
                                 INVENTORY_ITEM_AVERAGE_COST = "_inventoryReceipt.inventoryItem.averageCost",
                                 INVENTORY_ITEM_AVERAGE_DAILY_USE = "_inventoryReceipt.inventoryItem.averageDailyUse",
                                 INVENTORY_ITEM_PARENT_INVENTORY_ITEM_ID = "_inventoryReceipt.inventoryItem.parentInventoryItemId",
                                 INVENTORY_ITEM_PARENT_RATIO = "_inventoryReceipt.inventoryItem.parentRatio",                                
                                 
                                 INVENTORY_X_PUT_ID = "_inventoryXPut.id",
                                 INVENTORY_X_PUT_INVENTORY_RECEIPT_ID = "_inventoryXPut.inventoryReceiptId", 
                                 INVENTORY_X_PUT_INVENTORY_LOCATION_ID = "_inventoryXPut.inventoryLocationId",
                                 INVENTORY_X_PUT_QUANTITY = "_inventoryXPut.quantity",
                                 
                                 INVENTORY_LOCATION_LOT_NUMBER = "_inventoryLocation.lotNumber", 
                                 INVENTORY_LOCATION_STORAGE_LOCATION_ID = "_inventoryLocation.storageLocationId",
                                 INVENTORY_LOCATION_QUANTITY_ON_HAND = "_inventoryLocation.quantityOnhand",
                                 INVENTORY_LOCATION_EXPIRATION_DATE = "_inventoryLocation.expirationDate",
                                 
                                 ORDER_ITEM_ID = "_orderItem.id", 
                                 ORDER_ITEM_ORDER_ID = "_orderItem.orderId",
                                 ORDER_ITEM_INVENTORY_ITEM_ID = "_orderItem.inventoryItemId",
                                 ORDER_ITEM_QUANTITY = "_orderItem.quantity",
                                 ORDER_ITEM_CATALOG_NUMBER = "_orderItem.catalogNumber",
                                 ORDER_ITEM_UNIT_COST = "_orderItem.unitCost",  
                                 
                                 ORGANIZATION_ADDRESS_ID = "_organization.address.id",
                                 ORGANIZATION_ADDRESS_MULTIPLE_UNIT = "_organization.address.multipleUnit",
                                 ORGANIZATION_ADDRESS_STREET_ADDRESS = "_organization.address.streetAddress",
                                 ORGANIZATION_ADDRESS_CITY = "_organization.address.city",
                                 ORGANIZATION_ADDRESS_STATE = "_organization.address.state",
                                 ORGANIZATION_ADDRESS_ZIP_CODE = "_organization.address.zipCode",
                                 ORGANIZATION_ADDRESS_WORK_PHONE = "_organization.address.workPhone",
                                 ORGANIZATION_ADDRESS_HOME_PHONE = "_organization.address.homePhone",
                                 ORGANIZATION_ADDRESS_CELL_PHONE = "_organization.address.cellPhone",
                                 ORGANIZATION_ADDRESS_FAX_PHONE = "_organization.address.faxPhone",
                                 ORGANIZATION_ADDRESS_EMAIL = "_organization.address.email",
                                 ORGANIZATION_ADDRESS_COUNTRY = "_organization.address.country",                                                               
                                                           
                                 INVENTORY_LOCATION_STORAGE_LOCATION_NAME = "_storageLocation.name",   
                                 INVENTORY_LOCATION_STORAGE_LOCATION_LOCATION = "_storageLocation.location",
                                 INVENTORY_LOCATION_STORAGE_LOCATION_STORAGE_UNIT_DESCRIPTION = "_storageUnit.description", 
                                 ORDER_ITEM_ORDER_EXTERNAL_ORDER_NUMBER = "_orderItem.order.externalOrderNumber",
                                 ORDER_ITEM_ORDER_STATUS_ID = "_orderItem.order.statusId",
                                 ORDER_ITEM_ORDER_TYPE = "_orderItem.order.type",
                                 ORGANIZATION_NAME = "_organization.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, INVENTORY_ITEM_ID, INVENTORY_ITEM_NAME, INVENTORY_ITEM_DESCRIPTION,
                                                  INVENTORY_ITEM_CATEGORY_ID, INVENTORY_ITEM_STORE_ID,
                                                  INVENTORY_ITEM_QUANTITY_MIN_LEVEL, INVENTORY_ITEM_QUANTITY_MAX_LEVEL,
                                                  INVENTORY_ITEM_QUANTITY_TO_REORDER, INVENTORY_ITEM_DISPENSED_UNITS_ID,
                                                  INVENTORY_ITEM_IS_REORDER_AUTO, INVENTORY_ITEM_IS_LOT_MAINTAINED,
                                                  INVENTORY_ITEM_IS_SERIAL_MAINTAINED, INVENTORY_ITEM_IS_ACTIVE,
                                                  INVENTORY_ITEM_IS_BULK, INVENTORY_ITEM_IS_NOT_FOR_SALE,
                                                  INVENTORY_ITEM_IS_SUB_ASSEMBLY, INVENTORY_ITEM_IS_LABOR,
                                                  INVENTORY_ITEM_IS_NOT_INVENTORIED, INVENTORY_ITEM_PRODUCT_URI,
                                                  INVENTORY_ITEM_AVERAGE_LEAD_TIME, INVENTORY_ITEM_AVERAGE_COST,
                                                  INVENTORY_ITEM_AVERAGE_DAILY_USE, INVENTORY_ITEM_PARENT_INVENTORY_ITEM_ID,
                                                  INVENTORY_ITEM_PARENT_RATIO, INVENTORY_X_PUT_ID,
                                                  INVENTORY_X_PUT_INVENTORY_RECEIPT_ID, INVENTORY_X_PUT_INVENTORY_LOCATION_ID,
                                                  INVENTORY_X_PUT_QUANTITY, INVENTORY_LOCATION_LOT_NUMBER,
                                                  INVENTORY_LOCATION_STORAGE_LOCATION_ID, INVENTORY_LOCATION_QUANTITY_ON_HAND,
                                                  INVENTORY_LOCATION_EXPIRATION_DATE, ORDER_ITEM_ID, ORGANIZATION_ID,
                                                  RECEIVED_DATE, QUANTITY_RECEIVED, UNIT_COST, QC_REFERENCE, EXTERNAL_REFERENCE,
                                                  UPC, ORDER_ITEM_ORDER_ID, ORDER_ITEM_INVENTORY_ITEM_ID, ORDER_ITEM_QUANTITY,
                                                  ORDER_ITEM_CATALOG_NUMBER, ORDER_ITEM_UNIT_COST,
                                                  ORGANIZATION_ADDRESS_ID, ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  ORGANIZATION_ADDRESS_STREET_ADDRESS, ORGANIZATION_ADDRESS_CITY,
                                                  ORGANIZATION_ADDRESS_STATE, ORGANIZATION_ADDRESS_ZIP_CODE,
                                                  ORGANIZATION_ADDRESS_WORK_PHONE, ORGANIZATION_ADDRESS_HOME_PHONE,
                                                  ORGANIZATION_ADDRESS_CELL_PHONE, ORGANIZATION_ADDRESS_FAX_PHONE,
                                                  ORGANIZATION_ADDRESS_EMAIL, ORGANIZATION_ADDRESS_COUNTRY,
                                                  INVENTORY_LOCATION_STORAGE_LOCATION_NAME,   
                                                  INVENTORY_LOCATION_STORAGE_LOCATION_LOCATION,
                                                  INVENTORY_LOCATION_STORAGE_LOCATION_STORAGE_UNIT_DESCRIPTION,
                                                  ORDER_ITEM_ORDER_EXTERNAL_ORDER_NUMBER, ORDER_ITEM_ORDER_STATUS_ID,
                                                  ORDER_ITEM_ORDER_TYPE, ORGANIZATION_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getInventoryItemId() {
        return INVENTORY_ITEM_ID;
    }

    public static String getOrderItemId() {
        return ORDER_ITEM_ID;
    }

    public static String getOrganizationId() {
        return ORGANIZATION_ID;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getQuantityReceived() {
        return QUANTITY_RECEIVED;
    }

    public static String getUnitCost() {
        return UNIT_COST;
    }

    public static String getQcReference() {
        return QC_REFERENCE;
    }

    public static String getExternalReference() {
        return EXTERNAL_REFERENCE;
    }

    public static String getUpc() {
        return UPC;
    }
    
    public static String getOrganizationAddressId() {
        return ORGANIZATION_ADDRESS_ID;
    }
    
    public static String getOrganizationAddressMultipleUnit() {
        return ORGANIZATION_ADDRESS_MULTIPLE_UNIT;
    }

    public static String getOrganizationAddressStreetAddress() {
        return ORGANIZATION_ADDRESS_STREET_ADDRESS;
    }

    public static String getOrganizationAddressCity() {
        return ORGANIZATION_ADDRESS_CITY;
    }

    public static String getOrganizationAddressState() {
        return ORGANIZATION_ADDRESS_STATE;
    }

    public static String getOrganizationAddressZipCode() {
        return ORGANIZATION_ADDRESS_ZIP_CODE;
    }
    
    public static String getOrganizationAddressWorkPhone() {
        return ORGANIZATION_ADDRESS_WORK_PHONE;
    }

    public static String getOrganizationAddressHomePhone() {
        return ORGANIZATION_ADDRESS_HOME_PHONE;
    }

    public static String getOrganizationAddressCellPhone() {
        return ORGANIZATION_ADDRESS_CELL_PHONE;
    }

    public static String getOrganizationAddressFaxPhone() {
        return ORGANIZATION_ADDRESS_FAX_PHONE;
    }

    public static String getOrganizationAddressEmail() {
        return ORGANIZATION_ADDRESS_EMAIL;
    }

    public static String getOrganizationAddressCountry() {
        return ORGANIZATION_ADDRESS_COUNTRY;
    }   
    
    public static String getOrderItemOrderId() {
        return ORDER_ITEM_ORDER_ID;
    }
    
    public static String getOrderItemInventoryItemId() {
        return ORDER_ITEM_INVENTORY_ITEM_ID;
    }
    
    public static String getOrderItemQuantity() {
        return ORDER_ITEM_QUANTITY;
    }
    
    public static String getOrderItemCatalogNumber() {
        return ORDER_ITEM_CATALOG_NUMBER;
    }
    
    public static String getOrderItemUnitCost() {
        return ORDER_ITEM_UNIT_COST;
    }
    
    public static String getInventoryItemName() {
        return INVENTORY_ITEM_NAME;
    }
    
    public static String getInventoryItemDescription() {
        return INVENTORY_ITEM_DESCRIPTION;
    }
    
    public static String getInventoryItemCategoryId() {
        return INVENTORY_ITEM_CATEGORY_ID;
    }
    
    public static String getInventoryItemStoreId() {
        return INVENTORY_ITEM_STORE_ID;
    }
    
    public static String getInventoryItemQuantityMinLevel() {
       return INVENTORY_ITEM_QUANTITY_MIN_LEVEL;
    }
    
    public static String getInventoryItemQuantityMaxLevel() {
       return INVENTORY_ITEM_QUANTITY_MAX_LEVEL;
    }

    public static String getInventoryItemQuantityToReorder() {
       return INVENTORY_ITEM_QUANTITY_TO_REORDER;
    }
    
    public static String getInventoryItemDispensedUnitsId() {
       return INVENTORY_ITEM_DISPENSED_UNITS_ID;
    }
    
    public static String getInventoryItemIsReorderAuto() {
        return INVENTORY_ITEM_IS_REORDER_AUTO;
    }

    public static String getInventoryItemIsLotMaintained() {
        return INVENTORY_ITEM_IS_LOT_MAINTAINED;
    }

    public static String getInventoryItemIsSerialMaintained() {
        return INVENTORY_ITEM_IS_SERIAL_MAINTAINED;
    }

    public static String getInventoryItemIsActive() {
        return INVENTORY_ITEM_IS_ACTIVE;
    }

    public static String getInventoryItemIsBulk() {
        return INVENTORY_ITEM_IS_BULK;
    }

    public static String getInventoryItemIsNotForSale() {
        return INVENTORY_ITEM_IS_NOT_FOR_SALE;
    }

    public static String getInventoryItemIsSubAssembly() {
        return INVENTORY_ITEM_IS_SUB_ASSEMBLY;
    }

    public static String getInventoryItemIsLabor() {
        return INVENTORY_ITEM_IS_LABOR;
    }

    public static String getInventoryItemIsNotInventoried() {
        return INVENTORY_ITEM_IS_NOT_INVENTORIED;
    }

    public static String getInventoryItemProductUri() {
        return INVENTORY_ITEM_PRODUCT_URI;
    }

    public static String getInventoryItemAverageLeadTime() {
        return INVENTORY_ITEM_AVERAGE_LEAD_TIME;
    }

    public static String getInventoryItemAverageCost() {
        return INVENTORY_ITEM_AVERAGE_COST;
    }

    public static String getInventoryItemAverageDailyUse() {
        return INVENTORY_ITEM_AVERAGE_DAILY_USE;
    }

    public static String getInventoryItemParentInventoryItemId() {
        return INVENTORY_ITEM_PARENT_INVENTORY_ITEM_ID;
    }

    public static String getInventoryItemParentRatio() {
        return INVENTORY_ITEM_PARENT_RATIO;
    }
    
    public static String getInventoryXPutId() {
        return INVENTORY_X_PUT_ID;
    }
    
    public static String getInventoryXPutInventoryReceiptId() {
        return INVENTORY_X_PUT_INVENTORY_RECEIPT_ID;
    }
    
    public static String getInventoryXPutInventoryLocationId() {
        return INVENTORY_X_PUT_INVENTORY_LOCATION_ID;
    }
    
    public static String getInventoryXPutQuantity() {
        return INVENTORY_X_PUT_QUANTITY;
    }
    
    public static String getInventoryLocationLotNumber() {
        return INVENTORY_LOCATION_LOT_NUMBER;
    }   
    
    public static String getInventoryLocationStorageLocationId() {
        return INVENTORY_LOCATION_STORAGE_LOCATION_ID;
    }
    
    public static String getInventoryLocationStorageLocationName() {
        return INVENTORY_LOCATION_STORAGE_LOCATION_NAME;
    }
    
    public static String getInventoryLocationStorageLocationLocation() {
        return INVENTORY_LOCATION_STORAGE_LOCATION_LOCATION;
    }
     
    public static String getInventoryLocationStorageLocationStorageUnitDescription() {
        return INVENTORY_LOCATION_STORAGE_LOCATION_STORAGE_UNIT_DESCRIPTION;
    }
    
    public static String getInventoryLocationQuantityOnHand() {
        return INVENTORY_LOCATION_QUANTITY_ON_HAND;
    } 
    
    public static String getInventoryLocationExpirationDate() {
        return INVENTORY_LOCATION_EXPIRATION_DATE;
    }     
    
    public static String getOrderItemOrderExternalOrderNumber() {
        return ORDER_ITEM_ORDER_EXTERNAL_ORDER_NUMBER;
    }
    
    public static String getOrderItemOrderStatusId() {
        return ORDER_ITEM_ORDER_STATUS_ID;
    }
    
    public static String getOrderItemOrderType() {
        return ORDER_ITEM_ORDER_TYPE;
    }
    
    public static String getOrganizationName() {
        return ORGANIZATION_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }   
    
    public String buildFrom(String where) {
        String from;
                     
        from = null;
        
        if (where.indexOf("inventoryReceipt") > -1) {
            from = "InventoryReceipt _inventoryReceipt ";		
        } else if (where.indexOf("orderItem.") > -1) {
            from = "OrderItem _orderItem ";
            from += " left join _orderItem.inventoryReceipt _inventoryReceipt";                                
        }
                
        return from;
    }

}
