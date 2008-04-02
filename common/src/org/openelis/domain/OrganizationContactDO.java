package org.openelis.domain;

import java.io.Serializable;

public class OrganizationContactDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3193069940053803677L;

	//contact fields
	protected Integer id;
	protected Integer organization;
	protected Integer contactType;  
	protected String name;
	
	//address fields
	protected AddressDO addressDO = new AddressDO();
	
	protected Boolean delete = false;
	
	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public OrganizationContactDO() {

    }

    public OrganizationContactDO(Integer id, Integer organization, Integer contactType, String name, Integer addressId, String multipleUnit, 
    		String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone, String cellPhone, String faxPhone,
    							 String email, String country) {
    	this.id = id;
    	this.organization = organization;
    	this.contactType = contactType;
    	this.name = name;
    	addressDO.setId(addressId);
    	addressDO.setMultipleUnit(multipleUnit);
    	addressDO.setStreetAddress(streetAddress);
    	addressDO.setCity(city);
    	addressDO.setState(state);
    	addressDO.setZipCode(zipCode);
    	addressDO.setWorkPhone(workPhone);
    	addressDO.setHomePhone(homePhone);
    	addressDO.setCellPhone(cellPhone);
    	addressDO.setFaxPhone(faxPhone);
    	addressDO.setEmail(email);
    	addressDO.setCountry(country);
    	
    }

	public Integer getContactType() {
		return contactType;
	}

	public void setContactType(Integer contactType) {
		this.contactType = contactType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrganization() {
		return organization;
	}

	public void setOrganization(Integer organization) {
		this.organization = organization;
	}

	public AddressDO getAddressDO() {
		return addressDO;
	}
}
