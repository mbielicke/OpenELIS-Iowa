package org.openelis.domain;

import java.io.Serializable;

public class OrganizationDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5149465794756622618L;

	protected Integer id; 
	protected Integer parentOrganization;  
	protected String name;    
	protected String isActive; 
	 
	public OrganizationDO() {

    }

    public OrganizationDO(Integer id, Integer parentOrganization, String name, String isActive) {
        this.id = id;
        this.parentOrganization = parentOrganization;
        this.name = name;
        this.isActive = isActive;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(Integer parentOrganization) {
		this.parentOrganization = parentOrganization;
	}
}
