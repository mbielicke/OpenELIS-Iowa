package org.openelis.stfu.domain;

import org.openelis.domain.DataObject;

public class CaseUserDO extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id,caseId,systemUserId,sectionId,actionId;
	
	public CaseUserDO() {
		
	}
	
	public CaseUserDO(Integer id, Integer caseId,Integer systemUserId,Integer sectionId,Integer actionId) {
		setId(id);
		setCaseId(caseId);
		setSystemUserId(systemUserId);
		setSectionId(sectionId);
		setActionId(actionId);
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}

	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
		_changed = true;
	}

	public Integer getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(Integer systemUserId) {
		this.systemUserId = systemUserId;
		_changed = true;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
		_changed = true;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
		_changed = true;
	}
}
