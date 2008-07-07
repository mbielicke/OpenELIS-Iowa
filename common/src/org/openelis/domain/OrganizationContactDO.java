/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class OrganizationContactDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3193069940053803677L;

    // contact fields
    protected Integer         id;
    protected Integer         organization;
    protected Integer         contactType;
    protected String          name;

    // address fields
    protected AddressDO       addressDO        = new AddressDO();

    protected Boolean         delete           = false;

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public OrganizationContactDO() {

    }

    public OrganizationContactDO(Integer id,
                                 Integer organization,
                                 Integer contactType,
                                 String name,
                                 Integer addressId,
                                 String multipleUnit,
                                 String streetAddress,
                                 String city,
                                 String state,
                                 String zipCode,
                                 String workPhone,
                                 String homePhone,
                                 String cellPhone,
                                 String faxPhone,
                                 String email,
                                 String country) {
        setId(id);
        setOrganization(organization);
        setContactType(contactType);
        setName(name);
        addressDO.setId(addressId);
        addressDO.setMultipleUnit(multipleUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
        addressDO.setWorkPhone(workPhone);
        addressDO.setHomePhone(homePhone);
        addressDO.setCellPhone(cellPhone);
        addressDO.setFaxPhone(faxPhone);
        addressDO.setEmail(email);
        addressDO.setCountry(country);

    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public Integer getOrganization() {
        return organization;
    }

    public void setOrganization(Integer organization) {
        this.organization = organization;
    }

    public AddressDO getAddressDO() {
        return addressDO;
    }
}
