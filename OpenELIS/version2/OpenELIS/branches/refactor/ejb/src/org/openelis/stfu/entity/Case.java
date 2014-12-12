package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.Auditable;
import org.openelis.utils.AuditUtil;

@NamedQueries ({
	@NamedQuery( name = "Case.fetchById",
			     query = "select new org.openelis.stfu.domain.CaseDO(id,createdDate,patientId,nextOfKinId,casePatientId,caseNextOfKinId,organizationId," 
	                     +"completedDate,isFinalized) from Case where id = :id"),
	               
	@NamedQuery( name = "Case.fetchByIds",
	             query = "select new org.openelis.stfu.domain.CaseDO(id,createdDate,patientId,nextOfKinId,casePatientId,caseNextOfKinId,organizationId," 
	                     +"completedDate,isFinalized) from Case where id in (:ids)")
	/*
	@NamedQuery( name = "Case.FetchActiveByUser",
	             query = "select new org.openelis.stfu.domain.CaseDO(c.id,c.createdDate,c.patientId,c.nextOfKinId,c.casePatientId,c.caseNextOfKinId,"
	                     +"c.organizationId,c.completedDate,c.isFinalized) form Case c where c.caseUser.systemUserId = :systemUserId")
	*/
})

@Entity
@Table(name="case")
@EntityListeners({AuditUtil.class})
public class Case implements Auditable, Cloneable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                     id;
    
    @Column(name = "created_date")
    private Date                        createdDate;
    
    @Column(name = "patient_id")
    private Integer                     patientId;
    
    @Column(name = "next_of_kin_id")
    private Integer                     nextOfKinId;
    
    @Column(name = "case_patient_id")
    private Integer                     casePatientId;
    
    @Column(name = "case_next_of_kin_id") 
    private Integer                     caseNextOfKinId;
    
    @Column(name = "organization_id")
    private Integer                     organizationId;
    
    @Column(name = "completed_date")
    private Date                        completedDate;
    
    @Column(name = "is_finalized")
    private String                      isFinalized;
    
    @Transient
    private Case original;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(isDifferent(id, this.id))
			this.id = id;
	}

	public Datetime getCreatedDate() {
		return toYM(createdDate);
	}

	public void setCreatedDate(Datetime createdDate) {
		if(isDifferentYM(createdDate,this.createdDate))
			this.createdDate = toDate(createdDate);
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		if(isDifferent(patientId,this.patientId))
			this.patientId = patientId;
	}

	public Integer getNextOfKinId() {
		return nextOfKinId;
	}

	public void setNextOfKinId(Integer nextOfKinId) {
		if(isDifferent(nextOfKinId,this.nextOfKinId))
			this.nextOfKinId = nextOfKinId;
	}

	public Integer getCasePatientId() {
		return casePatientId;
	}

	public void setCasePatientId(Integer casePatientId) {
		if(isDifferent(casePatientId,this.casePatientId))
			this.casePatientId = casePatientId;
	}

	public Integer getCaseNextOfKinId() {
		return caseNextOfKinId;
	}

	public void setCaseNextOfKinId(Integer caseNextOfKinId) {
		if(isDifferent(caseNextOfKinId,this.caseNextOfKinId))
			this.caseNextOfKinId = caseNextOfKinId;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		if(isDifferent(organizationId,this.organizationId))
			this.organizationId = organizationId;
	}

	public Datetime getCompletedDate() {
		return toYM(completedDate);
	}

	public void setCompletedDate(Datetime completedDate) {
		if(isDifferentYM(completedDate,this.completedDate))
			this.completedDate = toDate(completedDate);
	}

	public String getIsFinalized() {
		return isFinalized;
	}

	public void setIsFinalized(String isFinalized) {
		this.isFinalized = isFinalized;
	}

	@Override
	public void setClone() {
		try {
			original = (Case)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("created_date", createdDate, original.createdDate)
        	     .setField("patient_id", patientId, original.patientId, Constants.table().PATIENT)
        	     .setField("next_of_kin_id", nextOfKinId, original.nextOfKinId, Constants.table().PATIENT)
        	     .setField("case_patient_id", casePatientId, original.casePatientId, Constants.table().CASE_PATIENT)
        	     .setField("case_next_of_kin_id", caseNextOfKinId, original.caseNextOfKinId, Constants.table().CASE_PATIENT)
        	     .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
        	     .setField("completed_date", completedDate, original.completedDate)
        	     .setField("is_finalized", isFinalized, original.isFinalized);
        }
        
		return audit;
	}
	

}
