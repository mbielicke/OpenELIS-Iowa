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
package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table organization. The address
 * information in this class is maintained in AddressDO object.
 */

public class OrganizationDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, parentOrganizationId;
    protected String          name, isActive;
    protected AddressDO       address;

    public OrganizationDO() {
        address = new AddressDO();
    }

    public OrganizationDO(Integer id, Integer parentOrganizationId, String name, String isActive,
                          Integer addressId, String multipleUnit, String streetAddress,
                          String city, String state, String zipCode, String workPhone,
                          String homePhone, String cellPhone, String faxPhone, String email,
                          String country) {
        setId(id);
        setParentOrganizationId(parentOrganizationId);
        setName(name);
        setIsActive(isActive);
        
        address = new AddressDO(addressId, multipleUnit, streetAddress, city, state, zipCode,
                                  workPhone, homePhone, cellPhone, faxPhone, email, country);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Integer parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public AddressDO getAddress() {
        return address;
    }
}
