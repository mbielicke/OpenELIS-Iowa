package org.openelis.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case")
@EntityListeners({AuditUtil.class})
public class Case implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created")
    private Date    created;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "last_name")
    private String  lastName;

    @Column(name = "first_name")
    private String  firstName;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "birth_date")
    private Date    birthDate;

    @Column(name = "birth_time")
    private Date    birthTime;

    @Column(name = "race_id")
    private Integer raceId;

    @Column(name = "ethnicity_id")
    private Integer ethnicityId;

    @Column(name = "national_id")
    private String  nationalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @Transient
    private Case    original;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Datetime getCreated() {
        return DataBaseUtil.toYM(created);
    }

    public void setCreated(Datetime created) {
        if (DataBaseUtil.isDifferent(created, this.created))
            this.created = DataBaseUtil.toDate(created);
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        if (DataBaseUtil.isDifferent(patientId, this.patientId))
            this.patientId = patientId;
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

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }

    public Datetime getBirthDate() {
        return DataBaseUtil.toYD(birthDate);
    }

    public void setBirthDate(Datetime birthDate) {
        if (DataBaseUtil.isDifferent(birthDate, this.birthDate))
            this.birthDate = DataBaseUtil.toDate(birthDate);
    }

    public Datetime getBirthTime() {
        return DataBaseUtil.toYM(birthTime);
    }

    public void setBirthTime(Datetime birthTime) {
        if (DataBaseUtil.isDifferent(birthTime, this.birthTime))
            this.birthTime = DataBaseUtil.toDate(birthTime);
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

    public Patient getPatient() {
        return patient;
    }

    @Override
    public void setClone() {
        try {
            original = (Case)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("last_name", lastName, original.lastName)
                 .setField("first_name", firstName, original.firstName)
                 .setField("created", created, original.created)
                 .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS)
                 .setField("birth_date", birthDate, original.birthDate)
                 .setField("birth_time", birthTime, original.birthTime)
                 .setField("patient_id", patientId, original.patientId, Constants.table().DICTIONARY)
                 .setField("race_id", raceId, original.raceId, Constants.table().DICTIONARY)
                 .setField("ethnicity_id", ethnicityId, original.ethnicityId, Constants.table().DICTIONARY)
                 .setField("nationalId", nationalId, nationalId);

        return audit;
    }

}
