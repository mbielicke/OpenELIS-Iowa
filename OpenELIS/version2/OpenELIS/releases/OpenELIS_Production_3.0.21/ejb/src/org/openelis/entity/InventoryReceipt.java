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
 * InventoryReceipt Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
 
@NamedQueries( {
    @NamedQuery ( name = "InventoryReceipt.FetchById",
                 query = "select distinct new org.openelis.domain.InventoryReceiptViewDO(r.id, r.inventoryItemId, r.iorderItemId, r.organizationId," +
    		             "r.receivedDate, r.quantityReceived, r.unitCost, r.qcReference, r.externalReference, r.upc, i.quantity, o.id," +
    		             "o.externalOrderNumber, i.unitCost)"
                       + " from InventoryReceipt r left join r.iorderItem i left join i.iorder o where r.id = :id"),
    @NamedQuery ( name = "InventoryReceipt.FetchByUpc",
                 query = "select distinct new org.openelis.domain.IdNameVO(r.inventoryItemId, r.upc, i.name)"
                       + " from InventoryReceipt r left join r.inventoryItem i where r.upc like :upc")})

@Entity
@Table(name = "inventory_receipt")
@EntityListeners({AuditUtil.class})
public class InventoryReceipt implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                   id;

    @Column(name = "inventory_item_id")
    private Integer                   inventoryItemId;

    @Column(name = "iorder_item_id")
    private Integer                   iorderItemId;

    @Column(name = "organization_id")
    private Integer                   organizationId;

    @Column(name = "received_date")
    private Date                      receivedDate;

    @Column(name = "quantity_received")
    private Integer                   quantityReceived;

    @Column(name = "unit_cost")
    private Double                    unitCost;

    @Column(name = "qc_reference")
    private String                    qcReference;

    @Column(name = "external_reference")
    private String                    externalReference;

    @Column(name = "upc")
    private String                    upc;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
    private InventoryItem             inventoryItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization              organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_item_id", insertable = false, updatable = false)
    private IOrderItem                 iorderItem;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
    private Collection<InventoryXPut> inventoryXPut;

    @Transient
    private InventoryReceipt          original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        if (DataBaseUtil.isDifferent(inventoryItemId, this.inventoryItemId))
            this.inventoryItemId = inventoryItemId;
    }

    public Integer getIorderItemId() {
        return iorderItemId;
    }

    public void setIorderItemId(Integer iorderItemId) {
        if (DataBaseUtil.isDifferent(iorderItemId, this.iorderItemId))
            this.iorderItemId = iorderItemId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }

    public Datetime getReceivedDate() {
        return DataBaseUtil.toYD(receivedDate);
    }

    public void setReceivedDate(Datetime receivedDate) {
        if (DataBaseUtil.isDifferentYD(receivedDate, this.receivedDate))
            this.receivedDate = DataBaseUtil.toDate(receivedDate);
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        if (DataBaseUtil.isDifferent(quantityReceived, this.quantityReceived))
            this.quantityReceived = quantityReceived;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        if (DataBaseUtil.isDifferent(unitCost, this.unitCost))
            this.unitCost = unitCost;
    }

    public String getQcReference() {
        return qcReference;
    }

    public void setQcReference(String qcReference) {
        if (DataBaseUtil.isDifferent(qcReference, this.qcReference))
            this.qcReference = qcReference;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        if (DataBaseUtil.isDifferent(externalReference, this.externalReference))
            this.externalReference = externalReference;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        if (DataBaseUtil.isDifferent(upc, this.upc))
            this.upc = upc;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setInventoryXPut(Collection<InventoryXPut> inventoryXPut) {
        this.inventoryXPut = inventoryXPut;
    }

    public Collection<InventoryXPut> getInventoryXPut() {
        return inventoryXPut;
    }

    public IOrderItem getIorderItem() {
        return iorderItem;
    }

    public void setIorderItem(IOrderItem iorderItem) {
        this.iorderItem = iorderItem;
    }

    public void setClone() {
        try {
            original = (InventoryReceipt)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().INVENTORY_RECEIPT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId, Constants.table().INVENTORY_ITEM)
                 .setField("iorder_item_id", iorderItemId, original.iorderItemId, Constants.table().IORDER_ITEM)
                 .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
                 .setField("received_date", receivedDate, original.receivedDate)
                 .setField("quantity_received", quantityReceived, original.quantityReceived)
                 .setField("unit_cost", unitCost, original.unitCost)
                 .setField("qc_reference", qcReference, original.qcReference)
                 .setField("external_reference", externalReference, original.externalReference)
                 .setField("upc", upc, original.upc);

        return audit;
    }
}
