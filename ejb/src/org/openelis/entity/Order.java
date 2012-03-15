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
 * Order Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Order.FetchById",
                query = "select new org.openelis.domain.OrderViewDO(o.id,o.parentOrderId,o.description,o.statusId,o.orderedDate," +
                		"o.neededInDays,o.requestedBy,o.costCenterId,o.organizationId,o.organizationAttention," +
                		"o.type,o.externalOrderNumber,o.reportToId,o.reportToAttention,o.billToId,o.billToAttention,o.shipFromId,o.numberOfForms)"
                	  + " from Order o where o.id = :id"),
    @NamedQuery( name = "Order.FetchByDescription",
                query = "select distinct new org.openelis.domain.IdNameVO(o.id,o.description)"
                      + " from Order o where o.description like :description"),
    @NamedQuery( name = "Order.FetchByShippingItemId",
                query  = "select new org.openelis.domain.OrderViewDO(o.id,o.parentOrderId,o.description,o.statusId,o.orderedDate," +
                        "o.neededInDays,o.requestedBy,o.costCenterId,o.organizationId,o.organizationAttention," +
                        "o.type,o.externalOrderNumber,o.reportToId,o.reportToAttention,o.billToId,o.billToAttention,o.shipFromId,o.numberOfForms)"
                      + " from Order o left join o.orderItem i "
                      +	" where i.id = (select s.referenceId from ShippingItem s where s.referenceTableId = org.openelis.domain.ReferenceTable.ORDER_ITEM and s.id = :id)"),
    @NamedQuery( name = "Order.FetchByShippingId",
                query  = "select new org.openelis.domain.OrderViewDO(o.id,o.parentOrderId,o.description,o.statusId,o.orderedDate," +
                         "o.neededInDays,o.requestedBy,o.costCenterId,o.organizationId,o.organizationAttention," +
                         "o.type,o.externalOrderNumber,o.reportToId,o.reportToAttention,o.billToId,o.billToAttention,o.shipFromId,o.numberOfForms)"
                       + " from Order o left join o.orderItem i "
                       + " where i.id in (select s.referenceId from ShippingItem s where s.referenceTableId = org.openelis.domain.ReferenceTable.ORDER_ITEM and s.shippingId = :shippingId)")})

@Entity
@Table(name = "order")
@EntityListeners( {AuditUtil.class})
public class Order implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;
    
    @Column(name = "parent_order_id")
    private Integer               parentOrderId;

    @Column(name = "description")
    private String                description;

    @Column(name = "status_id")
    private Integer               statusId;

    @Column(name = "ordered_date")
    private Date                  orderedDate;

    @Column(name = "needed_in_days")
    private Integer               neededInDays;

    @Column(name = "requested_by")
    private String                requestedBy;

    @Column(name = "cost_center_id")
    private Integer               costCenterId;

    @Column(name = "organization_id")
    private Integer               organizationId;
    
    @Column(name = "organization_attention")
    private String               organizationAttention;

    @Column(name = "type")
    private String                type;

    @Column(name = "external_order_number")
    private String                externalOrderNumber;

    @Column(name = "report_to_id")
    private Integer               reportToId;
    
    @Column(name = "report_to_attention")
    private String               reportToAttention;

    @Column(name = "bill_to_id")
    private Integer               billToId;
    
    @Column(name = "bill_to_attention")
    private String               billToAttention;

    @Column(name = "ship_from_id")
    private Integer               shipFromId;
    
    @Column(name = "number_of_forms")
    private Integer               numberOfForms;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization          organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_to_id", insertable = false, updatable = false)
    private Organization          reportTo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_to_id", insertable = false, updatable = false)
    private Organization          billTo;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Collection<OrderItem> orderItem;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Collection<OrderContainer> orderContainer;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Collection<OrderTest> orderTest;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private Collection<AuxData> auxData;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Collection<OrderRecurrence>     orderRecurrence;    

    @Transient
    private Order                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(Integer parentOrderId) {
        if (DataBaseUtil.isDifferent(parentOrderId, this.parentOrderId))
            this.parentOrderId = parentOrderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Datetime getOrderedDate() {
        return DataBaseUtil.toYD(orderedDate);
    }

    public void setOrderedDate(Datetime orderedDate) {
        if (DataBaseUtil.isDifferentYD(orderedDate, this.orderedDate))
            this.orderedDate = DataBaseUtil.toDate(orderedDate);
    }

    public Integer getNeededInDays() {
        return neededInDays;
    }

    public void setNeededInDays(Integer neededInDays) {
        if (DataBaseUtil.isDifferent(neededInDays, this.neededInDays))
            this.neededInDays = neededInDays;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        if (DataBaseUtil.isDifferent(requestedBy, this.requestedBy))
            this.requestedBy = requestedBy;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        if (DataBaseUtil.isDifferent(costCenterId, this.costCenterId))
            this.costCenterId = costCenterId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }
    
    
    public String getOrganizationAttention() {        
        return organizationAttention;
    }

    public void setOrganizationAttention(String organizationAttention) {
        if (DataBaseUtil.isDifferent(organizationAttention, this.organizationAttention))
        this.organizationAttention = organizationAttention;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (DataBaseUtil.isDifferent(type, this.type))
            this.type = type;
    }

    public String getExternalOrderNumber() {
        return externalOrderNumber;
    }

    public void setExternalOrderNumber(String externalOrderNumber) {
        if (DataBaseUtil.isDifferent(externalOrderNumber, this.externalOrderNumber))
            this.externalOrderNumber = externalOrderNumber;
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public void setReportToId(Integer reportToId) {
        if (DataBaseUtil.isDifferent(reportToId, this.reportToId))
            this.reportToId = reportToId;
    }

    public String getReportToAttention() {
        return reportToAttention;
    }

    public void setReportToAttention(String reportToAttention) {
        if (DataBaseUtil.isDifferent(reportToAttention, this.reportToAttention))
            this.reportToAttention = reportToAttention;
    }

    public Integer getBillToId() {
        return billToId;
    }

    public void setBillToId(Integer billToId) {
        if (DataBaseUtil.isDifferent(billToId, this.billToId))
            this.billToId = billToId;
    }    

    public String getBillToAttention() {
        return billToAttention;
    }

    public void setBillToAttention(String billToAttention) {
        if (DataBaseUtil.isDifferent(billToAttention, this.billToAttention))
            this.billToAttention = billToAttention;
    }

    public Integer getShipFromId() {
        return shipFromId;
    }

    public void setShipFromId(Integer shipFromId) {
        if (DataBaseUtil.isDifferent(shipFromId, this.shipFromId))
            this.shipFromId = shipFromId;
    }
    
    public Integer getNumberOfForms() {
        return numberOfForms;
    }

    public void setNumberOfForms(Integer numberOfForms) {
        if (DataBaseUtil.isDifferent(numberOfForms, this.numberOfForms))
            this.numberOfForms = numberOfForms;
    }    

    public Organization getBillTo() {
        return billTo;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Organization getReportTo() {
        return reportTo;
    }

    public Collection<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(Collection<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
    
    public Collection<OrderContainer> getOrderContainer() {
        return orderContainer;
    }

    public void setOrderContainer(Collection<OrderContainer> orderContainer) {
        this.orderContainer = orderContainer;
    }

    public Collection<OrderTest> getOrderTest() {
        return orderTest;
    }

    public void setOrderTest(Collection<OrderTest> orderTest) {
        this.orderTest = orderTest;
    }
    
    public Collection<AuxData> getAuxData() {
        return auxData;
    }
    
    public void setAuxData(Collection<AuxData> auxData) {
        this.auxData = auxData;
    }  

    public Collection<OrderRecurrence> getOrderRecurrence() {
        return orderRecurrence;
    }

    public void setOrderRecurrence(Collection<OrderRecurrence> orderRecurrence) {
        this.orderRecurrence = orderRecurrence;
    }

    public void setClone() {
        try {
            original = (Order)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ORDER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("parentOrderId", parentOrderId, original.parentOrderId)
                 .setField("description", description, original.description)
                 .setField("status_id", statusId, original.statusId)
                 .setField("ordered_date", orderedDate, original.orderedDate)
                 .setField("needed_in_days", neededInDays, original.neededInDays)
                 .setField("requested_by", requestedBy, original.requestedBy)
                 .setField("cost_center_id", costCenterId, original.costCenterId)
                 .setField("organization_id", organizationId, original.organizationId)
                 .setField("organization_attention", organizationAttention, original.organizationAttention)
                 .setField("type", type, original.type)
                 .setField("external_order_number", externalOrderNumber, original.externalOrderNumber)
                 .setField("report_to_id", reportToId, original.reportToId)
                 .setField("bill_to_id", billToId, original.billToId)
                 .setField("ship_from_id", shipFromId, original.shipFromId)
                 .setField("number_of_forms", numberOfForms, original.numberOfForms);

        return audit;
    }
}