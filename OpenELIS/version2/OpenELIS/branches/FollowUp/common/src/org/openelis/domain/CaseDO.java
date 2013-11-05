package org.openelis.domain;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class CaseDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer         id, genderId, raceId, ethnicityId, patientId;
    protected String          lastName, firstName, nationalId;
    protected Datetime        created, birthDate, birthTime;
    protected AddressDO       address;
    
    public CaseDO() {
        address = new AddressDO();
    }
    
    public CaseDO(Integer id, Date created, Integer patientId, String lastName, String firstName, Integer addressId, 
                  Date birthDate, Date birthTime, Integer genderId, Integer raceId, Integer enthnicityId, String nationalId,
                  String multipleUnit, String streetAddress, String city, String state, String zipCode,
                  String workPhone, String homePhone, String cellPhone, String faxPhone,
                  String email, String country) {
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setCreated(DataBaseUtil.toYM(created));
        setBirthDate(DataBaseUtil.toYD(birthDate));
        setBirthTime(DataBaseUtil.toHM(birthTime));
        setGenderId(genderId);
        setRaceId(raceId);
        setEthnicityId(ethnicityId);
        setNationalId(nationalId);
        
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
    
    public Datetime getCreated() {
        return created;
    }
    
    public void setCreated(Datetime created) {
        this.created = DataBaseUtil.toYM(created);
        _changed = true;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = DataBaseUtil.trim(lastName);
        _changed = true;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = DataBaseUtil.trim(firstName);
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
        this.birthTime = DataBaseUtil.toHM(birthTime);
        _changed = true;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
        _changed = true;
    }

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
        _changed = true;
    }

    public Integer getEthnicityId() {
        return ethnicityId;
    }

    public void setEthnicityId(Integer ethnicityId) {
        this.ethnicityId = ethnicityId;
        _changed = true;
    }
    
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = DataBaseUtil.trim(nationalId);
        _changed = true;
    }

    public AddressDO getAddress() {
        return address;
    }
}
