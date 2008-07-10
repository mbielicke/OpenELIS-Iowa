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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
 package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;


public class OrganizationAddressDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4603524512208169740L;
	
	//organization fields
	protected Integer organizationId; 
	protected Integer parentOrganizationId;  
	protected String parentOrganization;
	protected String name;    
	protected String isActive; 
	
	//address fields
	protected AddressDO addressDO = new AddressDO();

	public OrganizationAddressDO(){
		
	}

	public OrganizationAddressDO(Integer organizationId, Integer parentOrganizationId, String parentOrganization, String name, String isActive,
			Integer addressId, String multipleUnit, String streetAddress, String city, String state, String zipCode,
			String country){
		
		setOrganizationId(organizationId);
		setParentOrganizationId(parentOrganizationId);
		setParentOrganization(parentOrganization);
		setName(name);
		setIsActive(isActive);
		addressDO.setId(addressId);
		addressDO.setMultipleUnit(multipleUnit);
		addressDO.setStreetAddress(streetAddress);
		addressDO.setCity(city);
		addressDO.setState(state);
		addressDO.setZipCode(zipCode);
		addressDO.setCountry(country);
		
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = DataBaseUtil.trim(isActive);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = DataBaseUtil.trim(name);
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getParentOrganizationId() {
		return parentOrganizationId;
	}

	public void setParentOrganizationId(Integer parentOrganizationId) {
		this.parentOrganizationId = parentOrganizationId;
	}

	public AddressDO getAddressDO() {
		return addressDO;
	}

	public String getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(String parentOrganization) {
		this.parentOrganization = DataBaseUtil.trim(parentOrganization);
	}
}
