package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class StorageLocationDO implements Serializable{

	private static final long serialVersionUID = 6315788445035701792L;
	
	protected Integer id;
	protected Integer sortOrder;
	protected String name;
	protected String location;
	protected Integer parentStorageLocationId;
	protected Integer storageUnitId;
	protected String storageUnit;
	protected String isAvailable;
	
	protected Boolean delete = false;

	public StorageLocationDO(){
		
	}
	
	public StorageLocationDO(Integer id, Integer sortOrder, String name, String location, Integer parentStorageLocationId, Integer storageUnitId, String storageUnit, String isAvailable){
		setId(id);
		setSortOrder(sortOrder);
		setName(name);
		setLocation(location);
		setParentStorageLocationId(parentStorageLocationId);
		setStorageUnitId(storageUnitId);
		setStorageUnit(storageUnit);
		setIsAvailable(isAvailable);
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
		this.isAvailable = DataBaseUtil.trim(isAvailable);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = DataBaseUtil.trim(location);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = DataBaseUtil.trim(name);
	}
	public Integer getParentStorageLocationId() {
		return parentStorageLocationId;
	}
	public void setParentStorageLocationId(Integer parentStorageLocationId) {
		this.parentStorageLocationId = parentStorageLocationId;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Integer getStorageUnitId() {
		return storageUnitId;
	}
	public void setStorageUnitId(Integer storageUnitId) {
		this.storageUnitId = storageUnitId;
	}

	public String getStorageUnit() {
		return storageUnit;
	}

	public void setStorageUnit(String storageUnit) {
		this.storageUnit = DataBaseUtil.trim(storageUnit);
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
}
