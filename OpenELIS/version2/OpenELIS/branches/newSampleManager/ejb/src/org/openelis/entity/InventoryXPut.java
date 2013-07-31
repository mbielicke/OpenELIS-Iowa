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
 * TransReceiptLocation Entity POJO for database
 */

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery(name = "InventoryXPut.FetchByInventoryReceiptId",
                           query = "select distinct new org.openelis.domain.InventoryXPutViewDO(tr.id, tr.inventoryReceiptId, tr.inventoryLocationId, tr.quantity,"
                                   + "il.inventoryItemId, il.lotNumber, il.storageLocationId, il.quantityOnhand, il.expirationDate, ii.name, ii.storeId,"
                                   + "s.name, su.description, s.location, oi.id, ir.receivedDate, ir.unitCost, ir.externalReference, oi.orderId)"
                                   + " from InventoryXPut tr left join tr.inventoryLocation il left join tr.inventoryReceipt ir left join ir.orderItem oi left join il.storageLocation s left join s.storageUnit su"
                                   + " left join il.inventoryItem ii where tr.inventoryReceiptId = :id"),
               @NamedQuery(name = "InventoryXPut.FetchByInventoryLocationId",
                           query = "select distinct new org.openelis.domain.InventoryXPutViewDO(tr.id, tr.inventoryReceiptId, tr.inventoryLocationId, tr.quantity,"
                                   + "il.inventoryItemId, il.lotNumber, il.storageLocationId, il.quantityOnhand, il.expirationDate, ii.name, ii.storeId,"
                                   + "s.name, su.description, s.location, oi.id, ir.receivedDate, ir.unitCost, ir.externalReference, oi.orderId)"
                                   + " from InventoryXPut tr left join tr.inventoryLocation il left join tr.inventoryReceipt ir left join ir.orderItem oi left join il.storageLocation s left join s.storageUnit su left join il.inventoryItem ii where tr.inventoryLocationId = :id"),
               @NamedQuery(name = "InventoryXPut.FetchByOrderId",
                           query = "select distinct new org.openelis.domain.InventoryXPutViewDO(tr.id, tr.inventoryReceiptId, tr.inventoryLocationId, tr.quantity,"
                                   + "il.inventoryItemId, il.lotNumber, il.storageLocationId, il.quantityOnhand, il.expirationDate, ii.name, ii.storeId,"
                                   + "s.name, su.description, s.location, oi.id, ir.receivedDate, ir.unitCost, ir.externalReference, oi.orderId)"
                                   + " from InventoryXPut tr left join tr.inventoryReceipt ir left join ir.orderItem oi left join tr.inventoryLocation il left join il.inventoryItem ii left join il.storageLocation s left join s.storageUnit su where oi.orderId = :id"),
               @NamedQuery(name = "InventoryXPut.FetchByOrderIds",
                           query = "select distinct new org.openelis.domain.InventoryXPutViewDO(tr.id, tr.inventoryReceiptId, tr.inventoryLocationId, tr.quantity,"
                                   + "il.inventoryItemId, il.lotNumber, il.storageLocationId, il.quantityOnhand, il.expirationDate, ii.name, ii.storeId,"
                                   + "s.name, su.description, s.location, oi.id, ir.receivedDate, ir.unitCost, ir.externalReference, oi.orderId)"
                                   + " from InventoryXPut tr left join tr.inventoryReceipt ir left join ir.orderItem oi left join tr.inventoryLocation il left join il.inventoryItem ii left join il.storageLocation s left join s.storageUnit su where oi.orderId in ( :ids )")})
@Entity
@Table(name = "inventory_x_put")
@EntityListeners({AuditUtil.class})
public class InventoryXPut implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "inventory_receipt_id")
    private Integer           inventoryReceiptId;

    @Column(name = "inventory_location_id")
    private Integer           inventoryLocationId;

    @Column(name = "quantity")
    private Integer           quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
    private InventoryLocation inventoryLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
    private InventoryReceipt  inventoryReceipt;

    @Transient
    private InventoryXPut     original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInventoryReceiptId() {
        return inventoryReceiptId;
    }

    public void setInventoryReceiptId(Integer inventoryReceiptId) {
        if (DataBaseUtil.isDifferent(inventoryReceiptId, this.inventoryReceiptId))
            this.inventoryReceiptId = inventoryReceiptId;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        if (DataBaseUtil.isDifferent(inventoryLocationId, this.inventoryLocationId))
            this.inventoryLocationId = inventoryLocationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public InventoryLocation getInventoryLocation() {
        return inventoryLocation;
    }

    public InventoryReceipt getInventoryReceipt() {
        return inventoryReceipt;
    }

    public void setInventoryReceipt(InventoryReceipt inventoryReceipt) {
        this.inventoryReceipt = inventoryReceipt;
    }

    public void setClone() {
        try {
            original = (InventoryXPut)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().INVENTORY_X_PUT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_receipt_id",
                           inventoryReceiptId,
                           original.inventoryReceiptId,
                           Constants.table().INVENTORY_X_PUT)
                 .setField("inventory_location_id",
                           inventoryLocationId,
                           original.inventoryLocationId,
                           Constants.table().INVENTORY_LOCATION)
                 .setField("quantity", quantity, original.quantity);

        return audit;
    }
}
