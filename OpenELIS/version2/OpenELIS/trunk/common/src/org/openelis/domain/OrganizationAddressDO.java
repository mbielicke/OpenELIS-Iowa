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
