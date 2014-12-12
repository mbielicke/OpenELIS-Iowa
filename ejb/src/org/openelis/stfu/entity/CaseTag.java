package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
	@NamedQuery( name = "CaseTag.fetchById",
			     query = "select new org.openelis.stfu.domain.CaseTagDO(id,caseId,typeId,systemUserId,createdDate,"
                         +"remindDate,completedDate,note) from CaseTag where id = :id"),
    @NamedQuery( name = "CaseTag.fetchByIds",
    		     query = "select new org.openelis.stfu.domain.CaseTagDO(id,caseId,typeId,systemUserId,createdDate,"
                         +"remindDate,completedDate,note) from CaseTag where id in (:ids)"),
	@NamedQuery( name = "CaseTag.fetchByCaseId",
    			 query = "select new org.openelis.stfu.domain.CaseTagDO(id,caseId,typeId,systemUserId,createdDate,"
    					 +"remindDate,completedDate,note) from CaseTag where caseId = :id"),
    @NamedQuery( name = "CaseTag.fetchByCaseIds",
                 query = "select new org.openelis.stfu.domain.CaseTagDO(id,caseId,typeId,systemUserId,createdDate,"
                         +"remindDate,completedDate,note) from CaseTag where caseId in (:ids)")	
})

@Entity
@Table(name = "case_tag")
@EntityListeners(value = AuditUtil.class)
public class CaseTag implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "case_id")
	private Integer caseId;
	
	@Column(name = "type_id")
	private Integer typeId;
	
	@Column(name = "system_user_id")
	private Integer systemUserId;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "remind_date")
	private Date remindDate;
	
	@Column(name = "completed_date")
	private Date completedDate;
	
	@Column(name = "note")
	private String note;
	
	@Transient
	private CaseTag original;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(isDifferent(id,this.id))
			this.id = id;
	}

	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		if(isDifferent(caseId,this.caseId))
			this.caseId = caseId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeID(Integer typeId) {
		if(isDifferent(typeId,this.typeId))
			this.typeId = typeId;
	}

	public Integer getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(Integer systemUserId) {
		if(isDifferent(systemUserId,this.systemUserId))
			this.systemUserId = systemUserId;
	}

	public Datetime getCreateDate() {
		return toYM(createdDate);
	}

	public void setCreateDate(Datetime createdDate) {
		if(isDifferentYM(createdDate,this.createdDate))
			this.createdDate = toDate(createdDate);
	}

	public Datetime getRemindDate() {
		return toYM(remindDate);
	}

	public void setRemindDate(Datetime remindDate) {
		if(isDifferentYM(remindDate,this.remindDate))
			this.remindDate = toDate(remindDate);
	}

	public Datetime getCompletedDate() {
		return toYM(completedDate);
	}

	public void setCompletedDate(Datetime completedDate) {
		if(isDifferentYM(completedDate,this.completedDate))
			this.completedDate = toDate(completedDate);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		if(isDifferent(note,this.note))
			this.note = note;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseTag)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_TAG);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("case_id", caseId, original.caseId, Constants.table().CASE)
        	     .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
        	     .setField("system_user_id", systemUserId, original.systemUserId)
        	     .setField("created_date", createdDate, original.createdDate)
        	     .setField("remind_date", remindDate, original.remindDate)
        	     .setField("completed_date", completedDate, original.completedDate)
        	     .setField("note", note, original.note);
        }
		
        return audit;
	}

}
