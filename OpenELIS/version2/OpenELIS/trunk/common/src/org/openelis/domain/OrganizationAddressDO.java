 package org.openelis.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OrganizationAddressDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4603524512208169740L;
	
	//organization fields
	protected Integer organizationId; 
	protected Integer parentOrganization;  
	protected String name;    
	protected String isActive; 
	
	//address fields
	protected AddressDO addressDO = new AddressDO();

	public OrganizationAddressDO(){
		
	}

	public OrganizationAddressDO(Integer organizationId, Integer parentOrganization, String name, String isActive,
			Integer addressId, String multipleUnit, String streetAddress, String city, String state, String zipCode,
			String country){
		
		this.organizationId = organizationId;
		this.parentOrganization = parentOrganization;
		this.name = name;
		this.isActive = isActive;
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
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(Integer parentOrganization) {
		this.parentOrganization = parentOrganization;
	}

	public AddressDO getAddressDO() {
		return addressDO;
	}
}
