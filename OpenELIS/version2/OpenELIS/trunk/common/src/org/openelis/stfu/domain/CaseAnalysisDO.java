package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CaseAnalysisDO extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id,caseSampleId,testId,statusId,conditionId;
	private Datetime completedDate;
	
	public CaseAnalysisDO() {
		
	}
	
	public CaseAnalysisDO(Integer id, Integer caseSampleId, Integer testId, Integer statusId, Date completedDate,Integer conditionId) {
		setId(id);
		setCaseSampleId(caseSampleId);
		setTestId(testId);
		setStatusId(statusId);
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

	public Integer getCaseSampleId() {
		return caseSampleId;
	}
	
	public void setCaseSampleId(Integer caseSampleId) {
		this.caseSampleId = caseSampleId;
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

	public Datetime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Datetime completedDate) {
		this.completedDate = toYM(completedDate);
		_changed = true;
	}
	
}
