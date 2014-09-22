package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CaseAnalysisDO extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id,caseId,organizationId,testId,statusId,conditionId;
	private String accession;
	private Datetime collectionDate,completedDate;
	
	public CaseAnalysisDO() {
		
	}
	
	public CaseAnalysisDO(Integer id, Integer caseId, String accession, Integer organizationId,Integer testId,
			              Integer statusId,Date collectionDate, Date completedDate,Integer conditionId) {
		setId(id);
		setCaseId(caseId);
		setAccession(accession);
		setOrganizationId(organizationId);
		setTestId(testId);
		setStatusId(statusId);
		setCollectionDate(toYM(collectionDate));
		setCompletedDate(toYM(completedDate));
		setConditionId(conditionId);
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
	
	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
		_changed = true;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
		_changed = true;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
		_changed = true;
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
		_changed = true;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = trim(accession);
		_changed = true;
	}

	public Datetime getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Datetime collectionDate) {
		this.collectionDate = toYM(collectionDate);
		_changed = true;
	}

	public Datetime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Datetime completedDate) {
		this.completedDate = toYM(completedDate);
		_changed = true;
	}
	
}
