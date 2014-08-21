package org.openelis.domain;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class CaseAnalysisDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id,organizationId,testId,statusId,conditionId;
    protected Datetime collectionDate,completedDate;
    protected String accession;
    
    public CaseAnalysisDO() {
        
    }
    
    public CaseAnalysisDO(Integer id, String accession, Integer organizationId, Integer testId, Integer statusId,
                          Date collectionDate, Date completedDate, Integer conditionId) {
        
        setId(id);
        setAccession(accession);
        setOrganizationId(organizationId);
        setTestId(testId);
        setStatusId(statusId);
        setCollectionDate(DataBaseUtil.toYM(collectionDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setConditionId(conditionId);
        _changed = true;
        
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        this.accession = accession;
        _changed = true;
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
        _changed = true;
    }
    
    public Integer getTestId() {
        return testId;
    }
    
    public void setTestId(Integer testId) {
        this.testId = testId;
        _changed = true;
    }
    
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }
    
    public Datetime getCollectionDate() {
        return collectionDate;
    }
    
    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYM(collectionDate);
        _changed = true;
    }
    
    public Datetime getCompletedDate() {
        return completedDate;
    }
    
    public void setCompletedDate(Datetime completedDate) {
        this.completedDate = DataBaseUtil.toYM(completedDate);
        _changed = true;
    }
    
    public Integer getConditionId() {
        return conditionId;
    }
    
    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
        _changed = true;
    }
    

}
