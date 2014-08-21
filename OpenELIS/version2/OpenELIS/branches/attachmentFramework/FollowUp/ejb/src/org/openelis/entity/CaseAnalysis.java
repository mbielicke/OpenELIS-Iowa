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
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_analysis")
@EntityListeners({AuditUtil.class})
public class CaseAnalysis implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "accession")
    private String accession;
    
    @Column(name = "organization_id")
    private Integer organizationId;
    
    @Column(name = "test_id")
    private Integer testId;
    
    @Column(name = "status_id")
    private Integer statusId;
    
    @Column(name = "collection_date")
    private Date    collectionDate;
    
    @Column(name = "completed_date")
    private Date    completedDate;
    
    @Column(name = "condition_id")
    private Integer conditionId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test test;
    
    @Transient
    private CaseAnalysis original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        if(DataBaseUtil.isDifferent(accession, this.accession))
            this.accession = accession;
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
    
    public Integer getTestId() {
        return testId;
    }
    
    public void setTestId(Integer testId) {
        if(DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }
    
    public Test getTest() {
        return test;
    }
    
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        if(DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }
    
    public Datetime getCollectionDate() {
        return DataBaseUtil.toYM(collectionDate);
    }
    
    public void setCollectionDate(Datetime collectionDate) {
        if(DataBaseUtil.isDifferent(collectionDate, this.collectionDate))
            this.collectionDate = DataBaseUtil.toDate(collectionDate);
    }
    
    public Datetime getCompletedDate() {
        return DataBaseUtil.toYM(completedDate);
    }
    
    public void setCompletedDate(Datetime completedDate) {
        if(DataBaseUtil.isDifferent(completedDate, this.completedDate))
            this.completedDate = DataBaseUtil.toDate(completedDate);
    }
    
    public Integer getConditionId() {
        return conditionId;
    }
    
    public void setConditionId(Integer conditionId) {
        if(DataBaseUtil.isDifferent(conditionId, this.conditionId))
            this.conditionId = conditionId;
    }
    
    @Override
    public void setClone() {
        try {
            original = (CaseAnalysis)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_ANALYSIS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("accession", accession, original.accession)
                 .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
                 .setField("test_id", testId, original.testId, Constants.table().TEST)
                 .setField("status_id", statusId, original.statusId, Constants.table().DICTIONARY)
                 .setField("collection_date", collectionDate, original.collectionDate)
                 .setField("completed_date", completedDate, original.completedDate)
                 .setField("condition_id", conditionId, original.conditionId, Constants.table().DICTIONARY);

        return audit;
    }

}
