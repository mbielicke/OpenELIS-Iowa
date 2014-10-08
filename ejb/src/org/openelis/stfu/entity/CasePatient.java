package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
	@NamedQuery( name = "CasePatient.fetchById",
			     query = "select new org.openelis.stfu.domain.CasePatientDO(id,lastName,firstName,maidenName,addressId,birthDate,"
			             +"birthTime,genderId,raceId,ethnicityId,nationalId) from CasePatient where id = :id"),
    @NamedQuery( name = "CasePatient.fetchByIds",
	    	     query = "select new org.openelis.stfu.domain.CasePatientDO(id,lastName,firstName,maidenName,addressId,birthDate,"
		                 +"birthTime,genderId,raceId,ethnicityId,nationalId) from CasePatient where id in (:ids)")			             
})
@Entity
@Table(name = "case_patient")
@EntityListeners({AuditUtil.class})
public class CasePatient implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer                     id;
	
	@Column(name = "last_name")
	private String                      lastName;
	
	@Column(name = "first_name")
	private String                      firstName;
	
	@Column(name = "maiden_name")       
	private String                      maidenName; 
	
	@Column(name = "address_id")    
	private Integer                     addressId;
	
	@Column(name = "birth_date")
	private Date                        birthDate;
	
	@Column(name = "birth_time")
	private Date                        birthTime;
	
	@Column(name = "gender_id")
	private Integer                     genderId;
	
	@Column(name = "race_id")
	private Integer                     raceId;
	
	@Column(name = "ethnicity_id")      
	private Integer                     ethnicityId;
	
	@Column(name = "national_id") 
	private String                      nationalId;
	
	@Transient
	private CasePatient original;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(isDifferent(id,this.id))
			this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if(isDifferent(lastName, this.lastName))
			this.lastName = trim(lastName);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if(isDifferent(firstName,this.firstName))
			this.firstName = trim(firstName);
	}

	public String getMaidenName() {
		return maidenName;
	}
	
	public void setMaidenName(String maidenName) {
		if(isDifferent(maidenName,this.maidenName))
			this.maidenName = trim(maidenName);
	}
	
	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		if(isDifferent(addressId,this.addressId))
			this.addressId = addressId;
	}

	public Datetime getBirthDate() {
		return toYD(birthDate);
	}

	public void setBirthDate(Datetime birthDate) {
		if(isDifferentYD(birthDate,this.birthDate))
			this.birthDate = toDate(birthDate);
	}

	public Datetime getBirthTime() {
		return toHM(birthTime);
	}

	public void setBirthTime(Datetime birthTime) {
		if(isDifferentHM(birthTime,this.birthTime))
		this.birthTime = toDate(birthTime);
	}

	public Integer getGenderId() {
		return genderId;
	}

	public void setGenderId(Integer genderId) {
		if(isDifferent(genderId,this.genderId))
			this.genderId = genderId;
	}

	public Integer getRaceId() {
		return raceId;
	}

	public void setRaceId(Integer raceId) {
		if(isDifferent(raceId,this.raceId))
			this.raceId = raceId;
	}

	public Integer getEthnicityId() {
		return ethnicityId;
	}

	public void setEthnicityId(Integer ethnicityId) {
		if(isDifferent(ethnicityId, this.ethnicityId))
			this.ethnicityId = ethnicityId;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		if(isDifferent(nationalId,this.nationalId))
			this.nationalId = nationalId;
	}

	@Override
	public void setClone() {
		try {
			original = (CasePatient)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_PATIENT);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("last_name", lastName, original.lastName)
        	     .setField("first_name", firstName, original.firstName)
        	     .setField("maiden_name", maidenName, original.maidenName)
        	     .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS)
        	     .setField("birth_date", birthDate, original.birthDate)
        	     .setField("gender_id", genderId, original.genderId, Constants.table().DICTIONARY)
        	     .setField("race_id", raceId, original.raceId, Constants.table().DICTIONARY)
        	     .setField("ethnicity_id", ethnicityId, original.ethnicityId, Constants.table().DICTIONARY)
        	     .setField("national_id", nationalId, original.nationalId);		
        }
        
		return audit;
	}
	

}
