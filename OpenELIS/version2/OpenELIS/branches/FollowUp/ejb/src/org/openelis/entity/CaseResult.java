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
@Table(name = "case_result")
@EntityListeners({AuditUtil.class})
public class CaseResult implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "case_analysis_id")
    private Integer caseAnalysisId;
    
    @Column(name = "test_analyte_id")
    private Integer testAnalyteId;
    
    @Column(name = "test_result_id")
    private Integer testResultId;
    
    @Column(name = "row")
    private Integer row;
    
    @Column(name = "col")
    private Integer col;
    
    @Column(name = "is_reportable")
    private String isReportable;
    
    @Column(name = "analyte_id")
    private Integer analyteId;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "value")
    private String value;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_analysis_id", insertable = false, updatable = false)
    private CaseAnalysis caseAnalysis;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
    private TestAnalyte testAnalyte;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_result_id", insertable = false, updatable = false)
    private TestResult testResult;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte analyte;
    
    @Transient
    private CaseResult original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getCaseAnalysisId() {
        return caseAnalysisId;
    }
    
    public void setCaseAnalysisId(Integer caseAnalysisId) {
        if (DataBaseUtil.isDifferent(caseAnalysisId, this.caseAnalysisId))
            this.caseAnalysisId = caseAnalysisId;
    }
    
    public CaseAnalysis getCaseAnalysis() {
        return caseAnalysis;
    }
    
    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }
    
    public void setTestAnalyteId(Integer testAnalyteId) {
        if (DataBaseUtil.isDifferent(testAnalyteId, this.testAnalyteId))
            this.testAnalyteId = testAnalyteId;
    }
    
    public TestAnalyte getTestAnalyte() {
        return testAnalyte;
    }
    
    public Integer getTestResultId() {
        return testResultId;
    }
    
    public void setTestResultId(Integer testResultId) {
        if (DataBaseUtil.isDifferent(testResultId, this.testResultId))
            this.testResultId = testResultId;
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
    
    public Integer getRow() {
        return row;
    }
    
    public void setRow(Integer row) {
        if (DataBaseUtil.isDifferent(row, this.row)) 
            this.row = row;
    }
    
    public Integer getCol() {
        return col;
    }
    
    public void setCol(Integer col) {
        if (DataBaseUtil.isDifferent(col, this.col))
            this.col = col;
    }
    
    public String isReportable() {
        return isReportable;
    }
    
    public void setIsReportable(String isReportable) {
        if (DataBaseUtil.isDifferent(isReportable, this.isReportable))
            this.isReportable = isReportable;
    }
    
    public Integer getAnalyteId() {
        return analyteId;
    }
    
    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }
    
    public Analyte getAnalyte() {
        return analyte;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        if(DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }
    
    @Override
    public void setClone() {
        try {
            original = (CaseResult)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("case_analysis_id", caseAnalysisId, original.caseAnalysisId, Constants.table().CASE_ANALYSIS)
                 .setField("test_analyte_id", testAnalyteId, original.testAnalyteId, Constants.table().TEST_ANALYTE)
                 .setField("test_result_id", testResultId, original.testResultId, Constants.table().TEST_RESULT)
                 .setField("row", row, original.row)
                 .setField("col", col, original.col)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("analyte_id", analyteId, original.analyteId, Constants.table().ANALYTE)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("value", value, original.value);

        return audit;
    }
}
