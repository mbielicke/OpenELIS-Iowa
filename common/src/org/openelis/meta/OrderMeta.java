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
  * Order META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class OrderMeta implements Meta, MetaMap {
	private static final String    ID = "_order.id",
	                               DESCRIPTION = "_order.description",
	                               STATUS_ID = "_order.statusId",
	                               ORDERED_DATE = "_order.orderedDate",
	                               NEEDED_IN_DAYS = "_order.neededInDays",
	                               REQUESTED_BY = "_order.requestedBy",
	                               COST_CENTER_ID = "_order.costCenterId",
	                               ORGANIZATION_ID = "_order.organizationId",
	                               IS_EXTERNAL = "_order.isExternal",
	                               EXTERNAL_ORDER_NUMBER = "_order.externalOrderNumber",
	                               REPORT_TO_ID = "_order.reportToId",
	                               BILL_TO_ID = "_order.billToId",
	                               SHIP_FROM_ID = "_order.shipFromId",
	                               
	                               ITEM_ID = "_orderItem.id",
	                               ITEM_ORDER_ID = "_orderItem.orderId",
	                               ITEM_INVENTORY_ITEM_ID = "_orderItem.inventoryItemId",
	                               ITEM_QUANTITY = "_orderItem.quantity",
	                               ITEM_CATALOG_NUMBER = "_orderItem.catalogNumber",
	                               ITEM_UNIT_COST = "_orderItem.unitCost",
	                               
	                               RCPT_ID = "_inventoryRecept.id",
	                               RCPT_INVENTORY_ITEM_ID = "_inventoryRecept.inventoryItemId",
	                               RCPT_ORDER_ITEM_ID = "_inventoryRecept.orderItemId",
	                               RCPT_ORGANIZATION_ID = "_inventoryRecept.organizationId",
	                               RCPT_RECEIVED_DATE = "_inventoryRecept.receivedDate",
	                               RCPT_QUANTITY_RECEIVED = "_inventoryRecept.quantityReceived",
	                               RCPT_UNIT_COST = "_inventoryRecept.unitCost",
	                               RCPT_QC_REFERENCE = "_inventoryRecept.qcReference",
	                               RCPT_EXTERNAL_REFERENCE = "_inventoryRecept.externalReference",
	                               RCPT_UPC = "_inventoryRecept.upc",
	                               
                                   ITEM_INVENTORY_ITEM_NAME = "_orderItem.inventoryItem.name",
	                               ITEM_INVENTORY_ITEM_STORE_ID = "_orderItem.inventoryItem.storeId";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, DESCRIPTION, STATUS_ID, ORDERED_DATE,
                                                  NEEDED_IN_DAYS, REQUESTED_BY, COST_CENTER_ID,
                                                  ORGANIZATION_ID, IS_EXTERNAL, EXTERNAL_ORDER_NUMBER,
                                                  REPORT_TO_ID, BILL_TO_ID, SHIP_FROM_ID,
                                                  
                                                  ITEM_ID, ITEM_ORDER_ID, ITEM_INVENTORY_ITEM_ID,
                                                  ITEM_QUANTITY, ITEM_CATALOG_NUMBER, ITEM_UNIT_COST,

                                                  RCPT_ID, RCPT_INVENTORY_ITEM_ID, RCPT_ORDER_ITEM_ID,
                                                  RCPT_ORGANIZATION_ID, RCPT_RECEIVED_DATE, RCPT_QUANTITY_RECEIVED,
                                                  RCPT_UNIT_COST, RCPT_QC_REFERENCE, RCPT_EXTERNAL_REFERENCE,
                                                  RCPT_UPC,
                                                  
                                                  ITEM_INVENTORY_ITEM_NAME, ITEM_INVENTORY_ITEM_STORE_ID));
    }

    public static String getId() {
        return ID;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getOrderedDate() {
        return ORDERED_DATE;
    }

    public static String getNeededInDays() {
        return NEEDED_IN_DAYS;
    }

    public static String getRequestedBy() {
        return REQUESTED_BY;
    }

    public static String getCostCenterId() {
        return COST_CENTER_ID;
    }

    public static String getOrganizationId() {
        return ORGANIZATION_ID;
    }

    public static String getIsExternal() {
        return IS_EXTERNAL;
    }

    public static String getExternalOrderNumber() {
        return EXTERNAL_ORDER_NUMBER;
    }

    public static String getReportToId() {
        return REPORT_TO_ID;
    }

    public static String getBillToId() {
        return BILL_TO_ID;
    }

    public static String getShipFromId() {
        return SHIP_FROM_ID;
    }

    public static String getOrderItemId() {
        return ITEM_ID;
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
        return ITEM_CATALOG_NUMBER;
    }

    public static String getOrderItemUnitCost() {
        return ITEM_UNIT_COST;
    }

    public static String getInventoryReceiptId() {
        return RCPT_ID;
    }

    public static String getInventoryReceiptInventoryItemId() {
        return RCPT_INVENTORY_ITEM_ID;
    }

    public static String getInventoryReceiptorderItemId() {
        return RCPT_ORDER_ITEM_ID;
    }

    public static String getInventoryReceiptOrganizationId() {
        return RCPT_ORGANIZATION_ID;
    }

    public static String getInventoryReceiptReceivedDate() {
        return RCPT_RECEIVED_DATE;
    }

    public static String getInventoryReceiptQuantityReceived() {
        return RCPT_QUANTITY_RECEIVED;
    }

    public static String getInventoryReceiptUnitCost() {
        return RCPT_UNIT_COST;
    }

    public static String getInventoryReceiptQcReference() {
        return RCPT_QC_REFERENCE;
    }

    public static String getInventoryReceiptExternalReference() {
        return RCPT_EXTERNAL_REFERENCE;
    }

    public static String getInventoryReceiptUpc() {
        return RCPT_UPC;
    }

    public static String getOrderItemInventoryItemName() {
        return ITEM_INVENTORY_ITEM_NAME;
    }

    public static String getOrderItemInventoryItemStoreId() {
        return ITEM_INVENTORY_ITEM_STORE_ID;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Order _order ";
        if (where.indexOf("orderItem.") > -1)
            from += ",IN (_order.orderItem) _orderItem ";

        return from;
    }
}