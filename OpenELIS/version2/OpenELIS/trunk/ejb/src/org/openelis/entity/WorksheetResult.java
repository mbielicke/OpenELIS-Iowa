package org.openelis.entity;

/**
 * WorksheetResult Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetResult.FetchByWorksheetAnalysisId",
                query = "select new org.openelis.domain.WorksheetResultViewDO(wr.id,wr.worksheetAnalysisId,wr.testAnalyteId,wr.testResultId,wr.isColumn,wr.sortOrder,wr.analyteId,wr.typeId,wr.value,a.name,ta.resultGroup) "
                      + " from WorksheetResult wr LEFT JOIN wr.analyte a LEFT JOIN wr.testAnalyte ta "+
                        " where wr.worksheetAnalysisId = :id order by wr.sortOrder")})
@Entity
@Table(name = "worksheet_result")
@EntityListeners( {AuditUtil.class})
public class WorksheetResult implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer          id;

    @Column(name = "worksheet_analysis_id")
    private Integer          worksheetAnalysisId;

    @Column(name = "test_analyte_id")
    private Integer          testAnalyteId;

    @Column(name = "test_result_id")
    private Integer          testResultId;

    @Column(name = "is_column")
    private String           isColumn;

    @Column(name = "sort_order")
    private Integer          sortOrder;

    @Column(name = "analyte_id")
    private Integer          analyteId;

    @Column(name = "type_id")
    private Integer          typeId;

    @Column(name = "value")
    private String           value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte analyte;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
    private TestAnalyte testAnalyte;
    
    @Transient
    private WorksheetResult original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        if (DataBaseUtil.isDifferent(worksheetAnalysisId, this.worksheetAnalysisId))
            this.worksheetAnalysisId = worksheetAnalysisId;
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

    public void setClone() {
        try {
            original = (WorksheetResult)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET_RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("worksheet_analysis_id", worksheetAnalysisId, original.worksheetAnalysisId, ReferenceTable.WORKSHEET_ANALYSIS)
                 .setField("test_analyte_id", testAnalyteId, original.testAnalyteId, ReferenceTable.TEST_ANALYTE)
                 .setField("test_result_id", testResultId, original.testResultId, ReferenceTable.TEST_RESULT)
                 .setField("is_column", isColumn, original.isColumn)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("analyte_id", analyteId, original.analyteId, ReferenceTable.ANALYTE)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("value", value, original.value);

        return audit;
    }
}
