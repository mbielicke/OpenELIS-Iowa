package org.openelis.entity;

/**
 * WorksheetQc Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
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
@Table(name = "worksheet_qc")
@EntityListeners( {AuditUtil.class})
public class WorksheetQc implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer     id;

    @Column(name = "worksheet_analysis_id")
    private Integer     worksheetAnalysisId;

    @Column(name = "sort_order")
    private Integer     sortOrder;

    @Column(name = "qc_analyte_id")
    private Integer     qcAnalyteId;

    @Column(name = "type_id")
    private Integer     typeId;

    @Column(name = "value")
    private String      value;

    @Transient
    private WorksheetQc original;

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

    public Integer getQcAnalyteId() {
        return qcAnalyteId;
    }

    public void setQcAnalyteId(Integer qcAnalyteId) {
        if (DataBaseUtil.isDifferent(qcAnalyteId, this.qcAnalyteId))
            this.qcAnalyteId = qcAnalyteId;
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
            original = (WorksheetQc)this.clone();
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

            AuditUtil.getChangeXML(qcAnalyteId, original.qcAnalyteId, doc, "qc_analyte_id");

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
        return "worksheet_qc";
    }

}
