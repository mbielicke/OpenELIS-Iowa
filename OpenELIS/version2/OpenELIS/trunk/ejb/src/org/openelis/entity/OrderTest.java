package org.openelis.entity;

/**
 * OrderTest Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "OrderTest.FetchByOrderId",
            query = "select distinct new org.openelis.domain.OrderTestViewDO(o.id, o.orderId, o.sortOrder," +
            		"o.testId, o.test.name, o.test.method.name, o.test.description)"
                  + " from OrderTest o where o.orderId = :id")

@Entity
@Table(name = "order_test")
@EntityListeners( {AuditUtil.class})
public class OrderTest implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer   id;

    @Column(name = "order_id")
    private Integer   orderId;

    @Column(name = "sort_order")
    private Integer   sortOrder;

    @Column(name = "test_id")
    private Integer   testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test      test;

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
        return sortOrder;
    }

    public void setSequence(Integer sequence) {
        if (DataBaseUtil.isDifferent(sequence, this.sortOrder))
            this.sortOrder = sequence;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }   
    
    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
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
                 .setField("sequence", sortOrder, original.sortOrder)
                 .setField("test_id", testId, original.testId, ReferenceTable.TEST);

        return audit;
    }

}
