package org.openelis.entity;

/**
 * SampleQaevent Entity POJO for database
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

@NamedQueries( {
    @NamedQuery( name = "SampleQaevent.FetchBySampleId",
                query = "select new org.openelis.domain.SampleQaEventViewDO(sq.id, sq.sampleId, sq.qaeventId, sq.typeId, sq.isBillable, sq.qaEvent.name, sq.qaEvent.reportingText)"
                      + " from SampleQaevent sq where sq.sampleId = :id order by sq.id"),
   @NamedQuery( name = "SampleQaevent.FetchInternalBySampleId",
               query = "select new org.openelis.domain.SampleQaEventViewDO(sq.id, sq.sampleId, sq.qaeventId, sq.typeId, sq.isBillable, q.name, q.reportingText)"
                     + " from SampleQaevent sq left join sq.qaEvent q left join sq.dictionary d"
                     + " where sq.sampleId = :id and d.systemName = 'qaevent_internal' order by sq.id")})
@Entity 
@Table(name = "sample_qaevent")
@EntityListeners( {AuditUtil.class})
public class SampleQaevent implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer       id;

    @Column(name = "sample_id")
    private Integer       sampleId;

    @Column(name = "qaevent_id")
    private Integer       qaeventId;

    @Column(name = "type_id")
    private Integer       typeId;

    @Column(name = "is_billable")
    private String        isBillable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qaevent_id", insertable = false, updatable = false)
    private QaEvent       qaEvent;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private Dictionary    dictionary;

    @Transient
    private SampleQaevent original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
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
            original = (SampleQaevent)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SAMPLE_QAEVENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("qaevent_id", qaeventId, original.qaeventId, ReferenceTable.QAEVENT)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("is_billable", isBillable, original.isBillable);

        return audit;
    }
}
