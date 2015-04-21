/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * OrderItem Entity POJO for database 
  */

import java.util.Collection;

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
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "IOrderItem.FetchByIorderId",
                query = "select distinct new org.openelis.domain.IOrderItemViewDO(o.id,o.iorderId,o.inventoryItemId," +
                		"o.quantity,o.catalogNumber,o.unitCost,i.name,i.storeId,i.productUri)"
                      + " from IOrderItem o left join o.inventoryItem i where o.iorderId = :id"),
    @NamedQuery( name = "IOrderItem.FetchById",
                query = "select distinct new org.openelis.domain.IOrderItemViewDO(o.id,o.iorderId,o.inventoryItemId," +
                        "o.quantity,o.catalogNumber,o.unitCost,i.name,i.storeId,i.productUri)"
                      + " from IOrderItem o left join o.inventoryItem i where o.id = :id"),
    @NamedQuery( name = "IOrderItem.FetchByIorderIds",
                query = "select distinct new org.openelis.domain.IOrderItemViewDO(o.id,o.iorderId,o.inventoryItemId," +
                        "o.quantity,o.catalogNumber,o.unitCost,i.name,i.storeId,i.productUri)"
                      + " from IOrderItem o left join o.inventoryItem i where o.iorderId in (:ids)")})                      

@Entity
@Table(name = "iorder_item")
@EntityListeners({AuditUtil.class})
public class IOrderItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                      id;

    @Column(name = "iorder_id")
    private Integer                      iorderId;

    @Column(name = "inventory_item_id")
    private Integer                      inventoryItemId;

    @Column(name = "quantity")
    private Integer                      quantity;

    @Column(name = "catalog_number")
    private String                       catalogNumber;

    @Column(name = "unit_cost")
    private Double                       unitCost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
    private InventoryItem                inventoryItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_id", insertable = false, updatable = false)
    private IOrder                        iorder;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_item_id", insertable = false, updatable = false)
    private Collection<InventoryXUse>    inventoryXUse;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_item_id", insertable = false, updatable = false)
    private Collection<InventoryReceipt> inventoryReceipt;

    @Transient
    private IOrderItem                    original;

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

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        if (DataBaseUtil.isDifferent(inventoryItemId, this.inventoryItemId))
            this.inventoryItemId = inventoryItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        if (DataBaseUtil.isDifferent(unitCost, this.unitCost))
            this.unitCost = unitCost;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        if (DataBaseUtil.isDifferent(catalogNumber, this.catalogNumber))
            this.catalogNumber = catalogNumber;
    }

    public IOrder getIorder() {
        return iorder;
    }

    public void setIorder(IOrder iorder) {
        this.iorder = iorder;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Collection<InventoryReceipt> getInventoryReceipt() {
        return inventoryReceipt;
    }

    public void setInventoryReceipt(Collection<InventoryReceipt> inventoryReceipt) {
        this.inventoryReceipt = inventoryReceipt;
    }

    public Collection<InventoryXUse> getInventoryXUse() {
        return inventoryXUse;
    }

    public void setInventoryXUse(Collection<InventoryXUse> inventoryXUse) {
        this.inventoryXUse = inventoryXUse;
    }

    public void setClone() {
        try {
            original = (IOrderItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().IORDER_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("iorder_id", iorderId, original.iorderId)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId, Constants.table().INVENTORY_ITEM)
                 .setField("quantity", quantity, original.quantity)
                 .setField("catalog_number", catalogNumber, original.catalogNumber)
                 .setField("unit_cost", unitCost, original.unitCost);

        return audit;
    }
}
