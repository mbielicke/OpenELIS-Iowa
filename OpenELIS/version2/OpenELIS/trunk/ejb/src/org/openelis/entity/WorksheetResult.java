package org.openelis.entity;

/**
 * WorksheetResult Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetResult.FetchByWorksheetAnalysisId",
                query = "select new org.openelis.domain.WorksheetResultDO(wr.id,wr.worksheetAnalysisId,wr.testAnalyteId,wr.testResultId,wr.isColumn,wr.sortOrder,wr.analyteId,wr.typeId,wr.value) "+
                        "from WorksheetResult wr where wr.worksheetAnalysisId = :id")})
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

    public void setClone() {
        try {
            original = (WorksheetResult)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");

            AuditUtil.getChangeXML(worksheetAnalysisId,
                                   original.worksheetAnalysisId,
                                   doc,
                                   "worksheet_analysis_id");

            AuditUtil.getChangeXML(testAnalyteId,
                                   original.testAnalyteId,
                                   doc,
                                   "test_analyte_id");

            AuditUtil.getChangeXML(testResultId,
                                   original.testResultId,
                                   doc,
                                   "test_resul_id");

            AuditUtil.getChangeXML(isColumn,
                                   original.isColumn,
                                   doc,
                                   "is_column");

            AuditUtil.getChangeXML(sortOrder, original.sortOrder, doc, "sort_order");

            AuditUtil.getChangeXML(analyteId, original.analyteId, doc, "analyte_id");

            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");

            AuditUtil.getChangeXML(value, original.value, doc, "value");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "worksheet_result";
    }

}
