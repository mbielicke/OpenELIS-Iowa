package org.openelis.entity;

/**
 * WorksheetAnalyte Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "worksheet_analyte")
@EntityListeners( {AuditUtil.class})
public class WorksheetAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer          id;

    @Column(name = "worksheet_analysis_id")
    private Integer          worksheetAnalysisId;

    @Column(name = "sort_order")
    private Integer          sortOrder;

    @Column(name = "analyte_id")
    private Integer          analyteId;

    @Column(name = "value")
    private String           value;

    @Column(name = "result_id")
    private Integer          resultId;

    @Transient
    private WorksheetAnalyte original;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        if (DataBaseUtil.isDifferent(resultId, this.resultId))
            this.resultId = resultId;
    }

    public void setClone() {
        try {
            original = (WorksheetAnalyte)this.clone();
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

            AuditUtil.getChangeXML(sortOrder, original.sortOrder, doc, "sort_order");

            AuditUtil.getChangeXML(analyteId, original.analyteId, doc, "analyte_id");

            AuditUtil.getChangeXML(value, original.value, doc, "value");

            AuditUtil.getChangeXML(resultId, original.resultId, doc, "result_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "worksheet_analyte";
    }

}
