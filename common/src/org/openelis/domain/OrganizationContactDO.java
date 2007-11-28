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
	protected Integer addressId; 
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
	
	protected Boolean delete;
	
	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public OrganizationContactDO() {

    }

    public OrganizationContactDO(Integer id, Integer organization, Integer contactType, String name, Integer addressId, Integer referenceId, Integer referenceTable, Integer type,
    							 String multipleUnit, String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone, String cellPhone, String faxPhone,
    							 String email, String country) {
    	this.id = id;
    	this.organization = organization;
    	this.contactType = contactType;
    	this.name = name;
    	this.addressId = addressId;
    	this.referenceId = referenceId;
    	this.referenceTable = referenceTable;
    	this.type = type;
    	this.multipleUnit = multipleUnit;
    	this.streetAddress = streetAddress;
    	this.city = city;
    	this.state = state;
    	this.zipCode = zipCode;
    	this.workPhone = workPhone;
    	this.homePhone = homePhone;
    	this.cellPhone = cellPhone;
    	this.faxPhone = faxPhone;
    	this.email = email;
    	this.country = country;
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

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFaxPhone() {
		return faxPhone;
	}

	public void setFaxPhone(String faxPhone) {
		this.faxPhone = faxPhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMultipleUnit() {
		return multipleUnit;
	}

	public void setMultipleUnit(String multipleUnit) {
		this.multipleUnit = multipleUnit;
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
		this.state = state;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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
		this.workPhone = workPhone;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
