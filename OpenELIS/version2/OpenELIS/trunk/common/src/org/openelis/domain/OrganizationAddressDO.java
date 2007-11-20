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
	protected String parentOrganizationName;
	protected String name;    
	protected String isActive; 
	
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
	
	//contacts list
	protected List contacts = new ArrayList();
	
	public OrganizationAddressDO(){
		
	}
	
	public OrganizationAddressDO(Integer organizationId, Integer parentOrganization, String parentOrganizationName, String name, String isActive,
			Integer addressId, Integer referenceId, Integer referenceTable, Integer type, String multipleUnit, 
			String streetAddress, String city, String state, String zipCode, String workPhone, String homePhone,
			String cellPhone, String faxPhone, String email, String country){
		
		this.organizationId = organizationId;
		this.parentOrganization = parentOrganization;
		this.parentOrganizationName = parentOrganizationName;
		this.name = name;
		this.isActive = isActive;
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
		//System.out.println("(1).................["+contacts.getClass()+"].................");
		//System.out.println("(2).................["+email.getClass()+"]...................");
		//List<OrganizationContact> list = (List<OrganizationContact>) contacts;
		//this.contacts = contacts;		
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

	public List getContacts() {
		return contacts;
	}

	public void setContacts(List contacts) {
		this.contacts = contacts;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getMultipleUnit() {
		return multipleUnit;
	}

	public void setMultipleUnit(String multipleUnit) {
		this.multipleUnit = multipleUnit;
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

	public String getParentOrganizationName() {
		return parentOrganizationName;
	}

	public void setParentOrganizationName(String parentOrganizationName) {
		this.parentOrganizationName = parentOrganizationName;
	}

}
