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
@Table(name = "case")
@EntityListeners({AuditUtil.class})
public class Case implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created")
    private Date    created;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "nextkin_id")
    private Integer nextkinId;

    @Column(name = "case_patient_id")
    private Integer casePatientId;

    @Column(name = "case_nextkin_id")
    private Integer caseNextkinId;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "complete_date")
    private Date    completeDate;

    @Column(name = "is_finalized")
    private String  isFinalized;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextkin_id", insertable = false, updatable = false)
    private Patient nextkin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_nextkin_id", insertable = false, updatable = false)
    private CasePatient caseNextkin;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_patient_id", insertable = false, updatable = false)
    private CasePatient casePatient;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;
    
    @Transient
    private Case    original;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Datetime getCreated() {
        return DataBaseUtil.toYM(created);
    }

    public void setCreated(Datetime created) {
        if (DataBaseUtil.isDifferent(created, this.created))
            this.created = DataBaseUtil.toDate(created);
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        if (DataBaseUtil.isDifferent(patientId, this.patientId))
            this.patientId = patientId;
    }

    public Patient getPatient() {
        return patient;
    }
    
    public Integer getNextkinId() {
        return nextkinId;
    }
    
    public void setNextkinId(Integer nextkinId) {
        if(DataBaseUtil.isDifferent(nextkinId, this.nextkinId))
            this.nextkinId = nextkinId;
    }
    
    public Patient getNextKin() {
        return nextkin;
    }
    
    public Integer getCasePatientId() {
        return casePatientId;
    }
    
    public void setCasePatientId(Integer casePatientId) {
        if(DataBaseUtil.isDifferent(casePatientId, this.casePatientId))
            this.casePatientId = casePatientId;
    }
    
    public CasePatient getCasePatient() {
        return casePatient;
    }
    
    public Integer getCaseNextkinId() {
        return caseNextkinId;
    }
    
    public void setCaseNextkinId(Integer caseNextkinId) {
        if(DataBaseUtil.isDifferent(caseNextkinId, this.caseNextkinId))
            this.caseNextkinId = caseNextkinId;
    }
    
    public CasePatient getCaseNextkin() {
        return caseNextkin;
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Integer organizationId) {
        if(DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }
    
    public Organization getOrganization() {
        return organization;
    }
    
    public Datetime getCompleteDate() {
        return DataBaseUtil.toYM(completeDate);
    }

    public void setCompleteDate(Datetime completeDate) {
        if (DataBaseUtil.isDifferent(completeDate, this.completeDate))
            this.completeDate = DataBaseUtil.toDate(completeDate);
    }
    
    public String isFinalized() {
        return isFinalized;
    }
    
    public void setIsFinalized(String isFinalized) {
        if(DataBaseUtil.isDifferent(isFinalized, this.isFinalized))
            this.isFinalized = isFinalized;
    }
    

    @Override
    public void setClone() {
        try {
            original = (Case)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("created", created, original.created)
                 .setField("patient_id", patientId, original.patientId, Constants.table().PATIENT)
                 .setField("nextkin_id", nextkinId, original.nextkinId, Constants.table().PATIENT)
                 .setField("case_pateint_id", casePatientId, original.casePatientId, Constants.table().CASE_PATIENT)
                 .setField("case_nextkin_id", caseNextkinId, original.caseNextkinId, Constants.table().CASE_PATIENT)
                 .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
                 .setField("complete_date", completeDate, original.completeDate)
                 .setField("is_finalized", isFinalized, original.isFinalized);

        return audit;
    }

}
