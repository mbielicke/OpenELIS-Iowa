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
	@NamedQuery( name = "CaseProvider.fetchById",
			     query = "select new org.openelis.common.stfu.domain(id,caseId,caseContactId,typeId) "
			             +"from CaseProvider where id = :id"),
	@NamedQuery( name = "CaseProvider.fetchByIds",
	     	     query = "select new org.openelis.common.stfu.domain(id,caseId,caseContactId,typeId) "
		                 +"from CaseProvider where id in(:ids)"),
	@NamedQuery( name = "CaseProvider.fetchByCaseId",
                 query = "select new org.openelis.common.stfu.domain(id,caseId,caseContactId,typeId) "
                         +"from CaseProvider where caseId = :id"),
    @NamedQuery( name = "CaseProvider.fetchByCaseIds",
                 query = "select new org.openelis.common.stfu.domain(id,caseId,caseContactId,typeId) "
                         +"from CaseProvider where caseId in(:ids)")
})

@Entity
@Table(name = "case_provider")
@EntityListeners(value=AuditUtil.class)
public class CaseProvider implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "case_id")
	private Integer caseId;
	
	@Column(name = "case_contact_id")
	private Integer caseContactId;
	
	@Column(name = "type_id")
	private Integer typeId;
	
	@Transient
	private CaseProvider original;
	
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

	public Integer getCaseContactId() {
		return caseContactId;
	}

	public void setCaseContactId(Integer caseContactId) {
		if(isDifferent(caseContactId,this.caseContactId))
			this.caseContactId = caseContactId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		if(isDifferent(typeId,this.typeId))
			this.typeId = typeId;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseProvider)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_PROVIDER);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id",id,original.id)
        	     .setField("case_id", caseId, original.caseId, Constants.table().CASE)
        	     .setField("case_contact_id", caseContactId, original.caseContactId, Constants.table().CASE_CONTACT)
        	     .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY);
        }
		
        return audit;
	}

}
