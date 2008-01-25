package org.openelis.domain;

import java.io.Serializable;

public class StorageUnitDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -896897119790254351L;

	protected Integer id; 
	protected String category; 
	protected String description;  
	protected String isSingular; 
	 
	public StorageUnitDO() {

    }

    public StorageUnitDO(Integer id, String category, String description, String isSingular) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.isSingular = isSingular;
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIsSingular() {
		return isSingular;
	}

	public void setIsSingular(String isSingular) {
		this.isSingular = isSingular;
	}
}
