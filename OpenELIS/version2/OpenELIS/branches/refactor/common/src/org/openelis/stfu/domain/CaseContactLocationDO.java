package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import org.openelis.domain.DataObject;

public class CaseContactLocationDO extends DataObject {
	
	private static final long serialVersionUID = 1L;

	private Integer id,caseContactId,addressId;
	private String location;
	
	public CaseContactLocationDO() {
		
	}
	
	public CaseContactLocationDO(Integer id,Integer caseContactId,String location,Integer addressId) {
		setId(id);
		setCaseContactId(caseContactId);
		setLocation(location);
		setAddressId(addressId);
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}

	public Integer getCaseContactId() {
		return caseContactId;
	}

	public void setCaseContactId(Integer caseContactId) {
		this.caseContactId = caseContactId;
		_changed = true;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
		_changed = true;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = trim(location);
		_changed = true;
	}
}
