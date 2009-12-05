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
 * Provider META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class ProviderMeta implements Meta, MetaMap {
    private static final String   ID = "_provider.id", 
                                  LAST_NAME = "_provider.lastName",
                                  FIRST_NAME = "_provider.firstName",
                                  MIDDLE_NAME = "_provider.middleName",
                                  TYPE_ID = "_provider.typeId",
                                  NPI = "_provider.npi",
                                  
                                  LOC_ID = "_providerLocation.id", 
                                  LOC_LOCATION = "_providerLocation.location",
                                  LOC_EXTERNAL_ID = "_providerLocation.externalId",
                                  LOC_PROVIDER_ID = "_providerLocation.providerId",
                                  LOC_ADDRESS_ID = "_providerLocation.addressId",
                                  LOC_ADDR_ID = "_providerLocation.address.id",
                                  LOC_ADDR_MULTIPLE_UNIT = "_providerLocation.address.multipleUnit",
                                  LOC_ADDR_STREET_ADDRESS = "_providerLocation.address.streetAddress",
                                  LOC_ADDR_CITY = "_providerLocation.address.city",
                                  LOC_ADDR_STATE = "_providerLocation.address.state",
                                  LOC_ADDR_ZIP_CODE = "_providerLocation.address.zipCode",
                                  LOC_ADDR_WORK_PHONE = "_providerLocation.address.workPhone",
                                  LOC_ADDR_HOME_PHONE = "_providerLocation.address.homePhone",
                                  LOC_ADDR_CELL_PHONE = "_providerLocation.address.cellPhone",
                                  LOC_ADDR_FAX_PHONE = "_providerLocation.address.faxPhone",
                                  LOC_ADDR_EMAIL = "_providerLocation.address.email",
                                  LOC_ADDR_COUNTRY = "_providerLocation.address.country";

    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, LAST_NAME, FIRST_NAME, MIDDLE_NAME, TYPE_ID, NPI,
                                                  LOC_ID, LOC_LOCATION, LOC_EXTERNAL_ID,
                                                  LOC_PROVIDER_ID, LOC_ADDRESS_ID, LOC_ADDR_ID,
                                                  LOC_ADDR_MULTIPLE_UNIT, LOC_ADDR_STREET_ADDRESS,
                                                  LOC_ADDR_CITY, LOC_ADDR_STATE, LOC_ADDR_ZIP_CODE,
                                                  LOC_ADDR_WORK_PHONE, LOC_ADDR_HOME_PHONE, 
                                                  LOC_ADDR_CELL_PHONE, LOC_ADDR_FAX_PHONE, 
                                                  LOC_ADDR_EMAIL, LOC_ADDR_COUNTRY));
    }

    public static String getId() {
        return ID;
    }

    public static String getLastName() {
        return LAST_NAME;
    }

    public static String getFirstName() {
        return FIRST_NAME;
    }

    public static String getMiddleName() {
        return MIDDLE_NAME;
    }

    public static String getTypeId() {
        return TYPE_ID;
    }

    public static String getNpi() {
        return NPI;
    }

    public static String getProviderLocationId() {
        return LOC_ID;
    }

    public static String getProviderLocationLocation() {
        return LOC_LOCATION;
    }

    public static String getProviderLocationExternalId() {
        return LOC_EXTERNAL_ID;
    }

    public static String getProviderLocationProviderId() {
        return LOC_PROVIDER_ID;
    }

    public static String getProviderLocationAddressId() {
        return LOC_ADDRESS_ID;
    }

    //
    // this is a problem because it makes the same pattern as provider_location.address_id
    // we don't need it, but just in case.
    //
    public static String getProviderLocationAddress_Id() {
        return LOC_ADDR_ID;
    }

    public static String getProviderLocationAddressMultipleUnit() {
        return LOC_ADDR_MULTIPLE_UNIT;
    }

    public static String getProviderLocationAddressStreetAddress() {
        return LOC_ADDR_STREET_ADDRESS;
    }

    public static String getProviderLocationAddressCity() {
        return LOC_ADDR_CITY;
    }

    public static String getProviderLocationAddressState() {
        return LOC_ADDR_STATE;
    }

    public static String getProviderLocationAddressZipCode() {
        return LOC_ADDR_ZIP_CODE;
    }

    public static String getProviderLocationAddressWorkPhone() {
        return LOC_ADDR_WORK_PHONE;
    }

    public static String getProviderLocationAddressHomePhone() {
        return LOC_ADDR_HOME_PHONE;
    }

    public static String getProviderLocationAddressCellPhone() {
        return LOC_ADDR_CELL_PHONE;
    }

    public static String getProviderLocationAddressFaxPhone() {
        return LOC_ADDR_FAX_PHONE;
    }

    public static String getProviderLocationAddressEmail() {
        return LOC_ADDR_EMAIL;
    }

    public static String getProviderLocationAddressCountry() {
        return LOC_ADDR_COUNTRY;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Provider _provider ";
        if (where.indexOf("providerLocation.") > -1)
            from += ",IN (_provider.providerLocation) _providerLocation ";

        return from;
    }
}