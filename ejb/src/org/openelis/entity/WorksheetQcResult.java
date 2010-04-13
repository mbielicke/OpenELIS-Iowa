package org.openelis.entity;

/**
 * WorksheetQcResult Entity POJO for database
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
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetQcResult.FetchByWorksheetAnalysisId",
                query = "select new org.openelis.domain.WorksheetQcResultViewDO(wqr.id,wqr.worksheetAnalysisId,wqr.sortOrder,wqr.qcAnalyteId,wqr.typeId,wqr.value,a.name) "+
                        "from WorksheetQcResult wqr LEFT JOIN wqr.qcAnalyte qca LEFT JOIN qca.analyte a "+
                        "where wqr.worksheetAnalysisId = :id order by wqr.sortOrder")})
@Entity
@Table(name = "worksheet_qc_result")
@EntityListeners( {AuditUtil.class})
public class WorksheetQcResult implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer          id;

    @Column(name = "worksheet_analysis_id")
    private Integer          worksheetAnalysisId;

    @Column(name = "sort_order")
    private Integer          sortOrder;

    @Column(name = "qc_analyte_id")
    private Integer          qcAnalyteId;

    @Column(name = "type_id")
    private Integer          typeId;

    @Column(name = "value")
    private String           value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_analyte_id", insertable = false, updatable = false)
    private QcAnalyte qcAnalyte;
    
    @Transient
    private WorksheetQcResult original;

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

    public QcAnalyte getQcAnalyte() {
        return qcAnalyte;
    }
    
    public void setQcAnalyte(QcAnalyte qcAnalyte) {
        this.qcAnalyte = qcAnalyte;
    }

    public void setClone() {
        try {
            original = (WorksheetQcResult)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET_QC_RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("worksheet_analysis_id", worksheetAnalysisId, original.worksheetAnalysisId)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("qc_analyte_id", qcAnalyteId, original.qcAnalyteId)
                 .setField("type_id", typeId, original.typeId)
                 .setField("value", value, original.value);

        return audit;
    }
}
