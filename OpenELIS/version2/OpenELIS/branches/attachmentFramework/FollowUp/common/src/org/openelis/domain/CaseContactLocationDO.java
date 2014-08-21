package org.openelis.domain;

public class CaseContactLocationDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, caseContactId, addressId;
    protected String location;
    
    protected AddressDO address;
    
    public CaseContactLocationDO() {
        address = new AddressDO();
    }
    
    public CaseContactLocationDO(Integer id, Integer caseContactId, String location, Integer addressId,
                                 String multipleUnit, String streetAddress, String city,
                                 String state, String zipCode, String workPhone, String homePhone,
                                 String cellPhone, String faxPhone, String email, String country) {
        
        setId(id);
        setCaseContactId(caseContactId);
        setLocation(location);
        
        address = new AddressDO(addressId, multipleUnit, streetAddress, city, state, zipCode, workPhone, homePhone, cellPhone, faxPhone, email, country);

        _changed = false;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public Integer getCaseContactId() {
        return caseContactId;
    }
    
    public void setCaseContactId(Integer caseContactId) {
        this.caseContactId = caseContactId;
        _changed = true;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
        _changed = true;
    }
    
    public AddressDO getAddress() {
        return address;
    }
    
    public void setAddress(AddressDO address) {
        this.address = address;
        _changed = true;
    }

}
