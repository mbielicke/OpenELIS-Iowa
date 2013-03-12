package org.openelis.entity;

/**
 * WorksheetAnalysis Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

@NamedQueries({
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetItemId",
                query = "select new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.accessionNumber,wa.analysisId,wa.qcLotId,wa.worksheetAnalysisId,wa.qcSystemUserId,wa.qcStartedDate,wa.isFromOther) "+
                        "from WorksheetAnalysis wa where wa.worksheetItemId = :id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchById",
                query = "select distinct new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.accessionNumber,wa.analysisId,wa.qcLotId,wa.worksheetAnalysisId,wa.qcSystemUserId,wa.qcStartedDate,wa.isFromOther) "+
                        "from WorksheetAnalysis wa where wa.id = :id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByQcLotId",
                query = "select distinct new org.openelis.domain.WorksheetAnalysisDO(wa.id,wa.worksheetItemId,wa.accessionNumber,wa.analysisId,wa.qcLotId,wa.worksheetAnalysisId,wa.qcSystemUserId,wa.qcStartedDate,wa.isFromOther)"
                      + " from WorksheetAnalysis wa where wa.qcLotId = :id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetStatusId",
                query = "select distinct new org.openelis.domain.ToDoWorksheetVO(w.id, w.createdDate, w.systemUserId, w.statusId, t.name, m.name, s.name)"
                      + " from WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, Analysis a, Test t, Method m, Section s where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and w.statusId = :statusId"
                      +	" and wa.analysisId = a.id and a.testId = t.id and t.methodId = m.id and a.sectionId = s.id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByDateForQcChart",
                query = "select distinct new org.openelis.domain.QcChartResultVO(wa.accessionNumber, ql.lotNumber, w.id, q.id, a.id, wa.id, a.name, d.systemName, w.createdDate," +
                        " wqr.value1,wqr.value2,wqr.value3,wqr.value4,wqr.value5,wqr.value6,wqr.value7," +
                        " wqr.value8,wqr.value9,wqr.value10,wqr.value11,wqr.value12," +
                        " wqr.value13,wqr.value14,wqr.value15,wqr.value16,wqr.value17," +
                        " wqr.value18,wqr.value19,wqr.value20,wqr.value21,wqr.value22," +
                        " wqr.value23,wqr.value24,wqr.value25,wqr.value26,wqr.value27," +
                        " wqr.value28,wqr.value29,wqr.value30 ) " +
                        "from  WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, WorksheetQcResult wqr, Qc q, QcLot ql, QcAnalyte qa, Analyte a, Dictionary d " +  
                        "where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and wqr.worksheetAnalysisId = wa.id and wa.qcLotId = ql.id and q.id = ql.qcId and wqr.qcAnalyteId = qa.id and" + 
                        " qa.analyteId = a.id and qa.isTrendable = 'Y' and w.createdDate between :startedDate and :endDate and q.name = :qcName and d.id = w.formatId and" +
                        " w.statusId in (select id from Dictionary where systemName in ('worksheet_complete', 'worksheet_working')) "+ 
                        "order by w.createdDate"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByInstancesForQcChart",
                query = "select w.createdDate, wa.id " +
                        "from WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, Qc q, QcLot ql " +  
                        "where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and wa.qcLotId = ql.id and ql.qcId = q.id and" + 
                        " q.name = :qcName and w.statusId in (select id from Dictionary where systemName in ('worksheet_complete', 'worksheet_working')) " + 
                        "order by w.createdDate desc"),
   @NamedQuery( name = "WorksheetAnalysis.FetchAnalytesForQcChart",
               query = "select distinct new org.openelis.domain.QcChartResultVO(wa.accessionNumber, ql.lotNumber, w.id, q.id, a.id, wa.id, a.name, d.systemName, w.createdDate," +
                        " wqr.value1,wqr.value2,wqr.value3,wqr.value4,wqr.value5,wqr.value6,wqr.value7," +
                        " wqr.value8,wqr.value9,wqr.value10,wqr.value11,wqr.value12," +
                        " wqr.value13,wqr.value14,wqr.value15,wqr.value16,wqr.value17," +
                        " wqr.value18,wqr.value19,wqr.value20,wqr.value21,wqr.value22," +
                        " wqr.value23,wqr.value24,wqr.value25,wqr.value26,wqr.value27," +
                        " wqr.value28,wqr.value29,wqr.value30 ) " +
                        "from  WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, WorksheetQcResult wqr, Qc q, QcLot ql, QcAnalyte qa, Analyte a, Dictionary d " +  
                        "where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and wqr.worksheetAnalysisId = wa.id and wa.qcLotId = ql.id and q.id = ql.qcId and wqr.qcAnalyteId = qa.id and" + 
                        " qa.analyteId = a.id and qa.isTrendable = 'Y' and d.id = w.formatId and wa.id in (:ids) " + 
                        "order by w.createdDate")})      

@Entity
@Table(name = "worksheet_analysis")
public class WorksheetAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "worksheet_item_id")
    private Integer           worksheetItemId;

    @Column(name = "accession_number")
    private String            accessionNumber;

    @Column(name = "analysis_id")
    private Integer           analysisId;

    @Column(name = "qc_lot_id")
    private Integer           qcLotId;

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

    public Integer getQcLotId() {
        return qcLotId;
    }

    public void setQcLotId(Integer qcLotId) {
        if (DataBaseUtil.isDifferent(qcLotId, this.qcLotId))
            this.qcLotId = qcLotId;
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
}
