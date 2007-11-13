package org.openelis.domain;

import java.io.Serializable;

public class OrganizationTableRowDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4643119242220623967L;

	protected Integer id;   
	protected String name;
	
	public OrganizationTableRowDO(){
		
	}
	
	public OrganizationTableRowDO(Integer id, String name){
		this.id = id;
		this.name = name;
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
}
