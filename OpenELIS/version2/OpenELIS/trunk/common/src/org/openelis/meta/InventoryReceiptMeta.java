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

    public static final String   ID = "_inventoryReceipt.id", 
                                 INVENTORY_ITEM_ID = "_inventoryReceipt.inventoryItemId",
                                 ORDER_ITEM_ID = "_inventoryReceipt.orderItemId", 
                                 ORGANIZATION_ID = "_inventoryReceipt.organizationId",
                                 RECEIVED_DATE = "_inventoryReceipt.receivedDate", 
                                 QUANTITY_RECEIVED = "_inventoryReceipt.quantityReceived",
                                 UNIT_COST = "_inventoryReceipt.unitCost", 
                                 QC_REFERENCE = "_inventoryReceipt.qcReference",
                                 EXTERNAL_REFERENCE = "_inventoryReceipt.externalReference",
                                 UPC = "_inventoryReceipt.upc",
                                 
                                 ITEM_ORDER_ID = "_orderItem.orderId",
                                 ITEM_INVENTORY_ITEM_ID = "_orderItem.inventoryItemId",
                                 ITEM_QUANTITY = "_orderItem.quantity",
                                 ITEM_ORDER_CATALOG_NUMBER = "_orderItem.catalogNumber",
                                 ITEM_UNIT_COST = "_orderItem.unitCost",  
                                 
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
                                 
                                 INVENTORY_ITEM_NAME = "_inventoryItem.name",
                                 ORGANIZATION_NAME = "_inventoryReceipt.organization.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, INVENTORY_ITEM_ID, ORDER_ITEM_ID,
                                                  ORGANIZATION_ID, RECEIVED_DATE,
                                                  QUANTITY_RECEIVED, UNIT_COST, QC_REFERENCE,
                                                  EXTERNAL_REFERENCE, UPC, ITEM_ORDER_ID,
                                                  ITEM_INVENTORY_ITEM_ID, ITEM_QUANTITY,
                                                  ITEM_ORDER_CATALOG_NUMBER, ITEM_UNIT_COST,
                                                  ORGANIZATION_ADDRESS_ID,
                                                  ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  ORGANIZATION_ADDRESS_STREET_ADDRESS,
                                                  ORGANIZATION_ADDRESS_CITY,
                                                  ORGANIZATION_ADDRESS_STATE ,
                                                  ORGANIZATION_ADDRESS_ZIP_CODE,
                                                  ORGANIZATION_ADDRESS_WORK_PHONE,
                                                  ORGANIZATION_ADDRESS_HOME_PHONE,
                                                  ORGANIZATION_ADDRESS_CELL_PHONE,
                                                  ORGANIZATION_ADDRESS_FAX_PHONE,
                                                  ORGANIZATION_ADDRESS_EMAIL,
                                                  ORGANIZATION_ADDRESS_COUNTRY,
                                                  ORGANIZATION_NAME));
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
        return ITEM_ORDER_ID;
    }
    
    public static String getOrderItemInventoryItemId() {
        return ITEM_INVENTORY_ITEM_ID;
    }
    
    public static String getOrderItemQuantity() {
        return ITEM_QUANTITY;
    }
    
    public static String getOrderItemCatalogNumber() {
        return ITEM_ORDER_CATALOG_NUMBER;
    }
    
    public static String getOrderItemUnitCost() {
        return ITEM_UNIT_COST;
    }
    
    public static String getInventoryItemName() {
        return INVENTORY_ITEM_NAME;
    }
    
    public static String getOrganizationName() {
        return ORGANIZATION_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        return null;
    }

}
