/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class AddressDO implements Serializable{

	private static final long serialVersionUID = -6675858186327436797L;

	protected Integer id; 
	protected Integer referenceId;   
	protected Integer referenceTable; 
	protected Integer type; 
	protected String multipleUnit;  
	protected String streetAddress;
	protected String city;  
	protected String state;   
	protected String zipCode; 
	protected String workPhone;
	protected String homePhone; 
	protected String cellPhone; 
	protected String faxPhone;   
	protected String email;        
	protected String country; 
	
	
	public AddressDO() {

    }

    public AddressDO(Integer id, Integer referenceId, Integer referenceTable, Integer type, String multipleUnit,
    		String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone, 
    		String cellPhone, String faxPhone, String email, String country) {
        setId(id);
        setReferenceId(referenceId);
        setReferenceTable(referenceTable);
        setType(type);
        setMultipleUnit(multipleUnit);
        setStreetAddress(streetAddress);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setWorkPhone(workPhone);
        setHomePhone(homePhone);
        setCellPhone(cellPhone);
        setFaxPhone(faxPhone);
        setEmail(email);
        setCountry(country);
    }

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = DataBaseUtil.trim(cellPhone);
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = DataBaseUtil.trim(city);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = DataBaseUtil.trim(country);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = DataBaseUtil.trim(email);
	}

	public String getFaxPhone() {
		return faxPhone;
	}

	public void setFaxPhone(String faxPhone) {
		this.faxPhone = DataBaseUtil.trim(faxPhone);
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = DataBaseUtil.trim(homePhone);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMultipleUnit() {
		return multipleUnit;
	}

	public void setMultipleUnit(String multipleUnit) {
		this.multipleUnit = DataBaseUtil.trim(multipleUnit);
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public Integer getReferenceTable() {
		return referenceTable;
	}

	public void setReferenceTable(Integer referenceTable) {
		this.referenceTable = referenceTable;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = DataBaseUtil.trim(state);
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = DataBaseUtil.trim(streetAddress);
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = DataBaseUtil.trim(workPhone);
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = DataBaseUtil.trim(zipCode);
	}
}
