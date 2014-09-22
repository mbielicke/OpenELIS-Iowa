package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CasePatientDO extends DataObject {
	
	private static final long serialVersionUID = 1L;

	private Integer id,addressId,genderId,raceId,ethnicityId;
	private String lastName,firstName,maidenName,nationalId;
	private Datetime birthDate,birthTime;
	
	public CasePatientDO() {
		
	}
	
	public CasePatientDO(Integer id,String lastName,String firstName,String maidenName,Integer addressId,
			             Date birthDate, Date birthTime, Integer genderId, Integer raceId,
			             Integer ethnicityId, String nationalId) {
		setId(id);
		setLastName(lastName);
		setFirstName(firstName);
		setMaidenName(maidenName);
		setAddressId(addressId);
		setBirthDate(toYD(birthDate));
		setBirthTime(toHM(birthTime));
		setGenderId(genderId);
		setRaceId(raceId);
		setEthnicityId(ethnicityId);
		setNationalId(nationalId);
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = trim(lastName);
		_changed = true;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = trim(firstName);
		_changed = true;
	}
	
	public String getMaidenName() {
		return maidenName;
	}
	
	public void setMaidenName(String maidenName) {
		this.maidenName = trim(maidenName);
		_changed = true;
	}

	public Datetime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Datetime birthDate) {
		this.birthDate = toYD(birthDate);
		_changed = true;
	}

	public Datetime getBirthTime() {
		return birthTime;
	}

	public void setBirthTime(Datetime birthTime) {
		this.birthTime = toHM(birthTime);
		_changed = true;
	}
	
	public String getNationalId() {
		return nationalId;
	}
	
	public void setNationalId(String nationalId) {
		this.nationalId = trim(nationalId);
		_changed = true;
	}
	
}
