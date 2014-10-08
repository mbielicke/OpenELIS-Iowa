package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CaseDO extends DataObject {

	private static final long serialVersionUID = 1L;
	
	private Integer id,patientId,nextkinId,casePatientId,caseNextkinId,organizationId;
	private Datetime created,completeDate;
	private String isFinalized;
	
	public CaseDO() {
		
	}
	
	public CaseDO(Integer id,Date created,Integer patientId,Integer nextkinId,
			      Integer casePateintId,Integer caseNextkinId,Integer organizationId,
			      Date completeDate, String isFinalized) {
		setId(id);
		setCreated(toYM(created));
		setPatientId(patientId);
		setNextkinId(nextkinId);
		setCasePatientId(casePatientId);
		setCaseNextkinId(caseNextkinId);
		setOrganizationId(organizationId);
		setCompleteDate(toYM(completeDate));
		setIsFinalized(isFinalized);
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
		_changed = true;
	}

	public Integer getNextkinId() {
		return nextkinId;
	}

	public void setNextkinId(Integer nextkinId) {
		this.nextkinId = nextkinId;
		_changed = true;
	}

	public Integer getCasePatientId() {
		return casePatientId;
	}

	public void setCasePatientId(Integer casePatientId) {
		this.casePatientId = casePatientId;
		_changed = true;
	}

	public Integer getCaseNextkinId() {
		return caseNextkinId;
	}
	
	public void setCaseNextkinId(Integer caseNextkinId) {
		this.caseNextkinId = caseNextkinId;
		_changed = true;
	}
	
	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
		_changed = true;
	}

	public Datetime getCreated() {
		return created;
	}

	public void setCreated(Datetime created) {
		this.created = created;
		_changed = true;
	}

	public Datetime getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Datetime completeDate) {
		this.completeDate = completeDate;
		_changed = true;
	}

	public String getIsFinalized() {
		return isFinalized;
	}

	public void setIsFinalized(String isFinalized) {
		this.isFinalized = isFinalized;
		_changed = true;
	}

}
