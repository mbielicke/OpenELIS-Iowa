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
    @NamedQuery( name = "IOrderContainer.FetchByIorderId",
                query = "select distinct new org.openelis.domain.IOrderContainerDO(o.id,o.iorderId,o.containerId," +
                        "o.itemSequence,o.typeOfSampleId)"
                      + " from IOrderContainer o where o.iorderId = :id order by o.itemSequence"),
    @NamedQuery( name = "IOrderContainer.FetchByIorderIds",
                query = "select distinct new org.openelis.domain.IOrderContainerDO(o.id,o.iorderId,o.containerId," +
                        "o.itemSequence,o.typeOfSampleId)"
                      + " from IOrderContainer o where o.iorderId in ( :ids ) order by o.itemSequence")})
                   
@Entity
@Table(name = "iorder_container")
@EntityListeners({AuditUtil.class})
public class IOrderContainer implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer        id;

    @Column(name = "iorder_id")
    private Integer        iorderId;

    @Column(name = "container_id")
    private Integer        containerId;

    @Column(name = "item_sequence")
    private Integer        itemSequence;

    @Column(name = "type_of_sample_id")
    private Integer        typeOfSampleId;

    @Transient
    private IOrderContainer original;

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
            original = (IOrderContainer)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().IORDER_CONTAINER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("iorder_id", iorderId, original.iorderId)
                 .setField("container_id", containerId, original.containerId, Constants.table().DICTIONARY)
                 .setField("item_sequence", itemSequence, original.itemSequence)
                 .setField("type_of_sample_id", typeOfSampleId, original.typeOfSampleId, Constants.table().DICTIONARY);

        return audit;
    }
}
