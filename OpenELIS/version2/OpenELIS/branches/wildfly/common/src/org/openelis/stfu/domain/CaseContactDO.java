package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import org.openelis.domain.DataObject;

public class CaseContactDO extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id,source,typeId;
	private String sourceReference,lastName,firstName,middleName,npi;
	
	public CaseContactDO() {
		
	}
	
	public CaseContactDO(Integer id,Integer source,String sourceReference,String lastName,
			             String firstName,String middleName,Integer typeId,String npi) {
		setId(id);
		setSource(source);
		setSourceReference(sourceReference);
		setLastName(lastName);
		setFirstName(firstName);
		setMiddleName(middleName);
		setTypeId(typeId);
		setNpi(npi);
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
		_changed = true;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
		_changed = true;
	}

	public String getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(String sourceReference) {
		this.sourceReference = trim(sourceReference);
		_changed = true;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = trim(lastName);
		_changed = true;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = trim(firstName);
		_changed = true;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = trim(middleName);
		_changed = true;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = trim(npi);
		_changed = true;
	}
}
