/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class PatientDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          lastName;
    protected String          firstName;
    protected String          middleName;
    protected Integer         addressId;
    protected Datetime        birthDate;
    protected Datetime        birthTime;
    protected Integer         genderId;
    protected String          race;
    protected Integer         ethnicityId;

    public PatientDO() {

    }

    public PatientDO(Integer id,
                     String lastName,
                     String firstName,
                     String middleName,
                     Integer addressId,
                     Date birthDate,
                     Date birthTime,
                     Integer genderId,
                     String race,
                     Integer ethnicityId) {
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
