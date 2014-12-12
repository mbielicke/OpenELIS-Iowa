package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CaseTagDO extends DataObject {
	
	private static final long serialVersionUID = 1L;

	private Integer id,caseId,typeId,systemUserId;
	private Datetime createdDate,reminderDate,completedDate;
	private String note;
	
	public CaseTagDO() {
		
	}
	
	public CaseTagDO(Integer id,Integer caseId,Integer typeId,Integer SystemUserId,
			         Date createdDate,Date reminderDate, Date completedDate, String note) {
		setId(id);
		setCaseId(caseId);
		setTypeId(typeId);
		setSystemUserId(systemUserId);
		setCreatedDate(toYM(createdDate));
		setReminderDate(toYM(reminderDate));
		setCompletedDate(toYM(completedDate));
		setNote(note);
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
		_changed = true;
	}

	public Integer getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(Integer systemUserId) {
		this.systemUserId = systemUserId;
		_changed = true;
	}

	public Datetime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Datetime createdDate) {
		this.createdDate = toYM(createdDate);
		_changed = true;
	}

	public Datetime getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Datetime reminderDate) {
		this.reminderDate = toYM(reminderDate);
		_changed = true;
	}

	public Datetime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Datetime completedDate) {
		this.completedDate = toYM(completedDate);
		_changed = true;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = trim(note);
		_changed = true;
	}
}
