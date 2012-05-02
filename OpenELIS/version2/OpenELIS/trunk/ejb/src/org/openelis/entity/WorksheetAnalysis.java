package org.openelis.entity;

/**
 * WorksheetAnalysis Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
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
                      +	" and wa.analysisId = a.id and a.testId = t.id and t.methodId = m.id and a.sectionId = s.id"),
    @NamedQuery( name = "WorksheetAnalysis.FetchByDateForQcChart",
                query = "select distinct new org.openelis.domain.QcChartResultVO(wa.accessionNumber, q.lotNumber, q.id, a.id, wa.id, a.name, d.systemName, w.createdDate," +
                        " wqr.value1,wqr.value2,wqr.value3,wqr.value4,wqr.value5,wqr.value6,wqr.value7," +
                        " wqr.value8,wqr.value9,wqr.value10,wqr.value11,wqr.value12," +
                        " wqr.value13,wqr.value14,wqr.value15,wqr.value16,wqr.value17," +
                        " wqr.value18,wqr.value19,wqr.value20,wqr.value21,wqr.value22," +
                        " wqr.value23,wqr.value24,wqr.value25,wqr.value26,wqr.value27," +
                        " wqr.value28,wqr.value29,wqr.value30 ) " +
                        "from  WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, WorksheetQcResult wqr, Qc q, QcAnalyte qa, Analyte a, Dictionary d " +  
                        "where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and wqr.worksheetAnalysisId = wa.id and wa.qcId = q.id and wqr.qcAnalyteId = qa.id and" + 
                        " qa.analyteId = a.id and qa.isTrendable = 'Y' and w.createdDate between :startedDate and :endDate and q.name = :qcName and d.id = w.formatId " + 
                        "order by w.createdDate"),
   @NamedQuery( name = "WorksheetAnalysis.FetchByInstancesForQcChart",
                        query = "select distinct new org.openelis.domain.QcChartResultVO(wa.accessionNumber, q.lotNumber, q.id, a.id, wa.id, a.name, d.systemName, w.createdDate," +
                        " wqr.value1,wqr.value2,wqr.value3,wqr.value4,wqr.value5,wqr.value6,wqr.value7," +
                        " wqr.value8,wqr.value9,wqr.value10,wqr.value11,wqr.value12," +
                        " wqr.value13,wqr.value14,wqr.value15,wqr.value16,wqr.value17," +
                        " wqr.value18,wqr.value19,wqr.value20,wqr.value21,wqr.value22," +
                        " wqr.value23,wqr.value24,wqr.value25,wqr.value26,wqr.value27," +
                        " wqr.value28,wqr.value29,wqr.value30 ) " +
                        "from WorksheetAnalysis wa, WorksheetItem wi, Worksheet w, WorksheetQcResult wqr, Qc q, QcAnalyte qa, Analyte a, Dictionary d " +  
                        "where wa.worksheetItemId = wi.id and wi.worksheetId = w.id and wqr.worksheetAnalysisId = wa.id and wa.qcId = q.id and wqr.qcAnalyteId = qa.id and" + 
                        " qa.analyteId = a.id and qa.isTrendable = 'Y' and q.name = :qcName and d.id = w.formatId " + 
                        "order by wa.id desc")})         

    @NamedNativeQueries({
    @NamedNativeQuery(name = "WorksheetAnalysis.FetchDataForQcChartForDateRange",
                query = "select wa.accession_number, q.lot_number, CAST(a.name AS varchar(60)) analyte_parameter_name, ap.p1, ap.p2, ap.p3, w.created_date, d.system_name," +
                        " wqr.value_1,wqr.value_2,wqr.value_3,wqr.value_4,wqr.value_5,wqr.value_6,wqr.value_7," +
                        " wqr.value_8,wqr.value_9,wqr.value_10,wqr.value_11,wqr.value_12," +
                        " wqr.value_13,wqr.value_14,wqr.value_15,wqr.value_16,wqr.value_17," +
                        " wqr.value_18,wqr.value_19,wqr.value_20,wqr.value_21,wqr.value_22," +
                        " wqr.value_23,wqr.value_24,wqr.value_25,wqr.value_26,wqr.value_27," +
                        " wqr.value_28,wqr.value_29,wqr.value_30 " +
                        "from  worksheet_analysis wa, worksheet_item wi, worksheet w, worksheet_qc_result wqr, qc q, qc_analyte qa, analyte a, dictionary d, outer analyte_parameter ap " +
                        "where wa.worksheet_item_id = wi.id and wi.worksheet_id = w.id and wqr.worksheet_analysis_id = wa.id and wa.qc_id = q.id and wqr.qc_analyte_id = qa.id and" +
                        " qa.analyte_id = a.id and qa.is_trendable = 'Y' and w.created_date between :startedDate and :endDate and q.name = :qcName and" +
                        " ap.reference_id = q.id and ap.reference_table_id = 51 and ap.analyte_id = a.id and ap.active_begin < w.created_date and ap.active_end > w.created_date and d.id = w.format_id " +
                        "order by w.created_date",
           resultSetMapping = "WorksheetAnalysis.FetchDataForQcChartForDateRange"),
    @NamedNativeQuery(name = "WorksheetAnalysis.FetchDataForQcChartForNumRange",
                query = "select first :numInstances wa.accession_number, q.lot_number, CAST(a.name AS varchar(60)) analyte_parameter_name, ap.p1, ap.p2, ap.p3, w.created_date " +
                        "from  worksheet_analysis wa, worksheet_item wi, worksheet w, worksheet_qc_result wqr, qc q, qc_analyte qa, analyte a, outer analyte_parameter ap " +
                        "where wa.worksheet_item_id = wi.id and wi.worksheet_id = w.id and wqr.worksheet_analysis_id = wa.id and wa.qc_id = q.id and wqr.qc_analyte_id = qa.id and" +
                        " qa.analyte_id = a.id and qa.is_trendable = 'Y' and q.name = :qcName and" +
                        " ap.reference_id = q.id and ap.reference_table_id = 51 and ap.analyte_id = a.id and ap.active_begin < w.created_date and ap.active_end > w.created_date " +
                        "order by wa.id desc",
           resultSetMapping = "WorksheetAnalysis.FetchDataForQcChartForNumRange")})
@SqlResultSetMappings({
    @SqlResultSetMapping(name="WorksheetAnalysis.FetchDataForQcChartForDateRange",
                         columns={@ColumnResult(name="accession_number"), @ColumnResult(name="lot_number"), @ColumnResult(name="analyte_parameter_name"), 
                                  @ColumnResult(name="p1"), @ColumnResult(name="p2"), @ColumnResult(name="p3"),  
                                  @ColumnResult(name="created_date"), @ColumnResult(name="system_name"), @ColumnResult(name="value_1"), @ColumnResult(name="value_2"),
                                  @ColumnResult(name="value_3"),@ColumnResult(name="value_4"),@ColumnResult(name="value_5"),@ColumnResult(name="value_6"),
                                  @ColumnResult(name="value_7"),@ColumnResult(name="value_8"),@ColumnResult(name="value_9"),@ColumnResult(name="value_10"),
                                  @ColumnResult(name="value_11"),@ColumnResult(name="value_12"),@ColumnResult(name="value_13"),@ColumnResult(name="value_14"),
                                  @ColumnResult(name="value_15"),@ColumnResult(name="value_16"),@ColumnResult(name="value_17"),@ColumnResult(name="value_18"),
                                  @ColumnResult(name="value_19"),@ColumnResult(name="value_20"),@ColumnResult(name="value_21"),@ColumnResult(name="value_22"),
                                  @ColumnResult(name="value_23"),@ColumnResult(name="value_24"),@ColumnResult(name="value_25"),@ColumnResult(name="value_26"),
                                  @ColumnResult(name="value_27"),@ColumnResult(name="value_28"),@ColumnResult(name="value_29"),@ColumnResult(name="value_30")}),
    @SqlResultSetMapping(name="WorksheetAnalysis.FetchDataForQcChartForNumRange",
                         columns={@ColumnResult(name="accession_number"), @ColumnResult(name="lot_number"), @ColumnResult(name="analyte_parameter_name"), 
                                  @ColumnResult(name="p1"), @ColumnResult(name="p2"), @ColumnResult(name="p3"),  
                                  @ColumnResult(name="created_date")})}) 

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
