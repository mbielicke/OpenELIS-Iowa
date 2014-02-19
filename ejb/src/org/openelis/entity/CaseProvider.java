package org.openelis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_provider")
@EntityListeners({AuditUtil.class})
public class CaseProvider implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "case_id")
    private Integer caseId;
    
    @Column(name = "case_contact_id")
    private Integer caseContactId;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", insertable = false, updatable = false)
    private Case _case;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_contact_id", insertable = false, updatable = false)
    private CaseContact caseContact;
    
    @Transient
    private CaseProvider original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getCaseId() {
        return caseId;
    }
    
    public void setCaseId(Integer caseId) {
        if(DataBaseUtil.isDifferent(caseId, this.caseId))
            this.caseId = caseId;
    }
    
    public Case getCase() {
        return _case;
    }
    
    public Integer getCaseContactId() {
        return caseContactId;
    }
    
    public void setCaseContactId(Integer caseContactId) {
        if(DataBaseUtil.isDifferent(caseContactId, this.caseContactId))
            this.caseContactId = caseContactId;
    }
    
    public CaseContact getCaseContact() {
        return caseContact;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        if(DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }
    
    @Override
    public void setClone() {
        try {
            original = (CaseProvider)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_PROVIDER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("case_id", caseId, original.caseId, Constants.table().CASE)
                 .setField("case_contact_id", caseContactId, original.caseContactId, Constants.table().CASE_CONTACT)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY);

        return audit;
    }
}
