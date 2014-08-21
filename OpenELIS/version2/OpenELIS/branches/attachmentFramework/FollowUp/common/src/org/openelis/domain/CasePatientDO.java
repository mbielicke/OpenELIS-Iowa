package org.openelis.domain;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class CasePatientDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id,genderId,raceId,ethnicityId;
    protected Datetime birthDate, birthTime;
    protected String  lastName, firstName, nationalId;
    protected AddressDO address;
    
    public CasePatientDO() {
        address = new AddressDO();
    }
    
    public CasePatientDO(Integer id, String lastName, String firstName, Integer addressId, Date birthDate, Date birthTime,
                         Integer genderId, Integer raceId, Integer ethnicityId, String nationalId,String multipleUnit, String streetAddress, String city,
                         String state, String zipCode, String workPhone, String homePhone,
                         String cellPhone, String faxPhone, String email, String country) {
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setBirthDate(DataBaseUtil.toYD(birthDate));
        setBirthTime(DataBaseUtil.toHM(birthTime));
        setGenderId(genderId);
        setRaceId(raceId);
        setEthnicityId(ethnicityId);
        setNationalId(nationalId);
        
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
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        _changed = true;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        _changed = true;
    }
    
    public Datetime getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(Datetime birthDate) {
        this.birthDate = DataBaseUtil.toYD(birthDate);
        _changed = true;
    }
    
    public Datetime getBirthTime() {
        return birthTime;
    }
    
    public void setBirthTime(Datetime birthTime) {
        this.birthTime = DataBaseUtil.toYD(birthTime);
        _changed = true;
    }
    
    public Integer getGenderId() {
        return genderId;
    }
    
    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
        _changed = true;
    }
    
    public Integer getEthnicityId() {
        return ethnicityId;
    }
    
    public void setEthnicityId(Integer ethnicityId) {
        this.ethnicityId = ethnicityId;
        _changed = true;
    }
    
    public Integer getRaceId() {
        return raceId;
    }
    
    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
        _changed = true;
    }
    
    public String getNationalId() {
        return nationalId;
    }
    
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
        _changed = true;
    }
    
    public AddressDO getAddress() {
        return address;
    }
    
    public void setAddressDO(AddressDO address) {
        this.address = address;
        _changed = true;
    }
}
