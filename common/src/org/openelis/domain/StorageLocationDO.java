package org.openelis.domain;

import java.io.Serializable;

public class StorageLocationDO implements Serializable{

	private static final long serialVersionUID = 6315788445035701792L;
	
	Integer id;
	Integer sortOrder;
	String name;
	String location;
	Integer parentStorageLocation;
	Integer storageUnit;
	String isAvailable;
	
	public StorageLocationDO(){
		
	}
	
	public StorageLocationDO(Integer id, Integer sortOrder, String name, String location, Integer parentStorageLocation,
						Integer storageUnit, String isAvailable){
		this.id = id;
		this.sortOrder = sortOrder;
		this.name = name;
		this.location = location;
		this.parentStorageLocation = parentStorageLocation;
		this.storageUnit = storageUnit;
		this.isAvailable = isAvailable;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(String isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getParentStorageLocation() {
		return parentStorageLocation;
	}
	public void setParentStorageLocation(Integer parentStorageLocation) {
		this.parentStorageLocation = parentStorageLocation;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Integer getStorageUnit() {
		return storageUnit;
	}
	public void setStorageUnit(Integer storageUnit) {
		this.storageUnit = storageUnit;
	}

}
