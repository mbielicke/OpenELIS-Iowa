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

public class OrganizationMeta {
    public static final String   ID = "id",
                                  PARENT_ORGANIZATION_ID = "parentOrganizationId",
                                  NAME = "name",
                                  IS_ACTIVE = "isActive", 
                                  ADDRESS_ID = "addressId",
                                  ADDR_ID = "address.id",
                                  ADDR_MULTIPLE_UNIT = "address.multipleUnit",
                                  ADDR_STREET_ADDRESS = "address.streetAddress",
                                  ADDR_CITY = "address.city",
                                  ADDR_STATE = "address.state",
                                  ADDR_ZIP_CODE = "address.zipCode",
                                  ADDR_WORK_PHONE = "address.workPhone",
                                  ADDR_HOME_PHONE = "address.homePhone",
                                  ADDR_CELL_PHONE = "address.cellPhone",
                                  ADDR_FAX_PHONE = "address.faxPhone",
                                  ADDR_EMAIL = "address.email",
                                  ADDR_COUNTRY = "address.country",
                                  
                                  CONT_ID = "organizationContact.id",
                                  CONT_ORGANIZATION_ID = "organizationContact.organizationId",
                                  CONT_CONTACT_TYPE_ID = "organizationContact.contactTypeId",
                                  CONT_NAME = "organizationContact.name",
                                  CONT_ADDRESS_ID = "organizationContact.addressId",
                                  CONT_ADDR_ID = "organizationContact.address.id",
                                  CONT_ADDR_MULTIPLE_UNIT = "organizationContact.address.multipleUnit",
                                  CONT_ADDR_STREET_ADDRESS = "organizationContact.address.streetAddress",
                                  CONT_ADDR_CITY = "organizationContact.address.city",
                                  CONT_ADDR_STATE = "organizationContact.address.state",
                                  CONT_ADDR_ZIP_CODE = "organizationContact.address.zipCode",
                                  CONT_ADDR_WORK_PHONE = "organizationContact.address.workPhone",
                                  CONT_ADDR_HOME_PHONE = "organizationContact.address.homePhone",
                                  CONT_ADDR_CELL_PHONE = "organizationContact.address.cellPhone",
                                  CONT_ADDR_FAX_PHONE = "organizationContact.address.faxPhone",
                                  CONT_ADDR_EMAIL = "organizationContact.address.email",
                                  CONT_ADDR_COUNTRY = "organizationContact.address.country",
                                  
                                  PARM_ID              = "organizationParameter.id",
                                  PARM_ORGANIZATION_ID = "organizationParameter.organizationId",
                                  PARM_TYPE_ID         = "organizationParameter.typeId",
                                  PARM_VALUE           = "organizationParameter.value",
                                  
                                  PARENT_ORGANIZATION_NAME = "parentOrganization.name";
}
