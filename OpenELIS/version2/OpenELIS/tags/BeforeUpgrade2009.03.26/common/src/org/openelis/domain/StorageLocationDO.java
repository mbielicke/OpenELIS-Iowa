/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
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
