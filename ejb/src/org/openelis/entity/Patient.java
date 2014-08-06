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
package org.openelis.entity;

/**
 * Patient Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Patient.FetchById",
                query = "select new org.openelis.domain.PatientDO(p.id, p.lastName, p.firstName," +
                		"p.middleName, p.address.id, p.birthDate, p.birthTime, p.genderId, p.raceId," +
                		"p.ethnicityId, p.nationalId, p.address.multipleUnit, p.address.streetAddress," +
                        "p.address.city, p.address.state, p.address.zipCode, p.address.workPhone," +
                        "p.address.homePhone, p.address.cellPhone, p.address.faxPhone, p.address.email," +
                        "p.address.country)"
                      + " from Patient p where p.id = :id"),
     @NamedQuery(name = "Patient.FetchByIds",
                query = "select distinct new org.openelis.domain.PatientDO(p.id, p.lastName, p.firstName," +
                        "p.middleName, p.address.id, p.birthDate, p.birthTime, p.genderId, p.raceId," +
                        "p.ethnicityId, p.nationalId, p.address.multipleUnit, p.address.streetAddress," +
                        "p.address.city, p.address.state, p.address.zipCode, p.address.workPhone," +
                        "p.address.homePhone, p.address.cellPhone, p.address.faxPhone, p.address.email," +
                        "p.address.country)"
                      + " from Patient p where p.id in (:ids)"),
    @NamedQuery(name = "Patient.FetchByRelatedPatientId",
                query = "select new org.openelis.domain.PatientRelationVO(p.id, p.lastName, p.firstName," +
                        "p.middleName, p.address.id, p.birthDate, p.birthTime, p.genderId, p.raceId," +
                        "p.ethnicityId, p.nationalId, p.address.multipleUnit, p.address.streetAddress," +
                        "p.address.city, p.address.state, p.address.zipCode, p.address.workPhone," +
                        "p.address.homePhone, p.address.cellPhone, p.address.faxPhone, p.address.email," +
                        "p.address.country, pr.relationId)"
                      + " from Patient p, PatientRelation pr where pr.relatedPatientId = p.id and pr.patientId = :patientId")})
@Entity
@Table(name = "patient")
@EntityListeners( {AuditUtil.class})
public class Patient implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "last_name")
    private String  lastName;

    @Column(name = "first_name")
    private String  firstName;

    @Column(name = "middle_name")
    private String  middleName;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "birth_date")
    private Date    birthDate;

    @Column(name = "birth_time")
    private Date    birthTime;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "race_id")
    private Integer raceId;

    @Column(name = "ethnicity_id")
    private Integer ethnicityId;
    
    @Column(name = "national_id")
    private String nationalId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;

    @Transient
    private Patient original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (DataBaseUtil.isDifferent(lastName, this.lastName))
            this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (DataBaseUtil.isDifferent(firstName, this.firstName))
            this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        if (DataBaseUtil.isDifferent(middleName, this.middleName))
            this.middleName = middleName;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if ( DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }

    public Datetime getBirthDate() {
        return DataBaseUtil.toYD(birthDate);
    }

    public void setBirthDate(Datetime birthDate) {
        if (DataBaseUtil.isDifferentYD(birthDate, this.birthDate))
            this.birthDate = DataBaseUtil.toDate(birthDate);
    }

    public Datetime getBirthTime() {        
        return DataBaseUtil.toHM(birthTime);
    }

    public void setBirthTime(Datetime birthTime) {
        if (DataBaseUtil.isDifferentHM(birthTime, this.birthTime))
            this.birthTime = DataBaseUtil.toDate(birthTime);
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        if (DataBaseUtil.isDifferent(genderId, this.genderId))
            this.genderId = genderId;
    }

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        if (DataBaseUtil.isDifferent(raceId, this.raceId))
            this.raceId = raceId;
    }

    public Integer getEthnicityId() {
        return ethnicityId;
    }

    public void setEthnicityId(Integer ethnicityId) {
        if (DataBaseUtil.isDifferent(ethnicityId, this.ethnicityId))
            this.ethnicityId = ethnicityId;
    }
    
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        if (DataBaseUtil.isDifferent(nationalId, this.nationalId))
            this.nationalId = nationalId;
    }

    public Address getAddress() {
        return address;
    }
    
    public void setClone() {
        try {
            original = (Patient)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PATIENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("last_name", lastName, original.lastName)
                 .setField("first_name", firstName, original.firstName)
                 .setField("middle_name", middleName, original.middleName)
                 .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS)
                 .setField("birth_date", birthDate, original.birthDate)
                 .setField("birth_time", birthTime, original.birthTime)
                 .setField("gender_id", genderId, original.genderId, Constants.table().DICTIONARY)
                 .setField("race_id", raceId, original.raceId, Constants.table().DICTIONARY)
                 .setField("ethnicity_id", ethnicityId, original.ethnicityId, Constants.table().DICTIONARY)
                 .setField("nationalId", nationalId, original.nationalId);

        return audit;
    }
}