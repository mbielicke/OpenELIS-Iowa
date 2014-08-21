package org.openelis.entity;

import java.util.Date;

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
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_tag")
@EntityListeners({AuditUtil.class})
public class CaseTag implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "case_id")
    private Integer caseId;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "system_user_id")
    private Integer systemUserId;
    
    @Column(name = "created_date")
    private Date    createdDate;
    
    @Column(name = "reminder_date")
    private Date    reminderDate;
    
    @Column(name = "completed_date")
    private Date    completedDate;
    
    @Column(name = "note")
    private String  note;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", insertable = false, updatable = false)
    private Case _case;
    
    
    @Transient
    private CaseTag original;
    
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
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        if(DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }
    
    public Integer getSystemUserId() {
        return systemUserId;
    }
    
    public void setSystemUserId(Integer systemUserId) {
        if(DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }
    
    public Datetime getCreatedDate() {
        return DataBaseUtil.toYM(createdDate);
    }
    
    public void setCreatedDate(Datetime createdDate) {
        if(DataBaseUtil.isDifferent(createdDate, this.createdDate))
            this.createdDate = DataBaseUtil.toDate(createdDate);
    }
    
    public Datetime getRemiderDate() {
        return DataBaseUtil.toYM(reminderDate);
    }
    
    public void setReminderDate(Datetime reminderDate) {
        if(DataBaseUtil.isDifferent(reminderDate, this.reminderDate))
            this.reminderDate = DataBaseUtil.toDate(reminderDate);
    }
    
    public Datetime getCompletedDate() {
        return DataBaseUtil.toYM(completedDate);
    }
    
    public void setCompletedDate(Datetime completedDate) {
        if (DataBaseUtil.isDifferent(completedDate, this.completedDate))
            this.completedDate = DataBaseUtil.toDate(completedDate);
    }
    
    public String geNote() {
        return note;
    }
    
    public void setNote(String note) {
        if (DataBaseUtil.isDifferent(note, this.note))
            this.note = note;
    }
    @Override
    public void setClone() {
        try {
            original = (CaseTag)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_TAG);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("case_id", caseId, original.caseId, Constants.table().CASE)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("created_date", createdDate, original.createdDate)
                 .setField("reminderDate", reminderDate, original.reminderDate)
                 .setField("completedDate", completedDate, original.completedDate)
                 .setField("note", note, original.note);

        return audit;
    }

}
