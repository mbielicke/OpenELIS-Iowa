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
 * Shipping Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Shipping.FetchById",
                query = "select new org.openelis.domain.ShippingViewDO(s.id,s.statusId," +
                		"s.shippedFromId,s.shippedToId,s.processedBy,s.processedDate," +
                		"s.shippedMethodId,s.shippedDate,s.numberOfPackages,s.cost)" 
                      + " from Shipping s where s.id = :id"),
    @NamedQuery( name = "Shipping.FetchByReferenceTableAndReferenceId",
               query = "select new org.openelis.domain.ShippingViewDO(s.id,s.statusId," +
                       "s.shippedFromId,s.shippedToId,s.processedBy,s.processedDate," +
                       "s.shippedMethodId,s.shippedDate,s.numberOfPackages,s.cost)" 
                     + " from Shipping s left join s.shippingItem i where i.referenceTableId = :referenceTableId and i.referenceId = :referenceId")})
@Entity
@Table(name = "shipping")
@EntityListeners( {AuditUtil.class})
public class Shipping implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                      id;

    @Column(name = "status_id")
    private Integer                      statusId;

    @Column(name = "shipped_from_id")
    private Integer                      shippedFromId;

    @Column(name = "shipped_to_id")
    private Integer                      shippedToId;

    @Column(name = "processed_by")
    private String                       processedBy;

    @Column(name = "processed_date")
    private Date                         processedDate;

    @Column(name = "shipped_method_id")
    private Integer                      shippedMethodId;

    @Column(name = "shipped_date")
    private Date                         shippedDate;

    @Column(name = "number_of_packages")
    private Integer                      numberOfPackages;

    @Column(name = "cost")
    private Double                       cost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipped_to_id", insertable = false, updatable = false)
    private Organization                 shippedTo;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", insertable = false, updatable = false)
    private Collection<ShippingItem>     shippingItem;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", insertable = false, updatable = false)
    private Collection<ShippingTracking> shippingTracking;

    @Transient
    private Shipping                     original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Integer getShippedFromId() {
        return shippedFromId;
    }

    public void setShippedFromId(Integer shippedFromId) {
        if (DataBaseUtil.isDifferent(shippedFromId, this.shippedFromId))
            this.shippedFromId = shippedFromId;
    }

    public Integer getShippedToId() {
        return shippedToId;
    }

    public void setShippedToId(Integer shippedToId) {
        if (DataBaseUtil.isDifferent(shippedToId, this.shippedToId))
            this.shippedToId = shippedToId;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        if (DataBaseUtil.isDifferent(processedBy, this.processedBy))
            this.processedBy = processedBy;
    }

    public Datetime getProcessedDate() {
        return DataBaseUtil.toYD(processedDate);
    }

    public void setProcessedDate(Datetime processedDate) {
        if (DataBaseUtil.isDifferentYD(processedDate, this.processedDate))
            this.processedDate = DataBaseUtil.toDate(processedDate);
    }

    public Integer getShippedMethodId() {
        return shippedMethodId;
    }

    public void setShippedMethodId(Integer shippedMethodId) {
        if (DataBaseUtil.isDifferent(shippedMethodId, this.shippedMethodId))
            this.shippedMethodId = shippedMethodId;
    }

    public Datetime getShippedDate() {
        return DataBaseUtil.toYD(shippedDate);
    }

    public void setShippedDate(Datetime shippedDate) {
        if (DataBaseUtil.isDifferentYD(shippedDate, this.shippedDate))
            this.shippedDate = DataBaseUtil.toDate(shippedDate);
    }

    public Integer getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Integer numberOfPackages) {
        if (DataBaseUtil.isDifferent(numberOfPackages, this.numberOfPackages))
            this.numberOfPackages = numberOfPackages;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if (DataBaseUtil.isDifferent(cost, this.cost))
            this.cost = cost;
    }
    
    public Collection<ShippingItem> getShippingItem() {
        return shippingItem;
    }

    public void setShippingItem(Collection<ShippingItem> shippingItem) {
        this.shippingItem = shippingItem;
    }

    public Collection<ShippingTracking> getShippingTracking() {
        return shippingTracking;
    }

    public void setShippingTracking(Collection<ShippingTracking> shippingTracking) {
        this.shippingTracking = shippingTracking;
    }

    public Organization getShippedTo() {
        return shippedTo;
    }

    public void setShippedTo(Organization shippedTo) {
        this.shippedTo = shippedTo;
    }

    public void setClone() {
        try {
            original = (Shipping)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SHIPPING);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("status_id", statusId, original.statusId)
                 .setField("shipped_from_id", shippedFromId, original.shippedFromId)
                 .setField("shipped_to_id", shippedToId, original.shippedToId)
                 .setField("processed_by", processedBy, original.processedBy)
                 .setField("processed_date", processedDate, original.processedDate)
                 .setField("shipped_method_id", shippedMethodId, original.shippedMethodId)
                 .setField("shipped_date", shippedDate, original.shippedDate)
                 .setField("number_of_packages", numberOfPackages, original.numberOfPackages)
                 .setField("cost", cost, original.cost);

        return audit;
    }
}
