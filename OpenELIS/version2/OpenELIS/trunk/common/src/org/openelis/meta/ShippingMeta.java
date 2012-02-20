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
  * Shipping META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class ShippingMeta implements Meta, MetaMap {
	
	private static final String ID = "_shipping.id",
                                STATUS_ID = "_shipping.statusId",
                                SHIPPED_FROM_ID = "_shipping.shippedFromId",
                                SHIPPED_TO_ID = "_shipping.shippedToId",
                                SHIPPED_TO_ATTENTION = "_shipping.shippedToAttention",
                                PROCESSED_BY	= "_shipping.processedBy",
                                PROCESSED_DATE = "_shipping.processedDate",
                                SHIPPED_METHOD_ID = "_shipping.shippedMethodId",
                                SHIPPED_DATE = "_shipping.shippedDate",
                                NUMBER_OF_PACKAGES = "_shipping.numberOfPackages",
                                COST = "_shipping.cost",
                                
                                SHIPPED_TO_ADDRESS_ID = "_shippedTo.address.id",
                                SHIPPED_TO_ADDRESS_MULTIPLE_UNIT = "_shippedTo.address.multipleUnit",
                                SHIPPED_TO_ADDRESS_STREET_ADDRESS = "_shippedTo.address.streetAddress",
                                SHIPPED_TO_ADDRESS_CITY = "_shippedTo.address.city",
                                SHIPPED_TO_ADDRESS_STATE = "_shippedTo.address.state",
                                SHIPPED_TO_ADDRESS_ZIP_CODE = "_shippedTo.address.zipCode",
                                SHIPPED_TO_ADDRESS_WORK_PHONE = "_shippedTo.address.workPhone",
                                SHIPPED_TO_ADDRESS_HOME_PHONE = "_shippedTo.address.homePhone",
                                SHIPPED_TO_ADDRESS_CELL_PHONE = "_shippedTo.address.cellPhone",
                                SHIPPED_TO_ADDRESS_FAX_PHONE = "_shippedTo.address.faxPhone",
                                SHIPPED_TO_ADDRESS_EMAIL = "_shippedTo.address.email",
                                SHIPPED_TO_ADDRESS_COUNTRY = "_shippedTo.address.country",
                                
                                ITEM_ID = "_shippingItem.id",
                                ITEM_SHIPPING_ID = "_shippingItem.shippingId",
                                ITEM_REFERENCE_TABLE_ID = "_shippingItem.referenceTableId",
                                ITEM_REFERENCE_ID = "_shippingItem.referenceId",
                                ITEM_QUANTITY = "_shippingItem.quantity",
                                ITEM_DESCRIPTION = "_shippingItem.description",
                                
                                TRACKING_ID = "_shippingTracking.id",
                                TRACKING_SHIPPING_ID = "_shippingTracking.shippingId",
                                TRACKING_TRACKING_NUMBER = "_shippingTracking.trackingNumber",
	
                                SHIPPED_TO_NAME = "_shipping.shippedTo.name";
  	  
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, STATUS_ID, SHIPPED_FROM_ID, SHIPPED_TO_ID, 
                                                  SHIPPED_TO_ATTENTION, PROCESSED_BY, PROCESSED_DATE,
                                                  SHIPPED_METHOD_ID, SHIPPED_DATE, NUMBER_OF_PACKAGES, COST,
                                                  SHIPPED_TO_ADDRESS_ID, SHIPPED_TO_ADDRESS_MULTIPLE_UNIT,
                                                  SHIPPED_TO_ADDRESS_STREET_ADDRESS, SHIPPED_TO_ADDRESS_CITY,
                                                  SHIPPED_TO_ADDRESS_STATE, SHIPPED_TO_ADDRESS_ZIP_CODE,
                                                  SHIPPED_TO_ADDRESS_WORK_PHONE, SHIPPED_TO_ADDRESS_HOME_PHONE,
                                                  SHIPPED_TO_ADDRESS_CELL_PHONE, SHIPPED_TO_ADDRESS_FAX_PHONE,
                                                  SHIPPED_TO_ADDRESS_EMAIL, SHIPPED_TO_ADDRESS_COUNTRY,
                                                  ITEM_ID, ITEM_SHIPPING_ID, ITEM_REFERENCE_TABLE_ID,
                                                  ITEM_REFERENCE_ID, ITEM_QUANTITY, ITEM_DESCRIPTION,
                                                  TRACKING_ID, TRACKING_SHIPPING_ID, TRACKING_TRACKING_NUMBER,
                                                  SHIPPED_TO_NAME));
    }
        
    public static String getId() {
        return ID;
    } 

    public static String getStatusId() {
        return STATUS_ID;
    } 

    public static String getShippedFromId() {
        return SHIPPED_FROM_ID;
    } 
    
    public static String getShippedToId(){
        return SHIPPED_TO_ID;
    }
    
    public static String getShippedToAttention(){
        return SHIPPED_TO_ATTENTION;
    }

    public static String getProcessedBy() {
        return PROCESSED_BY;
    } 

    public static String getProcessedDate() {
        return PROCESSED_DATE;
    } 

    public static String getShippedMethodId() {
        return SHIPPED_METHOD_ID;
    } 

    public static String getShippedDate() {
        return SHIPPED_DATE;
    } 

    public static String getNumberOfPackages() {
        return NUMBER_OF_PACKAGES;
    }  
    
    public static String getCost(){
        return COST;
    } 
    
    public static String getShippedToAddressId() {
        return SHIPPED_TO_ADDRESS_ID;
    }
    
    public static String getShippedToAddressMultipleUnit() {
        return SHIPPED_TO_ADDRESS_MULTIPLE_UNIT;
    }

    public static String getShippedToAddressStreetAddress() {
        return SHIPPED_TO_ADDRESS_STREET_ADDRESS;
    }

    public static String getShippedToAddressCity() {
        return SHIPPED_TO_ADDRESS_CITY;
    }

    public static String getShippedToAddressState() {
        return SHIPPED_TO_ADDRESS_STATE;
    }

    public static String getShippedToAddressZipCode() {
        return SHIPPED_TO_ADDRESS_ZIP_CODE;
    }
    
    public static String getShippedToAddressWorkPhone() {
        return SHIPPED_TO_ADDRESS_WORK_PHONE;
    }

    public static String getShippedToAddressHomePhone() {
        return SHIPPED_TO_ADDRESS_HOME_PHONE;
    }

    public static String getShippedToAddressCellPhone() {
        return SHIPPED_TO_ADDRESS_CELL_PHONE;
    }

    public static String getShippedToAddressFaxPhone() {
        return SHIPPED_TO_ADDRESS_FAX_PHONE;
    }

    public static String getShippedToAddressEmail() {
        return SHIPPED_TO_ADDRESS_EMAIL;
    }

    public static String getShippedToAddressCountry() {
        return SHIPPED_TO_ADDRESS_COUNTRY;
    }
    
    public static String getItemId() {
        return ITEM_ID;
    } 
    
    public static String getItemShippingId() {
        return ITEM_SHIPPING_ID;
    } 
    
    public static String getItemReferenceTableId() {
        return ITEM_REFERENCE_TABLE_ID;
    }
    
    public static String getItemReferenceId() {
        return ITEM_REFERENCE_ID; 
    }
    
    public static String getItemQuantity() {
        return ITEM_QUANTITY; 
    } 
    
    public static String getItemDescription() {
        return ITEM_DESCRIPTION;
    }
    
    public static String getTrackingId() {
        return TRACKING_ID;
    } 

    public static String getTrackingShippingId() {
        return TRACKING_SHIPPING_ID;
    } 

    public static String getTrackingTrackingNumber() {
        return TRACKING_TRACKING_NUMBER;
    }
    
    public static String getShippedToName() {
        return SHIPPED_TO_NAME;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from = "Shipping _shipping ";
        if(where.indexOf("shippingItem.") > -1)
            from += ", IN (_shipping.shippingItem) _shippingItem ";
        if(where.indexOf("shippingTracking.") > -1)
            from += ", IN (_shipping.shippingTracking) _shippingTracking ";
        if(where.indexOf("shippedTo.") > -1)
            from += ", IN (_shipping.shippedTo) _shippedTo ";

        return from;
    } 
}   
