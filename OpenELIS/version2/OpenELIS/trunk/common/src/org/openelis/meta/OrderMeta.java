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
	                               TYPE = "_order.type",
	                               EXTERNAL_ORDER_NUMBER = "_order.externalOrderNumber",
	                               REPORT_TO_ID = "_order.reportToId",
	                               BILL_TO_ID = "_order.billToId",
	                               SHIP_FROM_ID = "_order.shipFromId",
	                               
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
                                   
	                               ITEM_ID = "_orderItem.id",
	                               ITEM_ORDER_ID = "_orderItem.orderId",
	                               ITEM_INVENTORY_ITEM_ID = "_orderItem.inventoryItemId",
	                               ITEM_QUANTITY = "_orderItem.quantity",
	                               ITEM_CATALOG_NUMBER = "_orderItem.catalogNumber",
	                               ITEM_UNIT_COST = "_orderItem.unitCost",
	                               
	                               RCPT_ID = "_inventoryReceipt.id",
	                               RCPT_INVENTORY_ITEM_ID = "_inventoryReceipt.inventoryItemId",
	                               RCPT_ORDER_ITEM_ID = "_inventoryReceipt.orderItemId",
	                               RCPT_ORGANIZATION_ID = "_inventoryReceipt.organizationId",
	                               RCPT_RECEIVED_DATE = "_inventoryReceipt.receivedDate",
	                               RCPT_QUANTITY_RECEIVED = "_inventoryReceipt.quantityReceived",
	                               RCPT_UNIT_COST = "_inventoryReceipt.unitCost",
	                               RCPT_QC_REFERENCE = "_inventoryReceipt.qcReference",
	                               RCPT_EXTERNAL_REFERENCE = "_inventoryReceipt.externalReference",
	                               RCPT_UPC = "_inventoryReceipt.upc",	                              
	                               
	                               REPORT_TO_ADDRESS_ID = "_reportTo.address.id",
	                               REPORT_TO_ADDRESS_MULTIPLE_UNIT = "_reportTo.address.multipleUnit",
	                               REPORT_TO_ADDRESS_STREET_ADDRESS = "_reportTo.address.streetAddress",
	                               REPORT_TO_ADDRESS_CITY = "_reportTo.address.city",
	                               REPORT_TO_ADDRESS_STATE = "_reportTo.address.state",
	                               REPORT_TO_ADDRESS_ZIP_CODE = "_reportTo.address.zipCode",
	                               REPORT_TO_ADDRESS_WORK_PHONE = "_reportTo.address.workPhone",
	                               REPORT_TO_ADDRESS_HOME_PHONE = "_reportTo.address.homePhone",
	                               REPORT_TO_ADDRESS_CELL_PHONE = "_reportTo.address.cellPhone",
	                               REPORT_TO_ADDRESS_FAX_PHONE = "_reportTo.address.faxPhone",
	                               REPORT_TO_ADDRESS_EMAIL = "_reportTo.address.email",
	                               REPORT_TO_ADDRESS_COUNTRY = "_reportTo.address.country",
	                               
	                               BILL_TO_ADDRESS_ID = "_billTo.address.id",
	                               BILL_TO_ADDRESS_MULTIPLE_UNIT = "_billTo.address.multipleUnit",
	                               BILL_TO_ADDRESS_STREET_ADDRESS = "_billTo.address.streetAddress",
	                               BILL_TO_ADDRESS_CITY = "_billTo.address.city",
	                               BILL_TO_ADDRESS_STATE = "_billTo.address.state",
	                               BILL_TO_ADDRESS_ZIP_CODE = "_billTo.address.zipCode",
	                               BILL_TO_ADDRESS_WORK_PHONE = "_billTo.address.workPhone",
	                               BILL_TO_ADDRESS_HOME_PHONE = "_billTo.address.homePhone",
	                               BILL_TO_ADDRESS_CELL_PHONE = "_billTo.address.cellPhone",
	                               BILL_TO_ADDRESS_FAX_PHONE = "_billTo.address.faxPhone",
	                               BILL_TO_ADDRESS_EMAIL = "_billTo.address.email",
	                               BILL_TO_ADDRESS_COUNTRY = "_billTo.address.country",
	                               
	                               CONT_ID = "_orderContainer.id",
	                               CONT_ORDER_ID = "_orderContainer.orderId",
	                               CONT_CONTAINER_ID = "_orderContainer.containerId",
	                               CONT_NUMBER_OF_CONTAINERS = "_orderContainer.numberOfContainers",
	                               CONT_TYPE_OF_SAMPLE_ID = "_orderContainer.typeOfSampleId",
	                               
	                               TEST_ID = "_orderTest.id",
	                               TEST_ORDER_ID = "_orderTest.orderId",
	                               TEST_SORT_ORDER = "_orderTest.sortOrder",
	                               TEST_REFERENCE_ID = "_orderTest.referenceId",
	                               TEST_REFERENCE_TABLE_ID = "_orderTest.referenceTableId",
	                               
	                               AUX_DATA_ID = "_auxData.id",
	                               AUX_DATA_AUX_FIELD_ID = "_auxData.auxFieldId",
	                               AUX_DATA_REFERENCE_ID = "_auxData.referenceId",
	                               AUX_DATA_REFERENCE_TABLE_ID = "_auxData.referenceTableId",
	                               AUX_DATA_IS_REPORTABLE = "_auxData.isReportable",
	                               AUX_DATA_TYPE_ID = "_auxData.typeId",
	                               AUX_DATA_VALUE = "_auxData.value",
	                               	                               
	                               ORGANIZATION_NAME = "_order.organization.name",
	                               REPORT_TO_NAME = "_reportTo.name",
                                   BILL_TO_NAME = "_billTo.name",
                                   ITEM_INVENTORY_ITEM_NAME = "_orderItem.inventoryItem.name",
	                               ITEM_INVENTORY_ITEM_STORE_ID = "_orderItem.inventoryItem.storeId";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, DESCRIPTION, STATUS_ID, ORDERED_DATE,
                                                  NEEDED_IN_DAYS, REQUESTED_BY, COST_CENTER_ID,
                                                  ORGANIZATION_ID, TYPE, EXTERNAL_ORDER_NUMBER,
                                                  REPORT_TO_ID, BILL_TO_ID, SHIP_FROM_ID,
                                                  
                                                  ORGANIZATION_ADDRESS_ID, ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  ORGANIZATION_ADDRESS_STREET_ADDRESS, ORGANIZATION_ADDRESS_CITY,
                                                  ORGANIZATION_ADDRESS_STATE, ORGANIZATION_ADDRESS_ZIP_CODE,
                                                  ORGANIZATION_ADDRESS_WORK_PHONE, ORGANIZATION_ADDRESS_HOME_PHONE,
                                                  ORGANIZATION_ADDRESS_CELL_PHONE, ORGANIZATION_ADDRESS_FAX_PHONE,
                                                  ORGANIZATION_ADDRESS_EMAIL, ORGANIZATION_ADDRESS_COUNTRY,
                                                  
                                                  ITEM_ID, ITEM_ORDER_ID, ITEM_INVENTORY_ITEM_ID,
                                                  ITEM_QUANTITY, ITEM_CATALOG_NUMBER, ITEM_UNIT_COST,

                                                  RCPT_ID, RCPT_INVENTORY_ITEM_ID, RCPT_ORDER_ITEM_ID,
                                                  RCPT_ORGANIZATION_ID, RCPT_RECEIVED_DATE, RCPT_QUANTITY_RECEIVED,
                                                  RCPT_UNIT_COST, RCPT_QC_REFERENCE, RCPT_EXTERNAL_REFERENCE,
                                                  RCPT_UPC,
                                                  
                                                  REPORT_TO_ADDRESS_ID, REPORT_TO_ADDRESS_MULTIPLE_UNIT,
                                                  REPORT_TO_ADDRESS_STREET_ADDRESS, REPORT_TO_ADDRESS_CITY,
                                                  REPORT_TO_ADDRESS_STATE, REPORT_TO_ADDRESS_ZIP_CODE,
                                                  REPORT_TO_ADDRESS_WORK_PHONE, REPORT_TO_ADDRESS_HOME_PHONE,
                                                  REPORT_TO_ADDRESS_CELL_PHONE, REPORT_TO_ADDRESS_FAX_PHONE,
                                                  REPORT_TO_ADDRESS_EMAIL, REPORT_TO_ADDRESS_COUNTRY,
                                                  
                                                  BILL_TO_ADDRESS_ID, BILL_TO_ADDRESS_MULTIPLE_UNIT,
                                                  BILL_TO_ADDRESS_STREET_ADDRESS, BILL_TO_ADDRESS_CITY,
                                                  BILL_TO_ADDRESS_STATE, BILL_TO_ADDRESS_ZIP_CODE,
                                                  BILL_TO_ADDRESS_WORK_PHONE, BILL_TO_ADDRESS_HOME_PHONE,
                                                  BILL_TO_ADDRESS_CELL_PHONE, BILL_TO_ADDRESS_FAX_PHONE,
                                                  BILL_TO_ADDRESS_EMAIL, BILL_TO_ADDRESS_COUNTRY,
                                                  
                                                  CONT_ID, CONT_ORDER_ID, CONT_CONTAINER_ID,
                                                  CONT_NUMBER_OF_CONTAINERS, CONT_TYPE_OF_SAMPLE_ID,
                                                  
                                                  TEST_ID, TEST_ORDER_ID, TEST_SORT_ORDER, TEST_REFERENCE_ID,
                                                  TEST_REFERENCE_TABLE_ID,
                                                  
                                                  AUX_DATA_ID, AUX_DATA_AUX_FIELD_ID, AUX_DATA_REFERENCE_ID,
                                                  AUX_DATA_REFERENCE_TABLE_ID, AUX_DATA_IS_REPORTABLE,
                                                  AUX_DATA_TYPE_ID, AUX_DATA_VALUE,
                                                  
                                                  ORGANIZATION_NAME, REPORT_TO_NAME, BILL_TO_NAME,
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

    public static String getType() {
        return TYPE;
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

    public static String getInventoryReceiptOrderItemId() {
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
    
    public static String getReportToAddressId() {
        return REPORT_TO_ADDRESS_ID;
    }
    
    public static String getReportToAddressMultipleUnit() {
        return REPORT_TO_ADDRESS_MULTIPLE_UNIT;
    }

    public static String getReportToAddressStreetAddress() {
        return REPORT_TO_ADDRESS_STREET_ADDRESS;
    }

    public static String getReportToAddressCity() {
        return REPORT_TO_ADDRESS_CITY;
    }

    public static String getReportToAddressState() {
        return REPORT_TO_ADDRESS_STATE;
    }

    public static String getReportToAddressZipCode() {
        return REPORT_TO_ADDRESS_ZIP_CODE;
    }
    
    public static String getReportToAddressWorkPhone() {
        return REPORT_TO_ADDRESS_WORK_PHONE;
    }

    public static String getReportToAddressHomePhone() {
        return REPORT_TO_ADDRESS_HOME_PHONE;
    }

    public static String getReportToAddressCellPhone() {
        return REPORT_TO_ADDRESS_CELL_PHONE;
    }

    public static String getReportToAddressFaxPhone() {
        return REPORT_TO_ADDRESS_FAX_PHONE;
    }

    public static String getReportToAddressEmail() {
        return REPORT_TO_ADDRESS_EMAIL;
    }

    public static String getReportToAddressCountry() {
        return REPORT_TO_ADDRESS_COUNTRY;
    }
    
    public static String getBillToAddressId() {
        return BILL_TO_ADDRESS_ID;
    }

    public static String getBillToAddressMultipleUnit() {
        return BILL_TO_ADDRESS_MULTIPLE_UNIT;
    }

    public static String getBillToAddressStreetAddress() {
        return BILL_TO_ADDRESS_STREET_ADDRESS;
    }

    public static String getBillToAddressCity() {
        return BILL_TO_ADDRESS_CITY;
    }

    public static String getBillToAddressState() {
        return BILL_TO_ADDRESS_STATE;
    }

    public static String getBillToAddressZipCode() {
        return BILL_TO_ADDRESS_ZIP_CODE;
    }
    
    public static String getBillToAddressWorkPhone() {
        return BILL_TO_ADDRESS_WORK_PHONE;
    }

    public static String getBillToAddressHomePhone() {
        return BILL_TO_ADDRESS_HOME_PHONE;
    }

    public static String getBillToAddressCellPhone() {
        return BILL_TO_ADDRESS_CELL_PHONE;
    }

    public static String getBillToAddressFaxPhone() {
        return BILL_TO_ADDRESS_FAX_PHONE;
    }

    public static String getBillToAddressEmail() {
        return BILL_TO_ADDRESS_EMAIL;
    }

    public static String getBillToAddressCountry() {
        return BILL_TO_ADDRESS_COUNTRY;
    }
    
    public static String getContainerId() {
        return CONT_ID;
    }
    
    public static String getContainerOrderId() {
        return CONT_ORDER_ID;
    }
    
    public static String getContainerContainerId() {
        return CONT_CONTAINER_ID;
    }
    
    public static String getContainerNumberOfContainers() {
        return CONT_NUMBER_OF_CONTAINERS;
    }
    
    public static String getContainerTypeOfSampleId() {
        return CONT_TYPE_OF_SAMPLE_ID;
    }
    
    public static String getTestId() {
        return TEST_ID;
    }
    
    public static String getTestOrderId() {
        return TEST_ORDER_ID;
    }
    
    public static String getTestSortOrder() {
        return TEST_SORT_ORDER;
    }
    
    public static String getTestReferenceId() {
        return TEST_REFERENCE_ID;
    }
    
    public static String getTestReferenceTableId() {
        return TEST_REFERENCE_TABLE_ID;
    }
    
    public static String getAuxDataId(){
        return AUX_DATA_ID;
    }
    
    public static String getAuxDataAuxFieldId(){
        return AUX_DATA_AUX_FIELD_ID;
    }
    
    public static String getAuxDataReferenceId(){
        return AUX_DATA_REFERENCE_ID;
    }
    
    public static String getAuxDataReferenceTableId(){
        return AUX_DATA_REFERENCE_TABLE_ID;
    }
    
    public static String getAuxDataIsReportable(){
        return AUX_DATA_IS_REPORTABLE;
    }
    
    public static String getAuxDataTypeId(){
        return AUX_DATA_TYPE_ID;
    }
    
    public static String getAuxDataValue(){
        return AUX_DATA_VALUE;
    }
    
    public static String getOrganizationName() {
        return ORGANIZATION_NAME;
    }
    
    public static String getReportToName() {
        return REPORT_TO_NAME;
    }
    
    public static String getBillToName() {
        return BILL_TO_NAME;
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
        String from, oiFrom, irFrom;
        
        oiFrom = ",IN (_order.orderItem) _orderItem ";
        irFrom = ",IN (_orderItem.inventoryReceipt) _inventoryReceipt ";
        
        from = "Order _order ";
        if (where.indexOf("orderItem.") > -1)
            from += oiFrom;
        if (where.indexOf("organization.") > -1)
            from += ",IN (_order.organization) _organization "; 
        if (where.indexOf("inventoryReceipt.") > -1)
            if (from.indexOf(oiFrom) < 0)
                from += oiFrom + irFrom;
            else
                from += irFrom;
        if (where.indexOf("reportTo.") > -1)
            from += ",IN (_order.reportTo) _reportTo ";
        if (where.indexOf("billTo.") > -1)
            from += ",IN (_order.billTo) _billTo ";
        if (where.indexOf("orderContainer.") > -1)
            from += ",IN (_order.orderContainer) _orderContainer ";
        if (where.indexOf("orderTest.") > -1)
            from += ",IN (_order.orderTest) _orderTest ";        
        if(where.indexOf("auxData.") > -1)
            from += ", IN (_order.auxData) _auxData ";

        return from;
    }
}