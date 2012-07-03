package org.openelis.entity;

/**
 * Result Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Result.FetchByAnalysisId",
                query = "select new org.openelis.domain.ResultViewDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId," +
                        "r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value, a.name, ta.rowGroup,ta.typeId,ta.resultGroup)"
                      + " from Result r, Analysis an, Analyte a, TestAnalyte ta "
                      + " where  r.analysisId = an.id and r.analyteId = a.id and r.testAnalyteId = ta.id and an.id = :id order by r.sortOrder"),
    @NamedQuery( name = "Result.FetchForFinalReportByAnalysisId",
                query = "select new org.openelis.domain.ResultViewDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId," +
                        "r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value, a.name, ta.rowGroup,ta.typeId, ta.resultGroup)"
                      + " from Result r left join r.analysis an left join r.analyte a left join r.testAnalyte ta "
                      + " where r.analysisId = :aid and" +
                      	" :aid not in (select analysisId from AnalysisQaevent q where q.analysisId = :aid and q.typeId = :overrideid) and"+
                      	" :sid not in (select sampleId from SampleQaevent sq where sq.sampleId = :sid and sq.typeId = :overrideid)"+
                        " order by r.sortOrder"),    
    @NamedQuery( name = "Result.FetchForDataViewByAnalysisIds",
                query = "select new org.openelis.domain.ResultViewDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId," +
                        "r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value, a.name, ta.rowGroup,ta.typeId,ta.resultGroup)"
                      + " from Result r, Analysis an, Test t, Analyte a, TestAnalyte ta "
                      + " where r.analysisId in (:ids) and r.isReportable = 'Y'and r.isColumn = 'N' and r.value != null"
                      + " and an.id = r.analysisId and t.id = an.testId and ta.id = r.testAnalyteId and a.id = r.analyteId order by a.name"),
   @NamedQuery( name = "Result.FetchForDataViewByAnalysisIdAndRowGroup",
               query = "select new org.openelis.domain.ResultViewDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId," +
                       "r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value, a.name, ta.rowGroup,ta.typeId,ta.resultGroup)"
                     + " from Result r, Analysis an, Test t, Analyte a, TestAnalyte ta "
                     + " where r.analysisId = :id and ta.rowGroup = :rowGroup and r.isColumn = 'Y' "
                     + " and an.id = r.analysisId and t.id = an.testId and ta.id = r.testAnalyteId and a.id = r.analyteId order by r.sortOrder"),  
  @NamedQuery( name = "Result.FetchForBillingByAnalysisId",
              query = "select new org.openelis.domain.ResultDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId," +
                      "r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value)"
                    + " from Result r where r.analysisId = :id and r.isReportable = 'Y' and r.isColumn = 'N'"),                  
   @NamedQuery( name = "Result.FetchAnalyteByAnalysisId",
                query = "select new org.openelis.domain.AnalyteDO(a.id,a.name,a.isActive,a.parentAnalyteId,a.externalId) "
                      + " from Result r left join r.analyte a where r.analysisId = :id order by r.sortOrder")})
@Entity
@Table(name = "result")
@EntityListeners({AuditUtil.class})
public class Result implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer     id;

    @Column(name = "analysis_id")
    private Integer     analysisId;

    @Column(name = "test_analyte_id")
    private Integer     testAnalyteId;

    @Column(name = "test_result_id")
    private Integer     testResultId;

    @Column(name = "is_column")
    private String      isColumn;

    @Column(name = "sort_order")
    private Integer     sortOrder;

    @Column(name = "is_reportable")
    private String      isReportable;

    @Column(name = "analyte_id")
    private Integer     analyteId;

    @Column(name = "type_id")
    private Integer     typeId;

    @Column(name = "value")
    private String      value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private Analysis    analysis;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte     analyte;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
    private TestAnalyte testAnalyte;

    @Transient
    private Result      original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        if (DataBaseUtil.isDifferent(analysisId, this.analysisId))
            this.analysisId = analysisId;
    }

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        if (DataBaseUtil.isDifferent(testAnalyteId, this.testAnalyteId))
            this.testAnalyteId = testAnalyteId;
    }

    public Integer getTestResultId() {
        return testResultId;
    }

    public void setTestResultId(Integer testResultId) {
        if (DataBaseUtil.isDifferent(testResultId, this.testResultId))
            this.testResultId = testResultId;
    }

    public String getIsColumn() {
        return isColumn;
    }

    public void setIsColumn(String isColumn) {
        if (DataBaseUtil.isDifferent(isColumn, this.isColumn))
            this.isColumn = isColumn;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public String getIsReportable() {
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public TestAnalyte getTestAnalyte() {
        return testAnalyte;
    }

    public void setTestAnalyte(TestAnalyte testAnalyte) {
        this.testAnalyte = testAnalyte;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public void setClone() {
        try {
            original = (Result)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("analysis_id", analysisId, original.analysisId, ReferenceTable.ANALYSIS)
                 .setField("test_analyte_id", testAnalyteId, original.testAnalyteId, ReferenceTable.TEST_ANALYTE)
                 .setField("test_result_id", testResultId, original.testResultId, ReferenceTable.TEST_RESULT)
                 .setField("is_column", isColumn, original.isColumn)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("analyte_id", analyteId, original.analyteId, ReferenceTable.ANALYTE)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("value", value, original.value);

        return audit;
    }
}