package org.openelis.entity;

/**
 * WorksheetAnalysis Entity POJO for database
 */

import java.util.Date;

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
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetItemId",
                query = "select new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.accessionNumber,wa.analysisId,wa.qcId,wa.worksheetAnalysisId,wa.qcSystemUserId,wa.qcStartedDate,wa.isFromOther) "+
                        "from WorksheetAnalysis wa where wa.worksheetItemId = :id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchById",
                query = "select distinct new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.accessionNumber,wa.analysisId,wa.qcId,wa.worksheetAnalysisId,wa.qcSystemUserId,wa.qcStartedDate,wa.isFromOther) "+
                        "from WorksheetAnalysis wa where wa.id = :id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetStatusId",
                query = "select distinct new org.openelis.domain.WorksheetCacheVO(w.id, w.createdDate, w.systemUserId, w.statusId, t.name, m.name, s.name)"
                      + " from WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, Analysis a, Test t, Method m, Section s where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and w.statusId = :statusId"
                      +	" and wa.analysisId = a.id and a.testId = t.id and t.methodId = m.id and a.sectionId = s.id")})
@Entity
@Table(name = "worksheet_analysis")
@EntityListeners( {AuditUtil.class})
public class WorksheetAnalysis implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "worksheet_item_id")
    private Integer           worksheetItemId;

    @Column(name = "accession_number")
    private String            accessionNumber;

    @Column(name = "analysis_id")
    private Integer           analysisId;

    @Column(name = "qc_id")
    private Integer           qcId;

    @Column(name = "worksheet_analysis_id")
    private Integer           worksheetAnalysisId;

    @Column(name = "qc_system_user_id")
    private Integer           qcSystemUserId;

    @Column(name = "qc_started_date")
    private Date              qcStartedDate;
    
    @Column(name = "is_from_other")
    private String            isFromOther;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_item_id", insertable = false, updatable = false)
    private WorksheetItem     worksheetItem;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private Analysis          analysis;

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

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        if (DataBaseUtil.isDifferent(analysisId, this.analysisId))
            this.analysisId = analysisId;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        if (DataBaseUtil.isDifferent(qcId, this.qcId))
            this.qcId = qcId;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        if (DataBaseUtil.isDifferent(worksheetAnalysisId, this.worksheetAnalysisId))
            this.worksheetAnalysisId = worksheetAnalysisId;
    }

    public Integer getQcSystemUserId() {
        return qcSystemUserId;
    }

    public void setQcSystemUserId(Integer qcSystemUserId) {
        if (DataBaseUtil.isDifferent(qcSystemUserId, this.qcSystemUserId))
            this.qcSystemUserId = qcSystemUserId;
    }

    public Datetime getQcStartedDate() {
        return DataBaseUtil.toYM(qcStartedDate);
    }

    public void setQcStartedDate(Datetime qcStartedDate) {
        if (DataBaseUtil.isDifferentYM(qcStartedDate, this.qcStartedDate))
            this.qcStartedDate = DataBaseUtil.toDate(qcStartedDate);
    }

    public String getIsFromOther() {
        return isFromOther;
    }

    public void setIsFromOther(String isFromOther) {
        this.isFromOther = isFromOther;
    }

    public WorksheetItem getWorksheetItem() {
        return worksheetItem;
    }

    public void setWorksheetItem(WorksheetItem worksheetItem) {
        this.worksheetItem = worksheetItem;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public void setClone() {
        try {
            original = (WorksheetAnalysis)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET_ANALYSIS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("worksheet_item_id", worksheetItemId, original.worksheetItemId, ReferenceTable.WORKSHEET_ITEM)
                 .setField("accession_number", accessionNumber, original.accessionNumber)
                 .setField("analysis_id", analysisId, original.analysisId, ReferenceTable.ANALYSIS)
                 .setField("qc_id", qcId, original.qcId, ReferenceTable.QC)
                 .setField("worksheet_analysis_id", worksheetAnalysisId, original.worksheetAnalysisId, ReferenceTable.WORKSHEET_ANALYSIS)
                 .setField("qc_system_user_id", qcSystemUserId, original.qcSystemUserId)
                 .setField("qc_started_date", qcStartedDate, original.qcStartedDate)
                 .setField("is_from_other", isFromOther, original.isFromOther);

        return audit;
    }
}
