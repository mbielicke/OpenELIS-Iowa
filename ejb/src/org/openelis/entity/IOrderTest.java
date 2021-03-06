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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
   @NamedQuery(name = "IOrderTest.FetchByIorderId",
               query = "select distinct new org.openelis.domain.IOrderTestViewDO(o.id, o.iorderId,"
                       + " o.itemSequence, o.sortOrder, o.testId, o.test.name, o.test.method.id,"
                       + " o.test.method.name, o.test.description, o.test.isActive)"
                       + " from IOrderTest o where o.iorderId = :id order by o.itemSequence, o.sortOrder"),
   @NamedQuery(name = "IOrderTest.FetchByIorderIds",
               query = "select distinct new org.openelis.domain.IOrderTestViewDO(o.id, o.iorderId,"
                       + " o.itemSequence, o.sortOrder, o.testId, o.test.name, o.test.method.id,"
                       + " o.test.method.name, o.test.description, o.test.isActive)"
                       + " from IOrderTest o where o.iorderId in ( :ids ) order by o.itemSequence, o.sortOrder")})
@Entity
@Table(name = "iorder_test")
@EntityListeners({AuditUtil.class})
public class IOrderTest implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer   id;

    @Column(name = "iorder_id")
    private Integer   iorderId;

    @Column(name = "item_sequence")
    private Integer   itemSequence;

    @Column(name = "sort_order")
    private Integer   sortOrder;

    @Column(name = "test_id")
    private Integer   testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test      test;

    @Transient
    private IOrderTest original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getIorderId() {
        return iorderId;
    }

    public void setIorderId(Integer iorderId) {
        if (DataBaseUtil.isDifferent(iorderId, this.iorderId))
            this.iorderId = iorderId;
    }

    public Integer getItemSequence() {
        return itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        if (DataBaseUtil.isDifferent(itemSequence, this.itemSequence))
            this.itemSequence = itemSequence;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
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
            original = (IOrderTest)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().IORDER_TEST);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("iorder_id", iorderId, original.iorderId)
                 .setField("item_sequence", itemSequence, original.itemSequence)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("test_id", testId, original.testId, Constants.table().TEST);

        return audit;
    }
}
