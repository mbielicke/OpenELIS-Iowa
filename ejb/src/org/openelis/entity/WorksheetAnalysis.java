package org.openelis.entity;

/**
 * WorksheetAnalysis Entity POJO for database
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
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetItemId",
                query = "select new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.referenceId,wa.referenceTableId,wa.worksheetAnalysisId) "+
                        "from WorksheetAnalysis wa where wa.worksheetItemId = :id")})
@Entity
@Table(name = "worksheet_analysis")
@EntityListeners( {AuditUtil.class})
public class WorksheetAnalysis implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer           id;

    @Column(name = "worksheet_item_id")
    private Integer           worksheetItemId;

    @Column(name = "reference_id")
    private Integer           referenceId;

    @Column(name = "reference_table_id")
    private Integer           referenceTableId;

    @Column(name = "worksheet_analysis_id")
    private Integer           worksheetAnalysisId;

    @Transient
    private WorksheetAnalysis original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    public void setWorksheetItemId(Integer worksheetItemId) {
        if (DataBaseUtil.isDifferent(worksheetItemId, this.worksheetItemId))
            this.worksheetItemId = worksheetItemId;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId, this.referenceId))
            this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
    }

    public void setClone() {
        try {
            original = (WorksheetAnalysis)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");

            AuditUtil.getChangeXML(worksheetItemId,
                                   original.worksheetItemId,
                                   doc,
                                   "worksheet_item_id");

            AuditUtil.getChangeXML(referenceId, original.referenceId, doc, "reference_id");

            AuditUtil.getChangeXML(referenceTableId,
                                   original.referenceTableId,
                                   doc,
                                   "reference_table_id");

            AuditUtil.getChangeXML(worksheetAnalysisId,
                                   original.worksheetAnalysisId,
                                   doc,
                                   "worksheet_analysis_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "worksheet_analysis";
    }
}
