package org.openelis.entity;

/**
 * OrderTest Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "order_test")
@EntityListeners( {AuditUtil.class})
public class OrderTest implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer   id;

    @Column(name = "order_id")
    private Integer   orderId;

    @Column(name = "sequence")
    private Integer   sequence;

    @Column(name = "reference_id")
    private Integer   referenceId;

    @Column(name = "reference_table_id")
    private Integer   referenceTableId;

    @Transient
    private OrderTest original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (DataBaseUtil.isDifferent(orderId, this.orderId))
            this.orderId = orderId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        if (DataBaseUtil.isDifferent(sequence, this.sequence))
            this.sequence = sequence;
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

    public void setClone() {
        try {
            original = (OrderTest)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ORDER_TEST);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("order_id", orderId, original.orderId)
                 .setField("sequence", sequence, original.sequence)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId);

        return audit;
    }

}
