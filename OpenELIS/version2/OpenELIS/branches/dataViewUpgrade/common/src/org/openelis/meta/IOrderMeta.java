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
  * IOrder META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class IOrderMeta implements Meta, MetaMap {
	private static final String    ID = "_iorder.id",
	                               PARENT_IORDER_ID = "_iorder.parentIorderId", 
	                               DESCRIPTION = "_iorder.description",
	                               STATUS_ID = "_iorder.statusId",
	                               ORDERED_DATE = "_iorder.orderedDate",
	                               NEEDED_IN_DAYS = "_iorder.neededInDays",
	                               REQUESTED_BY = "_iorder.requestedBy",
	                               COST_CENTER_ID = "_iorder.costCenterId",
	                               ORGANIZATION_ID = "_iorder.organizationId",
	                               ORGANIZATION_ATTENTION = "_iorder.organizationAttention",
	                               TYPE = "_iorder.type",
	                               EXTERNAL_ORDER_NUMBER = "_iorder.externalOrderNumber",
	                               REPORT_TO_ID = "_iorder.reportToId",
	                               REPORT_TO_ATTENTION = "_iorder.reportToAttention",
	                               BILL_TO_ID = "_iorder.billToId",
	                               BILL_TO_ATTENTION = "_iorder.billToAttention",
	                               SHIP_FROM_ID = "_iorder.shipFromId",
	                               NUMBER_OF_FORMS = "_iorder.numberOfForms",
	                               
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
	                               
	                               IORDER_ORG_ID = "_iorderOrganization.id",
	                               IORDER_ORG_IORDER_ID = "_iorderOrganization.iorderId",
	                               IORDER_ORG_ORGANIZATION_ID = "_iorderOrganization.organizationId",
	                               IORDER_ORG_ORGANIZATION_ATTENTION = "_iorderOrganization.organizationAttention",
	                               IORDER_ORG_TYPE_ID = "_iorderOrganization.typeId",
	                               
	                               IORDER_ORG_ORGANIZATION_ADDRESS_ID = "_iorderOrganization.organization.addressId",	   
	                               
	                               IORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT = "_iorderOrganizationOrganization.address.multipleUnit",
	                               IORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS = "_iorderOrganizationOrganization.address.streetAddress", 
	                               IORDER_ORG_ORGANIZATION_ADDRESS_CITY = "_iorderOrganizationOrganization.address.city",
	                               IORDER_ORG_ORGANIZATION_ADDRESS_STATE = "_iorderOrganizationOrganization.address.state", 
	                               IORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE = "_iorderOrganizationOrganization.address.zipCode",
	                               IORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE = "_iorderOrganizationOrganization.address.workPhone", 
	                               IORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE = "_iorderOrganizationOrganization.address.homePhone",
	                               IORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE = "_iorderOrganizationOrganization.address.cellPhone", 
	                               IORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE = "_iorderOrganizationOrganization.address.faxPhone",
	                               IORDER_ORG_ORGANIZATION_ADDRESS_EMAIL = "_iorderOrganizationOrganization.address.email", 
	                               IORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY = "_iorderOrganizationOrganization.address.country",
	                               
	                               ITEM_ID = "_iorderItem.id",
	                               ITEM_IORDER_ID = "_iorderItem.iorderId",
	                               ITEM_INVENTORY_ITEM_ID = "_iorderItem.inventoryItemId",
	                               ITEM_QUANTITY = "_iorderItem.quantity",
	                               ITEM_CATALOG_NUMBER = "_iorderItem.catalogNumber",
	                               ITEM_UNIT_COST = "_iorderItem.unitCost",
	                               
	                               RCPT_ID = "_inventoryReceipt.id",
	                               RCPT_INVENTORY_ITEM_ID = "_inventoryReceipt.inventoryItemId",
	                               RCPT_IORDER_ITEM_ID = "_inventoryReceipt.iorderItemId",
	                               RCPT_ORGANIZATION_ID = "_inventoryReceipt.organizationId",
	                               RCPT_RECEIVED_DATE = "_inventoryReceipt.receivedDate",
	                               RCPT_QUANTITY_RECEIVED = "_inventoryReceipt.quantityReceived",
	                               RCPT_UNIT_COST = "_inventoryReceipt.unitCost",
	                               RCPT_QC_REFERENCE = "_inventoryReceipt.qcReference",
	                               RCPT_EXTERNAL_REFERENCE = "_inventoryReceipt.externalReference",
	                               RCPT_UPC = "_inventoryReceipt.upc",	                              	                              
	                               
	                               CONT_ID = "_iorderContainer.id",
	                               CONT_IORDER_ID = "_iorderContainer.iorderId",
	                               CONT_CONTAINER_ID = "_iorderContainer.containerId",
	                               CONT_ITEM_SEQUENCE = "_iorderContainer.itemSequence",
	                               CONT_TYPE_OF_SAMPLE_ID = "_iorderContainer.typeOfSampleId",
	                               
	                               TEST_ID = "_iorderTest.id",
	                               TEST_IORDER_ID = "_iorderTest.iorderId",
	                               TEST_ITEM_SEQUENCE = "_iorderTest.itemSequence",
	                               TEST_SORT_ORDER = "_iorderTest.sortOrder",
	                               TEST_REFERENCE_ID = "_iorderTest.referenceId",
	                               TEST_REFERENCE_TABLE_ID = "_iorderTest.referenceTableId",
	                               
	                               AUX_DATA_ID = "_auxData.id",
	                               AUX_DATA_AUX_FIELD_ID = "_auxData.auxFieldId",
	                               AUX_DATA_REFERENCE_ID = "_auxData.referenceId",
	                               AUX_DATA_REFERENCE_TABLE_ID = "_auxData.referenceTableId",
	                               AUX_DATA_IS_REPORTABLE = "_auxData.isReportable",
	                               AUX_DATA_TYPE_ID = "_auxData.typeId",
	                               AUX_DATA_VALUE = "_auxData.value",
	                               
                                   RECUR_ID = "_iorderRecurrence.id",
                                   RECUR_IORDER_ID = "_iorderRecurrence.iorderId",
                                   RECUR_IS_ACTIVE = "_iorderRecurrence.isActive",
                                   RECUR_ACTIVE_BEGIN = "_iorderRecurrence.activeBegin",
                                   RECUR_ACTIVE_END = "_iorderRecurrence.activeEnd",
                                   RECUR_FREQUENCY = "_iorderRecurrence.frequency",
                                   RECUR_UNIT_ID = "_iorderRecurrence.unitId",
	                               	                               
	                               ORGANIZATION_NAME = "_iorder.organization.name",
	                               IORDER_ORG_ORGANIZATION_NAME = "_iorderOrganization.organization.name", 
	                               REPORT_TO_NAME = "_reportTo.name",
                                   BILL_TO_NAME = "_billTo.name",
                                   ITEM_INVENTORY_ITEM_NAME = "_iorderItem.inventoryItem.name",
	                               ITEM_INVENTORY_ITEM_STORE_ID = "_iorderItem.inventoryItem.storeId",
	                               TEST_NAME = "_test.name",
	                               TEST_METHOD_NAME = "_test.method.name";
	                                                

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, PARENT_IORDER_ID, DESCRIPTION, STATUS_ID, ORDERED_DATE,
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
                                                  
                                                  IORDER_ORG_ID, IORDER_ORG_IORDER_ID, IORDER_ORG_ORGANIZATION_ID,
                                                  IORDER_ORG_TYPE_ID, IORDER_ORG_ORGANIZATION_ATTENTION,
                                                  
                                                  IORDER_ORG_ORGANIZATION_NAME,  IORDER_ORG_ORGANIZATION_ADDRESS_ID,                                                       
                                                  
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT,
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS, 
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_CITY, IORDER_ORG_ORGANIZATION_ADDRESS_STATE, 
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE, IORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE, 
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE, IORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE, 
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE, IORDER_ORG_ORGANIZATION_ADDRESS_EMAIL, 
                                                  IORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY,
                                                  
                                                  ITEM_ID, ITEM_IORDER_ID, ITEM_INVENTORY_ITEM_ID,
                                                  ITEM_QUANTITY, ITEM_CATALOG_NUMBER, ITEM_UNIT_COST,

                                                  RCPT_ID, RCPT_INVENTORY_ITEM_ID, RCPT_IORDER_ITEM_ID,
                                                  RCPT_ORGANIZATION_ID, RCPT_RECEIVED_DATE, RCPT_QUANTITY_RECEIVED,
                                                  RCPT_UNIT_COST, RCPT_QC_REFERENCE, RCPT_EXTERNAL_REFERENCE,
                                                  RCPT_UPC,                                                
                                                  
                                                  CONT_ID, CONT_IORDER_ID, CONT_CONTAINER_ID,
                                                  CONT_ITEM_SEQUENCE, CONT_TYPE_OF_SAMPLE_ID,
                                                  
                                                  TEST_ID, TEST_IORDER_ID, TEST_ITEM_SEQUENCE, TEST_SORT_ORDER,
                                                  TEST_REFERENCE_ID, TEST_REFERENCE_TABLE_ID,
                                                  
                                                  AUX_DATA_ID, AUX_DATA_AUX_FIELD_ID, AUX_DATA_REFERENCE_ID,
                                                  AUX_DATA_REFERENCE_TABLE_ID, AUX_DATA_IS_REPORTABLE,
                                                  AUX_DATA_TYPE_ID, AUX_DATA_VALUE,
                                                  
                                                  RECUR_ID, RECUR_IORDER_ID, RECUR_IS_ACTIVE, RECUR_ACTIVE_BEGIN,
                                                  RECUR_ACTIVE_END, RECUR_FREQUENCY, RECUR_UNIT_ID,
                                                  
                                                  ORGANIZATION_NAME, REPORT_TO_NAME, BILL_TO_NAME,
                                                  ITEM_INVENTORY_ITEM_NAME, ITEM_INVENTORY_ITEM_STORE_ID,
                                                  TEST_NAME, TEST_METHOD_NAME));
    }

    public static String getId() {
        return ID;
    }
    
    public static String getParentIorderId() {
        return PARENT_IORDER_ID;
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
    
    public static String getIorderOrganizationId() {        
        return IORDER_ORG_ID;
    }
    
    public static String getIorderOrganizationOrderId() {
        return IORDER_ORG_IORDER_ID;
    }
    
    public static String getIorderOrganizationOrganizationId() {
        return IORDER_ORG_ORGANIZATION_ID;
    }
    
    public static String getIorderOrganizationOrganizationAttention() {
        return IORDER_ORG_ORGANIZATION_ATTENTION;
    }
    
    public static String getIorderOrganizationTypeId() {
        return IORDER_ORG_TYPE_ID;
    }
    
    public static String getIorderOrganizationOrganizationName()  {
        return IORDER_ORG_ORGANIZATION_NAME;
    }
    
    public static String getIorderOrganizationOrganizationAddressId() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_ID;                           
    }
    
    public static String getIorderOrganizationOrganizationAddressMultipleUnit() {
       return IORDER_ORG_ORGANIZATION_ADDRESS_MULTIPLE_UNIT;
    }
    
    public static String getIorderOrganizationOrganizationAddressStreetAddress() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_STREET_ADDRESS; 
    }
    
    public static String getIorderOrganizationOrganizationAddressCity() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_CITY;
    }
    
    public static String getIorderOrganizationOrganizationAddressState() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_STATE; 
    }
    
    public static String getIorderOrganizationOrganizationAddressZipCode() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_ZIP_CODE; 
    }
    
    public static String getIorderOrganizationOrganizationAddressWorkPhone() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_WORK_PHONE;
    }
    
    public static String getIorderOrganizationOrganizationAddressHomePhone() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_HOME_PHONE;
    }
    
    public static String getIorderOrganizationOrganizationAddressCellPhone() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_CELL_PHONE; 
    }
       
    public static String getIorderOrganizationOrganizationAddressFaxPhone() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_FAX_PHONE;
    }
    
    public static String getIorderOrganizationOrganizationAddressEmail() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_EMAIL;
    } 
    
    public static String getIorderOrganizationOrganizationAddressCountry() {
        return IORDER_ORG_ORGANIZATION_ADDRESS_COUNTRY;
    }

    public static String getIorderItemId() {
        return ITEM_ID;
    }

    public static String getIorderItemOrderId() {
        return ITEM_IORDER_ID;
    }

    public static String getIorderItemInventoryItemId() {
        return ITEM_INVENTORY_ITEM_ID;
    }

    public static String getIorderItemQuantity() {
        return ITEM_QUANTITY;
    }

    public static String getIorderItemCatalogNumber() {
        return ITEM_CATALOG_NUMBER;
    }

    public static String getIorderItemUnitCost() {
        return ITEM_UNIT_COST;
    }

    public static String getInventoryReceiptId() {
        return RCPT_ID;
    }

    public static String getInventoryReceiptInventoryItemId() {
        return RCPT_INVENTORY_ITEM_ID;
    }

    public static String getInventoryReceiptIorderItemId() {
        return RCPT_IORDER_ITEM_ID;
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
    
    public static String getContainerIorderId() {
        return CONT_IORDER_ID;
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
    
    public static String getTestIorderId() {
        return TEST_IORDER_ID;
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
        return RECUR_IORDER_ID;
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

    public static String getIorderItemInventoryItemName() {
        return ITEM_INVENTORY_ITEM_NAME;
    }

    public static String getIorderItemInventoryItemStoreId() {
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
        
        oiFrom = ",IN (_iorder.iorderItem) _iorderItem ";
        irFrom = ",IN (_iorderItem.inventoryReceipt) _inventoryReceipt ";
        
        from = "IOrder _iorder ";
        if (where.indexOf("iorderItem.") > -1)
            from += oiFrom;
        
        if (where.indexOf("inventoryReceipt.") > -1)
            if (from.indexOf(oiFrom) < 0)
                from += oiFrom + irFrom;
            else
                from += irFrom;
        
        if (where.indexOf("organization.") > -1)
            from += ",IN (_iorder.organization) _organization "; 
        
        if (where.indexOf("iorderOrganization.") > -1)
            from += ",IN (_iorder.iorderOrganization) _iorderOrganization ";
        
        if (where.indexOf("iorderOrganizationOrganization.") > -1) {
            if (from.indexOf("iorderOrganization") < 0) 
                from += ",IN (_iorder.iorderOrganization) _iorderOrganization ";
            from += ",IN (_iorderOrganization.organization) _iorderOrganizationOrganization ";
        }
        
        if (where.indexOf("reportTo.") > -1)
            from += ",IN (_iorder.reportTo) _reportTo ";
        if (where.indexOf("billTo.") > -1)
            from += ",IN (_iorder.billTo) _billTo ";
        if (where.indexOf("iorderContainer.") > -1)
            from += ",IN (_iorder.iorderContainer) _iorderContainer ";
        if (where.indexOf("_test.") > -1) {
            from += ",IN (_iorder.iorderTest) _iorderTest ";        
            from += ",IN (_iorderTest.test) _test ";
        }
        if (where.indexOf("_iorderTest.") > -1 && from.indexOf("_iorderTest.") == -1) 
            from += ",IN (_iorder.iorderTest) _iorderTest ";        
        if(where.indexOf("auxData.") > -1)
            from += ", IN (_iorder.auxData) _auxData ";
        if(where.indexOf("iorderRecurrence.") > -1)
            from += ", IN (_iorder.iorderRecurrence) _iorderRecurrence ";

        return from;
    }
}