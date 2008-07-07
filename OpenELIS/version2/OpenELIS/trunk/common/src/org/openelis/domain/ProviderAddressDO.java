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



public class ProviderAddressDO implements Serializable {

    private static final long serialVersionUID = -1754500573676617534L;
    
    protected Integer id;             
    protected String location;      
    protected String externalId;   
    protected Integer provider;    
    protected AddressDO addressDO = new AddressDO();
    
    protected Boolean delete;
    
    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public ProviderAddressDO(){
        
    }
    
    public ProviderAddressDO(Integer id, String location, String externalId, Integer provider, Integer addressId, String multipleUnit, 
                            String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone, String cellPhone, String faxPhone,
                             String email, String country){
        
        setId(id);
        setLocation(location);
        setExternalId(externalId);
        setProvider(provider);       
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

    public AddressDO getAddressDO() {
        return addressDO;
    }

    public void setAddressDO(AddressDO addressDO) {
        this.addressDO = addressDO;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = DataBaseUtil.trim(externalId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public Integer getProvider() {
        return provider;
    }

    public void setProvider(Integer provider) {
        this.provider = provider;
    }
    

}
