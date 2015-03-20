package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.*;

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
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
	@NamedQuery( name = "CaseUser.fetchById",
			     query = "select new org.openelis.stfu.domain.CaseUserDO(id,caseId,systemUserId,sectionId,actionId) " 
			             +"from CaseUser where id = :id"),
    @NamedQuery( name = "CaseUser.fetchByIds",
	    	     query = "select new org.openelis.stfu.domain.CaseUserDO(id,caseId,systemUserId,sectionId,actionId) " 
			             +"from CaseUser where id in (ids)"),	
	@NamedQuery( name = "CaseUser.fetchByCaseId",
	             query = "select new org.openelis.stfu.domain.CaseUserDO(id,caseId,systemUserId,sectionId,actionId) "
	                     + "from CaseUser where caseid = :id"),
	@NamedQuery( name = "CaseUser.fetchByCaseIds",
    		     query = "select new org.openelis.stfu.domain.CaseUserDO(id,caseId,systemUserId,sectionId,actionId) "
                         + "from CaseUser where caseid in (:ids)")
})

@Entity
@Table(name = "case_user")
@EntityListeners(value=AuditUtil.class)
public class CaseUser implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "case_id")
	private Integer caseId;
	
	@Column(name = "system_user_id")
	private Integer systemUserId;
	
	@Column(name = "section_id")
	private Integer sectionId;
	
	@Column(name = "action_id")
	private Integer actionId;
	
	@Transient
	private CaseUser original;

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

	public Integer getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(Integer systemUserId) {
		if(isDifferent(systemUserId,this.systemUserId))
			this.systemUserId = systemUserId;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		if(isDifferent(sectionId,this.sectionId))
			this.sectionId = sectionId;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		if(isDifferent(actionId,this.actionId))
			this.actionId = actionId;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseUser)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_USER);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("case_id", caseId, original.caseId, Constants.table().CASE)
        	     .setField("system_user_id", systemUserId, original.systemUserId)
        	     .setField("section_id", sectionId, original.sectionId, Constants.table().SECTION)
        	     .setField("action_id", actionId, original.actionId, Constants.table().DICTIONARY);
        }
		
        return audit;
	}

}
