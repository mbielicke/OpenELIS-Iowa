package org.openelis.entity;

/**
 * OrderContainer Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "OrderContainer.FetchByOrderId",
            query = "select distinct new org.openelis.domain.OrderContainerDO(o.id,o.orderId,o.containerId," +
                     "o.itemSequence,o.typeOfSampleId)"
                   + " from OrderContainer o where o.orderId = :id order by o.itemSequence")
                   
@Entity
@Table(name = "order_container")
@EntityListeners({AuditUtil.class})
public class OrderContainer implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer        id;

    @Column(name = "order_id")
    private Integer        orderId;

    @Column(name = "container_id")
    private Integer        containerId;

    @Column(name = "item_sequence")
    private Integer        itemSequence;

    @Column(name = "type_of_sample_id")
    private Integer        typeOfSampleId;

    @Transient
    private OrderContainer original;

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

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        if (DataBaseUtil.isDifferent(containerId, this.containerId))
            this.containerId = containerId;
    }

    public Integer getItemSequence() {
        return itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        if (DataBaseUtil.isDifferent(itemSequence, this.itemSequence))
            this.itemSequence = itemSequence;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        if (DataBaseUtil.isDifferent(typeOfSampleId, this.typeOfSampleId))
            this.typeOfSampleId = typeOfSampleId;
    }

    public void setClone() {
        try {
            original = (OrderContainer)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ORDER_CONTAINER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("order_id", orderId, original.orderId)
                 .setField("container_id", containerId, original.containerId, Constants.table().DICTIONARY)
                 .setField("item_sequence", itemSequence, original.itemSequence)
                 .setField("type_of_sample_id", typeOfSampleId, original.typeOfSampleId, Constants.table().DICTIONARY);

        return audit;
    }

}