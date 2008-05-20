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
