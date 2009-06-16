package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.gwt.common.RPC;
import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class PatientDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String lastName;
    protected String firstName;
    protected String middleName;
    protected Integer addressId;
    protected Datetime birthDate;
    protected Datetime birthTime;
    protected Integer genderId;
    protected String race;
    protected Integer ethnicityId;
    
    public PatientDO(){
        
    }
    
    public PatientDO(Integer id, String lastName, String firstName, String middleName, Integer addressId,
                     Date birthDate, Date birthTime, Integer genderId, String race, Integer ethnicityId){
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);
        setAddressId(addressId);
        setBirthDate(birthDate);
        setBirthTime(birthTime);
        setGenderId(genderId);
        setRace(race);
        setEthnicityId(ethnicityId);
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLastName() {
        return DataBaseUtil.trim(lastName);
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = DataBaseUtil.trim(firstName);
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = DataBaseUtil.trim(middleName);
    }
    public Integer getAddressId() {
        return addressId;
    }
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    public Datetime getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = new Datetime(Datetime.YEAR, Datetime.SECOND, birthDate);
    }
    public Datetime getBirthTime() {
        return birthTime;
    }
    public void setBirthTime(Date birthTime) {
        this.birthTime = new Datetime(Datetime.HOUR, Datetime.MINUTE, birthTime);
    }
    public Integer getGenderId() {
        return genderId;
    }
    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }
    public String getRace() {
        return DataBaseUtil.trim(race);
    }
    public void setRace(String race) {
        this.race = race;
    }
    public Integer getEthnicityId() {
        return ethnicityId;
    }
    public void setEthnicityId(Integer ethnicityId) {
        this.ethnicityId = ethnicityId;
    }
}
