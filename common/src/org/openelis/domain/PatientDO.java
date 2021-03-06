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

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class PatientDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, genderId, raceId, ethnicityId;
    protected String          lastName, firstName, middleName, nationalId;
    protected Datetime        birthDate, birthTime;
    protected AddressDO       address;

    public PatientDO() {
        address = new AddressDO();
    }

    public PatientDO(Integer id, String lastName, String firstName, String middleName,
                     Integer addressId, Date birthDate, Date birthTime, Integer genderId,
                     Integer raceId, Integer ethnicityId, String nationalId, String multipleUnit,
                     String streetAddress, String city, String state, String zipCode,
                     String workPhone, String homePhone, String cellPhone, String faxPhone,
                     String email, String country) {
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = DataBaseUtil.trim(middleName);
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