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
	                               PARENT_ORDER_ID = "_order.parentOrderId", 
	                               DESCRIPTION = "_order.description",
	                               STATUS_ID = "_order.statusId",
	                               ORDERED_DATE = "_order.orderedDate",
	                               NEEDED_IN_DAYS = "_order.neededInDays",
	                               REQUESTED_BY = "_order.requestedBy",
	                               COST_CENTER_ID = "_order.costCenterId",
	                               ORGANIZATION_ID = "_order.organizationId",
	                               ORGANIZATION_ATTENTION = "_order.organizationAttention",
	                               TYPE = "_order.type",
	                               EXTERNAL_ORDER_NUMBER = "_order.externalOrderNumber",
	                               REPORT_TO_ID = "_order.reportToId",
	                               REPORT_TO_ATTENTION = "_order.reportToAttention",
	                               BILL_TO_ID = "_order.billToId",
	                               BILL_TO_ATTENTION = "_order.billToAttention",
	                               SHIP_FROM_ID = "_order.shipFromId",
	                               NUMBER_OF_FORMS = "_order.numberOfForms",
	                               
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
	                               
	                               ORDER_ORG_ID = "_orderOrganization.id",
	                               ORDER_ORG_ORDER_ID = "_orderOrganization.orderId",
	                               ORDER_ORG_ORGANIZATION_ID = "_orderOrganization.organizationId",
	                               ORDER_ORG_ATTENTION = "_orderOrganization.attention",
	                               ORDER_ORG_TYPE_ID = "_orderOrganization.typeId",
	                               
	                               ORDER_ORG_ORGANIZATION_ADDRESS_ID = "_orderOrganization.organization.addressId",	   
	                               
	                               ORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT = "_orderOrganizationOrganization.address.multipleUnit",
	                               ORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS = "_orderOrganizationOrganization.address.streetAddress", 
	                               ORDER_ORG_ORGANIZATION_ADDRESS_CITY = "_orderOrganizationOrganization.address.city",
	                               ORDER_ORG_ORGANIZATION_ADDRESS_STATE = "_orderOrganizationOrganization.address.state", 
	                               ORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE = "_orderOrganizationOrganization.address.zipCode",
	                               ORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE = "_orderOrganizationOrganization.address.workPhone", 
	                               ORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE = "_orderOrganizationOrganization.address.homePhone",
	                               ORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE = "_orderOrganizationOrganization.address.cellPhone", 
	                               ORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE = "_orderOrganizationOrganization.address.faxPhone",
	                               ORDER_ORG_ORGANIZATION_ADDRESS_EMAIL = "_orderOrganizationOrganization.address.email", 
	                               ORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY = "_orderOrganizationOrganization.address.country",
	                               
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
	                               
	                               CONT_ID = "_orderContainer.id",
	                               CONT_ORDER_ID = "_orderContainer.orderId",
	                               CONT_CONTAINER_ID = "_orderContainer.containerId",
	                               CONT_ITEM_SEQUENCE = "_orderContainer.itemSequence",
	                               CONT_TYPE_OF_SAMPLE_ID = "_orderContainer.typeOfSampleId",
	                               
	                               TEST_ID = "_orderTest.id",
	                               TEST_ORDER_ID = "_orderTest.orderId",
	                               TEST_ITEM_SEQUENCE = "_orderTest.itemSequence",
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
	                               
                                   RECUR_ID = "_orderRecurrence.id",
                                   RECUR_ORDER_ID = "_orderRecurrence.orderId",
                                   RECUR_IS_ACTIVE = "_orderRecurrence.isActive",
                                   RECUR_ACTIVE_BEGIN = "_orderRecurrence.activeBegin",
                                   RECUR_ACTIVE_END = "_orderRecurrence.activeEnd",
                                   RECUR_FREQUENCY = "_orderRecurrence.frequency",
                                   RECUR_UNIT_ID = "_orderRecurrence.unitId",
	                               	                               
	                               ORGANIZATION_NAME = "_order.organization.name",
	                               ORDER_ORG_ORGANIZATION_NAME = "_orderOrganization.organization.name", 
	                               REPORT_TO_NAME = "_reportTo.name",
                                   BILL_TO_NAME = "_billTo.name",
                                   ITEM_INVENTORY_ITEM_NAME = "_orderItem.inventoryItem.name",
	                               ITEM_INVENTORY_ITEM_STORE_ID = "_orderItem.inventoryItem.storeId",
	                               TEST_NAME = "_test.name",
	                               TEST_METHOD_NAME = "_test.method.name";
	                                                

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, PARENT_ORDER_ID, DESCRIPTION, STATUS_ID, ORDERED_DATE,
                                                  NEEDED_IN_DAYS, REQUESTED_BY, COST_CENTER_ID,
                                                  ORGANIZATION_ID,ORGANIZATION_ATTENTION, TYPE,
                                                  EXTERNAL_ORDER_NUMBER, REPORT_TO_ID, REPORT_TO_ATTENTION,
                                                  BILL_TO_ID, BILL_TO_ATTENTION, SHIP_FROM_ID, NUMBER_OF_FORMS,                                                 
                                                  
                                                  ORGANIZATION_ADDRESS_ID, ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  ORGANIZATION_ADDRESS_STREET_ADDRESS, ORGANIZATION_ADDRESS_CITY,
                                                  ORGANIZATION_ADDRESS_STATE, ORGANIZATION_ADDRESS_ZIP_CODE,
                                                  ORGANIZATION_ADDRESS_WORK_PHONE, ORGANIZATION_ADDRESS_HOME_PHONE,
                                                  ORGANIZATION_ADDRESS_CELL_PHONE, ORGANIZATION_ADDRESS_FAX_PHONE,
                                                  ORGANIZATION_ADDRESS_EMAIL, ORGANIZATION_ADDRESS_COUNTRY,
                                                  
                                                  ORDER_ORG_ID, ORDER_ORG_ORDER_ID, ORDER_ORG_ORGANIZATION_ID,
                                                  ORDER_ORG_TYPE_ID, ORDER_ORG_ATTENTION,
                                                  
                                                  ORDER_ORG_ORGANIZATION_NAME,  ORDER_ORG_ORGANIZATION_ADDRESS_ID,                                                       
                                                  
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS, 
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_CITY, ORDER_ORG_ORGANIZATION_ADDRESS_STATE, 
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE, ORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE, 
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE, ORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE, 
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE, ORDER_ORG_ORGANIZATION_ADDRESS_EMAIL, 
                                                  ORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY,
                                                  
                                                  ITEM_ID, ITEM_ORDER_ID, ITEM_INVENTORY_ITEM_ID,
                                                  ITEM_QUANTITY, ITEM_CATALOG_NUMBER, ITEM_UNIT_COST,

                                                  RCPT_ID, RCPT_INVENTORY_ITEM_ID, RCPT_ORDER_ITEM_ID,
                                                  RCPT_ORGANIZATION_ID, RCPT_RECEIVED_DATE, RCPT_QUANTITY_RECEIVED,
                                                  RCPT_UNIT_COST, RCPT_QC_REFERENCE, RCPT_EXTERNAL_REFERENCE,
                                                  RCPT_UPC,                                                
                                                  
                                                  CONT_ID, CONT_ORDER_ID, CONT_CONTAINER_ID,
                                                  CONT_ITEM_SEQUENCE, CONT_TYPE_OF_SAMPLE_ID,
                                                  
                                                  TEST_ID, TEST_ORDER_ID, TEST_ITEM_SEQUENCE, TEST_SORT_ORDER,
                                                  TEST_REFERENCE_ID, TEST_REFERENCE_TABLE_ID,
                                                  
                                                  AUX_DATA_ID, AUX_DATA_AUX_FIELD_ID, AUX_DATA_REFERENCE_ID,
                                                  AUX_DATA_REFERENCE_TABLE_ID, AUX_DATA_IS_REPORTABLE,
                                                  AUX_DATA_TYPE_ID, AUX_DATA_VALUE,
                                                  
                                                  RECUR_ID, RECUR_ORDER_ID, RECUR_IS_ACTIVE, RECUR_ACTIVE_BEGIN,
                                                  RECUR_ACTIVE_END, RECUR_FREQUENCY, RECUR_UNIT_ID,
                                                  
                                                  ORGANIZATION_NAME, REPORT_TO_NAME, BILL_TO_NAME,
                                                  ITEM_INVENTORY_ITEM_NAME, ITEM_INVENTORY_ITEM_STORE_ID,
                                                  TEST_NAME, TEST_METHOD_NAME));
    }

    public static String getId() {
        return ID;
    }
    
    public static String getParentOrderId() {
        return PARENT_ORDER_ID;
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
    
    public static String getOrganizationAttention() {
        return ORGANIZATION_ATTENTION;
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
    
    public static String getReportToAttention() {
        return REPORT_TO_ATTENTION;
    }

    public static String getBillToId() {
        return BILL_TO_ID;
    }
    
    public static String getBillToAttention() {
        return BILL_TO_ATTENTION;
    }

    public static String getShipFromId() {
        return SHIP_FROM_ID;
    }
    
    public static String getNumberOfForms() {
        return NUMBER_OF_FORMS;
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
    
    public static String getOrderOrganizationId() {        
        return ORDER_ORG_ID;
    }
    
    public static String getOrderOrganizationOrderId() {
        return ORDER_ORG_ORDER_ID;
    }
    
    public static String getOrderOrganizationOrganizationId() {
        return ORDER_ORG_ORGANIZATION_ID;
    }
    
    public static String getOrderOrganizationAttention() {
        return ORDER_ORG_ATTENTION;
    }
    
    public static String getOrderOrganizationTypeId() {
        return ORDER_ORG_TYPE_ID;
    }
    
    public static String getOrderOrganizationOrganizationName()  {
        return ORDER_ORG_ORGANIZATION_NAME;
    }
    
    public static String getOrderOrganizationOrganizationAddressId() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_ID;                           
    }
    
    public static String getOrderOrganizationOrganizationAddressMultipleUnit() {
       return ORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT;
    }
    
    public static String getOrderOrganizationOrganizationAddressStreetAddress() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS; 
    }
    
    public static String getOrderOrganizationOrganizationAddressCity() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_CITY;
    }
    
    public static String getOrderOrganizationOrganizationAddressState() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_STATE; 
    }
    
    public static String getOrganizationOrganizationAddressZipCode() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE; 
    }
    
    public static String getOrderOrganizationOrganizationAddressWorkPhone() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE;
    }
    
    public static String getOrderOrganizationOrganizationAddressHomePhone() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE;
    }
    
    public static String getOrderOrganizationOrganizationAddressCellPhone() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE; 
    }
       
    public static String getOrderOrganizationOrganizationAddressFaxPhone() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE;
    }
    
    public static String getOrderOrganizationOrganizationAddressEmail() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_EMAIL;
    } 
    
    public static String getOrderOrganizationOrganizationAddressCountry() {
        return ORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY;
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
    
    public static String getContainerId() {
        return CONT_ID;
    }
    
    public static String getContainerOrderId() {
        return CONT_ORDER_ID;
    }
    
    public static String getContainerContainerId() {
        return CONT_CONTAINER_ID;
    }
    
    public static String getContainerItemSequence() {
        return CONT_ITEM_SEQUENCE;
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
    
    public static String getTestItemSequence() {
        return TEST_ITEM_SEQUENCE;    
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
    
    public static String getRecurrenceId(){
        return RECUR_ID;
    }
    
    public static String getRecurrenceOrderId(){
        return RECUR_ORDER_ID;
    }
    
    public static String getRecurrenceIsActive(){
        return RECUR_IS_ACTIVE;
    }
    
    public static String getRecurrenceActiveBegin(){
        return RECUR_ACTIVE_BEGIN;
    }
    
    public static String getRecurrenceActiveEnd(){
        return RECUR_ACTIVE_END;
    }
    
    public static String getRecurrenceFrequency(){
        return RECUR_FREQUENCY;
    }
    
    public static String getRecurrenceUnitId(){
        return RECUR_UNIT_ID;
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
    
    public static String getTestName() {
        return TEST_NAME;
    }
    
    public static String getTestMethodName() {
        return TEST_METHOD_NAME;
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
        
        if (where.indexOf("inventoryReceipt.") > -1)
            if (from.indexOf(oiFrom) < 0)
                from += oiFrom + irFrom;
            else
                from += irFrom;
        
        if (where.indexOf("organization.") > -1)
            from += ",IN (_order.organization) _organization "; 
        
        if (where.indexOf("orderOrganization.") > -1)
            from += ",IN (_order.orderOrganization) _orderOrganization ";
        
        if (where.indexOf("orderOrganizationOrganization.") > -1) {
            if (from.indexOf("orderOrganization") < 0) 
                from += ",IN (_order.orderOrganization) _orderOrganization ";
            from += ",IN (_orderOrganization.organization) _orderOrganizationOrganization ";
        }
        
        if (where.indexOf("reportTo.") > -1)
            from += ",IN (_order.reportTo) _reportTo ";
        if (where.indexOf("billTo.") > -1)
            from += ",IN (_order.billTo) _billTo ";
        if (where.indexOf("orderContainer.") > -1)
            from += ",IN (_order.orderContainer) _orderContainer ";
        if (where.indexOf("_test.") > -1) {
            from += ",IN (_order.orderTest) _orderTest ";        
            from += ",IN (_orderTest.test) _test ";
        }
        if (where.indexOf("_orderTest.") > -1 && from.indexOf("_orderTest.") == -1) 
            from += ",IN (_order.orderTest) _orderTest ";        
        if(where.indexOf("auxData.") > -1)
            from += ", IN (_order.auxData) _auxData ";
        if(where.indexOf("orderRecurrence.") > -1)
            from += ", IN (_order.orderRecurrence) _orderRecurrence ";

        return from;
    }
}