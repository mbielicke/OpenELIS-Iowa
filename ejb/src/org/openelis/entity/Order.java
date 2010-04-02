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
    @NamedQuery( name = "Order.FetchById",
                query = "select new org.openelis.domain.OrderViewDO(o.id,o.description,o.statusId,o.orderedDate," +
                		"o.neededInDays,o.requestedBy,o.costCenterId,o.organizationId,o.type,o.externalOrderNumber," +
                		"o.reportToId,o.billToId,o.shipFromId)"
                	  + " from Order o where o.id = :id"),
    @NamedQuery( name = "Order.FetchByDescription",
                query = "select new org.openelis.domain.IdNameVO(o.id,o.description)"
                      + " from Order o where o.description like :description")})
/*
    @NamedQuery(name = "Order.OrderExternalKit", query = "select new org.openelis.domain.OrderDO(o.id, o.statusId, o.orderedDate, o.neededInDays, o.requestedBy, o.costCenterId, o.organizationId, " +
                            " oo.name, oo.address.multipleUnit, oo.address.streetAddress, oo.address.city, oo.address.state, oo.address.zipCode, o.isExternal, o.externalOrderNumber, " +
                            " o.reportToId, o.billToId, o.shipFromId, o.description) from Order o left join o.organization oo where o.id = :id"),
    @NamedQuery(name = "Order.ReportToBillTo", query = "select new org.openelis.domain.BillToReportToDO(o.billToId, o.billTo.name, o.billTo.address.multipleUnit, o.billTo.address.streetAddress," +
                            " o.billTo.address.city, o.billTo.address.state, o.billTo.address.zipCode, o.reportToId, o.reportTo.name, o.reportTo.address.multipleUnit, o.reportTo.address.streetAddress, " +
                            " o.reportTo.address.city, o.reportTo.address.state, o.reportTo.address.zipCode) from Order o where o.id = :id"),
    @NamedQuery(name = "Order.ReceiptsForOrder", query = "select new org.openelis.domain.InventoryReceiptDO(r.id,r.inventoryItemId, r.inventoryItem.name,r.organizationId,r.receivedDate,r.quantityReceived, " +
                            " r.unitCost,r.qcReference,r.externalReference,r.upc) from InventoryReceipt r left join r.orderItems o where o.orderId = :id " +
                            " order by r.receivedDate desc "),
    @NamedQuery(name = "Order.LocsForOrder", query = "select new org.openelis.domain.InventoryLocationDO(loc.id, loc.inventoryItemId, loc.inventoryItem.name, loc.lotNumber, childLoc.name,childLoc.location, "+
                            " parentLoc.name, childLoc.storageUnit.description, trans.quantity, loc.expirationDate) " +
                            " from InventoryXUse trans left join trans.orderItem oi left join trans.inventoryLocation loc left join loc.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc where oi.orderId = :id " +
                            " order by trans.id "),
    @NamedQuery(name = "Order.descriptionAutoLookup", query = "select distinct new org.openelis.domain.IdNameDO(o.description) from Order o where o.description like :desc"),
    @NamedQuery(name = "Order.FillOrderSubInfo", query = "select new org.openelis.domain.FillOrderDO(o.requestedBy, o.costCenterId, add.multipleUnit, " +
            " add.streetAddress, add.city, add.state, add.zipCode) from Order o LEFT JOIN o.organization orgz LEFT JOIN orgz.address add" + 
            " where o.id = :id ")})
*/  

@Entity
@Table(name = "order")
@EntityListeners( {AuditUtil.class})
public class Order implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer               id;

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

    @Column(name = "type")
    private String                type;

    @Column(name = "external_order_number")
    private String                externalOrderNumber;

    @Column(name = "report_to_id")
    private Integer               reportToId;

    @Column(name = "bill_to_id")
    private Integer               billToId;

    @Column(name = "ship_from_id")
    private Integer               shipFromId;

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

    @Transient
    private Order                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
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

    public Integer getBillToId() {
        return billToId;
    }

    public void setBillToId(Integer billToId) {
        if (DataBaseUtil.isDifferent(billToId, this.billToId))
            this.billToId = billToId;
    }

    public Integer getShipFromId() {
        return shipFromId;
    }

    public void setShipFromId(Integer shipFromId) {
        if (DataBaseUtil.isDifferent(shipFromId, this.shipFromId))
            this.shipFromId = shipFromId;
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
                 .setField("description", description, original.description)
                 .setField("status_id", statusId, original.statusId)
                 .setField("ordered_date", orderedDate, original.orderedDate)
                 .setField("needed_in_days", neededInDays, original.neededInDays)
                 .setField("requested_by", requestedBy, original.requestedBy)
                 .setField("cost_center_id", costCenterId, original.costCenterId)
                 .setField("organization_id", organizationId, original.organizationId)
                 .setField("type", type, original.type)
                 .setField("external_order_number", externalOrderNumber, original.externalOrderNumber)
                 .setField("report_to_id", reportToId, original.reportToId)
                 .setField("bill_to_id", billToId, original.billToId)
                 .setField("ship_from_id", shipFromId, original.shipFromId);

        return audit;
    }
}