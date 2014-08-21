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
import org.openelis.security.entity.SystemUser;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_user")
@EntityListeners({AuditUtil.class})
public class CaseUser implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", insertable = false, updatable = false)
    private Case _case;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;
    
    @Transient
    private CaseUser  original;
    
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
    
    public Integer getSystemUserId() {
        return systemUserId;
    }
    
    public void setSystemUserId(Integer systemUserId) {
        if(DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }
    
    public Integer getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Integer sectionId) {
        if(DataBaseUtil.isDifferent(sectionId, this.sectionId))
            this.sectionId = sectionId;
    }
    
    public Section getSection() {
        return section;
    }
    
    public Integer getActionId() {
        return actionId;
    }
    
    public void setActionId(Integer actionId) {
        if(DataBaseUtil.isDifferent(actionId, this.actionId))
            this.actionId = actionId; 
    }
    
    @Override
    public void setClone() {
        try {
            original = (CaseUser)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_USER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("case_id", caseId, original.caseId, Constants.table().CASE)
                 .setField("system_user_id", systemUserId, original.systemUserId, Constants.table().CASE)
                 .setField("section_id", sectionId, original.sectionId, Constants.table().SECTION)
                 .setField("action_id", actionId, original.actionId, Constants.table().DICTIONARY);

        return audit;
    }


}
