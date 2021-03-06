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
	@NamedQuery( name = "CaseAnalysis.fetchById",
			     query = "select new org.openelis.stfu.domain.CaseAnalysisDO(id,caseSampleId,testId,statusId,"
			             +"completedDate,conditionId) from CaseAnalysis where id = :id"),
	@NamedQuery( name = "CaseAnalysis.fetchByIds",
			     query = "select new org.openelis.stfu.domain.CaseAnalysisDO(id,caseSampleId,testId,statusId,"
			             +"completedDate,conditionId) from CaseAnalysis where id in (:ids)"),		
	@NamedQuery( name = "CaseAnalysis.fetchByCaseId",
                 query = "select new org.openelis.stfu.domain.CaseAnalysisDO(id,caseSampleId,testId,statusId,"
                         +"completedDate,conditionId) from CaseAnalysis where caseId = :id"),
    @NamedQuery( name = "CaseAnalysis.fetchByCaseIds",
                 query = "select new org.openelis.stfu.domain.CaseAnalysisDO(id,caseSampleId,testId,statusId,"
                         +"completedDate,conditionId) from CaseAnalysis where caseId in (:ids)")			
})

@Entity
@Table(name = "case_analysis")
@EntityListeners(value=AuditUtil.class)
public class CaseAnalysis implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "case_sample_id")
	private Integer caseSampleId;
			
	@Column(name = "test_id")
	private Integer testId;
	
	@Column(name = "status_id")
	private Integer statusId;
		
	@Column(name = "completed_date")
	private Date completedDate;
	
	@Column(name = "condition_id")
	private Integer conditionId;
	
	@Transient
	private CaseAnalysis original;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(isDifferent(id,this.id))
			this.id = id;
	}
	
	public Integer getCaseSampleId() {
		return caseSampleId;
	}
	
	public void setCaseId(Integer caseSampleId) {
		if(isDifferent(caseSampleId,this.caseSampleId))
			this.caseSampleId = caseSampleId;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		if(isDifferent(testId,this.testId))
			this.testId = testId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		if(isDifferent(statusId,this.statusId))
			this.statusId = statusId;
	}

	public Datetime getCompletedDate() {
		return toYM(completedDate);
	}

	public void setCompletedDate(Datetime completedDate) {
		if(isDifferentYM(completedDate,this.completedDate))
			this.completedDate = toDate(completedDate);
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public void setConditionId(Integer conditionId) {
		if(isDifferent(conditionId,this.conditionId))
			this.conditionId = conditionId;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseAnalysis)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_ANALYSIS);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("case_id", caseSampleId, original.caseSampleId)
        	     .setField("test_id", testId, original.testId, Constants.table().TEST)
        	     .setField("status_id", statusId, original.statusId, Constants.table().DICTIONARY)
        	     .setField("completed_date", completedDate, original.completedDate)
        	     .setField("condition_id", conditionId, original.conditionId, Constants.table().DICTIONARY);
        }
		
        return audit;
	}
        

}
