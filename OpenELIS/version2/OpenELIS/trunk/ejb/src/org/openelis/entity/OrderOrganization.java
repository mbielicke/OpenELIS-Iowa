/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.entity;

/**
 * OrderOrganization Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "OrderOrganization.FetchByOrderId",
             query = "select new org.openelis.domain.OrderOrganizationViewDO(oo.id, " +
                     "oo.orderId, oo.organizationId, oo.organizationAttention, " +
                     "oo.typeId, o.name, o.address.multipleUnit, o.address.streetAddress, " +
                     "o.address.city, o.address.state, o.address.zipCode, o.address.workPhone, " +
                     "o.address.faxPhone, o.address.country)"
                   + " from OrderOrganization oo LEFT JOIN oo.organization o where oo.orderId = :id")
                   
@Entity
@Table(name = "order_organization")
@EntityListeners({AuditUtil.class})
public class OrderOrganization implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "order_id")
    private Integer           orderId;

    @Column(name = "organization_id")
    private Integer           organizationId;

    @Column(name = "organization_attention")
    private String            organizationAttention;

    @Column(name = "type_id")
    private Integer           typeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order             order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization      organization;

    @Transient
    private OrderOrganization original;

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

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }

    public String getOrganizationAttetnion() {
        return organizationAttention;
    }

    public void setOrganizationAttention(String organizationAttention) {
        if (DataBaseUtil.isDifferent(organizationAttention, this.organizationAttention))
            this.organizationAttention = organizationAttention;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Order getorder() {
        return order;
    }

    public void setorder(Order order) {
        this.order = order;
    }

    public void setClone() {
        try {
            original = (OrderOrganization)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ORDER_ORGANIZATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("order_id", orderId, original.orderId)
                 .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
                 .setField("organization_attention", organizationAttention, original.organizationAttention)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY);

        return audit;
    }
}
