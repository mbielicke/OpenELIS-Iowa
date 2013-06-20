package org.openelis.entity;

/**
 * AnalysisQaevent Entity POJO for database
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
    @NamedQuery( name = "AnalysisQaevent.FetchByAnalysisId",
                query = "select new org.openelis.domain.AnalysisQaEventViewDO(q.id, q.analysisId, q.qaeventId, " +
                        "q.typeId, q.isBillable, q.qaEvent.name)"
                      + " from AnalysisQaevent q where q.analysisId = :id order by q.id"),
   @NamedQuery( name = "AnalysisQaevent.FetchInternalByAnalysisId",
               query = "select new org.openelis.domain.AnalysisQaEventViewDO(aq.id, aq.analysisId, aq.qaeventId, " +
                       "aq.typeId, aq.isBillable, q.name)"
                     + " from AnalysisQaevent aq left join aq.qaEvent q left join aq.dictionary d"
                     + " where aq.analysisId = :id and d.systemName = 'qaevent_internal' order by aq.id")})
@Entity
@Table(name = "analysis_qaevent")
@EntityListeners( {AuditUtil.class})
public class AnalysisQaevent implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer         id;

    @Column(name = "analysis_id")
    private Integer         analysisId;

    @Column(name = "qaevent_id")
    private Integer         qaeventId;

    @Column(name = "type_id")
    private Integer         typeId;

    @Column(name = "is_billable")
    private String          isBillable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaevent_id", insertable = false, updatable = false)
    private QaEvent         qaEvent;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private Dictionary    dictionary;

    @Transient
    private AnalysisQaevent original;

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

    public Integer getQaeventId() {
        return qaeventId;
    }

    public void setQaeventId(Integer qaeventId) {
        if (DataBaseUtil.isDifferent(qaeventId, this.qaeventId))
            this.qaeventId = qaeventId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))            
            this.typeId = typeId;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        if (DataBaseUtil.isDifferent(isBillable, this.isBillable))
            this.isBillable = isBillable;
    }

    public QaEvent getQaEvent() {
        return qaEvent;
    }

    public void setQaEvent(QaEvent qaEvent) {
        this.qaEvent = qaEvent;
    }
    
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setClone() {
        try {
            original = (AnalysisQaevent)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ANALYSIS_QAEVENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("analysis_id", analysisId, original.analysisId, ReferenceTable.ANALYSIS)
                 .setField("qaevent_id", qaeventId, original.qaeventId, ReferenceTable.QAEVENT)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("is_billable", isBillable, original.isBillable);

        return audit;
    }

}