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
 * Organization META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class OrganizationMeta implements Meta, MetaMap {
    private static final String   ID = "_organization.id",
                                  PARENT_ORGANIZATION_ID = "_organization.parentOrganizationId",
                                  NAME = "_organization.name",
                                  IS_ACTIVE = "_organization.isActive", 
                                  ADDRESS_ID = "_organization.addressId",
                                  ADDR_ID = "_organization.address.id",
                                  ADDR_MULTIPLE_UNIT = "_organization.address.multipleUnit",
                                  ADDR_STREET_ADDRESS = "_organization.address.streetAddress",
                                  ADDR_CITY = "_organization.address.city",
                                  ADDR_STATE = "_organization.address.state",
                                  ADDR_ZIP_CODE = "_organization.address.zipCode",
                                  ADDR_WORK_PHONE = "_organization.address.workPhone",
                                  ADDR_HOME_PHONE = "_organization.address.homePhone",
                                  ADDR_CELL_PHONE = "_organization.address.cellPhone",
                                  ADDR_FAX_PHONE = "_organization.address.faxPhone",
                                  ADDR_EMAIL = "_organization.address.email",
                                  ADDR_COUNTRY = "_organization.address.country",
                                  
                                  CONT_ID = "_organizationContact.id",
                                  CONT_ORGANIZATION_ID = "_organizationContact.organizationId",
                                  CONT_CONTACT_TYPE_ID = "_organizationContact.contactTypeId",
                                  CONT_NAME = "_organizationContact.name",
                                  CONT_ADDRESS_ID = "_organizationContact.addressId",
                                  CONT_ADDR_ID = "_organizationContact.address.id",
                                  CONT_ADDR_MULTIPLE_UNIT = "_organizationContact.address.multipleUnit",
                                  CONT_ADDR_STREET_ADDRESS = "_organizationContact.address.streetAddress",
                                  CONT_ADDR_CITY = "_organizationContact.address.city",
                                  CONT_ADDR_STATE = "_organizationContact.address.state",
                                  CONT_ADDR_ZIP_CODE = "_organizationContact.address.zipCode",
                                  CONT_ADDR_WORK_PHONE = "_organizationContact.address.workPhone",
                                  CONT_ADDR_HOME_PHONE = "_organizationContact.address.homePhone",
                                  CONT_ADDR_CELL_PHONE = "_organizationContact.address.cellPhone",
                                  CONT_ADDR_FAX_PHONE = "_organizationContact.address.faxPhone",
                                  CONT_ADDR_EMAIL = "_organizationContact.address.email",
                                  CONT_ADDR_COUNTRY = "_organizationContact.address.country",
                                  
                                  PARM_ID              = "_organizationParameter.id",
                                  PARM_ORGANIZATION_ID = "_organizationParameter.organizationId",
                                  PARM_TYPE_ID         = "_organizationParameter.typeId",
                                  PARM_VALUE           = "_organizationParameter.value",
                                  
                                  PARENT_ORGANIZATION_NAME = "_organization.parentOrganization.name";

    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, PARENT_ORGANIZATION_ID, NAME, IS_ACTIVE,
                                                  ADDRESS_ID, ADDR_ID, ADDR_MULTIPLE_UNIT, ADDR_STREET_ADDRESS,
                                                  ADDR_CITY, ADDR_STATE, ADDR_ZIP_CODE, ADDR_WORK_PHONE,
                                                  ADDR_HOME_PHONE, ADDR_CELL_PHONE, ADDR_FAX_PHONE,
                                                  ADDR_EMAIL, ADDR_COUNTRY,
                                                  CONT_ID, CONT_ORGANIZATION_ID, CONT_CONTACT_TYPE_ID,
                                                  CONT_NAME, CONT_ADDRESS_ID, CONT_ADDR_ID, 
                                                  CONT_ADDR_MULTIPLE_UNIT, CONT_ADDR_STREET_ADDRESS,
                                                  CONT_ADDR_CITY, CONT_ADDR_STATE, CONT_ADDR_ZIP_CODE,
                                                  CONT_ADDR_WORK_PHONE, CONT_ADDR_HOME_PHONE, 
                                                  CONT_ADDR_CELL_PHONE, CONT_ADDR_FAX_PHONE, CONT_ADDR_EMAIL,
                                                  CONT_ADDR_COUNTRY,
                                                  PARM_ID, PARM_ORGANIZATION_ID, PARM_TYPE_ID, PARM_VALUE,
                                                  PARENT_ORGANIZATION_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getParentOrganizationId() {
        return PARENT_ORGANIZATION_ID;
    }

    public static String getName() {
        return NAME;
    }

    public static String getIsActive() {
        return IS_ACTIVE;
    }

    public static String getAddressId() {
        return ADDRESS_ID;
    }

    public static String getAddressAddressId() {
        return ADDR_ID;
    }

    public static String getAddressMultipleUnit() {
        return ADDR_MULTIPLE_UNIT;
    }

    public static String getAddressStreetAddress() {
        return ADDR_STREET_ADDRESS;
    }

    public static String getAddressCity() {
        return ADDR_CITY;
    }

    public static String getAddressState() {
        return ADDR_STATE;
    }

    public static String getAddressZipCode() {
        return ADDR_ZIP_CODE;
    }

    public static String getAddressWorkPhone() {
        return ADDR_WORK_PHONE;
    }

    public static String getAddressHomePhone() {
        return ADDR_HOME_PHONE;
    }

    public static String getAddressCellPhone() {
        return ADDR_CELL_PHONE;
    }

    public static String getAddressFaxPhone() {
        return ADDR_FAX_PHONE;
    }

    public static String getAddressEmail() {
        return ADDR_EMAIL;
    }

    public static String getAddressCountry() {
        return ADDR_COUNTRY;
    }

    public static String getContactId() {
        return CONT_ID;
    }

    public static String getContactOrganizationId() {
        return CONT_ORGANIZATION_ID;
    }

    public static String getContactContactTypeId() {
        return CONT_CONTACT_TYPE_ID;
    }

    public static String getContactName() {
        return CONT_NAME;
    }

    public static String getContactAddressId() {
        return CONT_ADDRESS_ID;
    }

    public static String getContactAddressAdressId() {
        return CONT_ADDR_ID;
    }

    public static String getContactAddressMultipleUnit() {
        return CONT_ADDR_MULTIPLE_UNIT;
    }

    public static String getContactAddressStreetAddress() {
        return CONT_ADDR_STREET_ADDRESS;
    }

    public static String getContactAddressCity() {
        return CONT_ADDR_CITY;
    }

    public static String getContactAddressState() {
        return CONT_ADDR_STATE;
    }

    public static String getContactAddressZipCode() {
        return CONT_ADDR_ZIP_CODE;
    }

    public static String getContactAddressWorkPhone() {
        return CONT_ADDR_WORK_PHONE;
    }

    public static String getContactAddressHomePhone() {
        return CONT_ADDR_HOME_PHONE;
    }

    public static String getContactAddressCellPhone() {
        return CONT_ADDR_CELL_PHONE;
    }

    public static String getContactAddressFaxPhone() {
        return CONT_ADDR_FAX_PHONE;
    }

    public static String getContactAddressEmail() {
        return CONT_ADDR_EMAIL;
    }

    public static String getContactAddressCountry() {
        return CONT_ADDR_COUNTRY;
    }

    public static String getOrganizationParameterId() {
        return PARM_ID;
    }

    public static String getOrganizationParameterOrganizationId() {
        return PARM_ORGANIZATION_ID;
    }

    public static String getOrganizationParameterTypeId() {
        return PARM_TYPE_ID;
    }

    public static String getOrganizationParameterValue() {
        return PARM_VALUE;
    }

    public static String getParentOrganizationName() {
        return PARENT_ORGANIZATION_NAME;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Organization _organization ";
        if (where.indexOf("organizationContact.") > -1)
            from += ",IN (_organization.organizationContact) _organizationContact ";
        if (where.indexOf("organizationParameter.") > -1)
            from += ",IN (_organization.organizationParameter) _organizationParameter ";

        return from;
    }
}
