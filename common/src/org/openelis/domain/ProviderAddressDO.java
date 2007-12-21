package org.openelis.domain;

import java.io.Serializable;



public class ProviderAddressDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1754500573676617534L;
    
    private Integer id;             

    private String location;             

    private String externalId;             

    private Integer provider;       

    private AddressDO addressDO ;
    
    public ProviderAddressDO(){
        
    }
    
    public ProviderAddressDO(Integer id, String location, String externalId, Integer provider, Integer addressId, String multipleUnit, 
                            String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone, String cellPhone, String faxPhone,
                             String email, String country){
        
        this.id = id;
        this.location = location;
        this.externalId = externalId;
        this.provider = provider;
        addressDO = new AddressDO();
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
        this.externalId = externalId;
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
        this.location = location;
    }

    public Integer getProvider() {
        return provider;
    }

    public void setProvider(Integer provider) {
        this.provider = provider;
    }
    

}
