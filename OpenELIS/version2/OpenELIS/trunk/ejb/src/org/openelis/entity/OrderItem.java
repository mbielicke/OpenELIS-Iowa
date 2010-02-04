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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "OrderItem.FetchByOrderId",
                query = "select distinct new org.openelis.domain.OrderItemViewDO(o.id,o.orderId,o.inventoryItemId," +
                		"o.quantity,o.catalogNumber,o.unitCost,i.name,i.storeId)"
                      + " from OrderItem o left join o.inventoryItem i where o.orderId = :id"),
    
    /*@NamedQuery(name = "OrderItem.OrderItemsWithLocByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(oi.id, oi.orderId, oi.inventoryItemId, ii.name,oi.quantity, " +
                            " d.entry, t.inventoryLocationId, childLoc.name, childLoc.location, parentLoc.name, childLoc.storageUnit.description, t.id, il.quantityOnhand, il.lotNumber) from " +
                            " InventoryXUse t left join t.orderItem oi left join oi.inventoryItem ii left join t.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where oi.inventoryItem.storeId = d.id and oi.orderId = :id"),
    @NamedQuery(name = "OrderItem.OrderItemName", query = "select ii.name from OrderItem o left join o.inventoryItem ii where o.id = :id")*/})
                            
                            
@Entity
@Table(name = "order_item")
@EntityListeners( {AuditUtil.class})
public class OrderItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                      id;

    @Column(name = "order_id")
    private Integer                      orderId;

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
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order                        order;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    private Collection<InventoryXUse>    inventoryXUse;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "inventory_receipt_order_item", joinColumns = {@JoinColumn(name = "order_item_id")}, inverseJoinColumns = {@JoinColumn(name = "inventory_receipt_id")})
    private Collection<InventoryReceipt> inventoryReceipts;

    @Transient
    private OrderItem                    original;

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

    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public Collection<InventoryReceipt> getInventoryReceipts() {
        return inventoryReceipts;
    }

    public void setInventoryReceipts(Collection<InventoryReceipt> inventoryReceipts) {
        this.inventoryReceipts = inventoryReceipts;
    }

    public Collection<InventoryXUse> getInventoryXUse() {
        return inventoryXUse;
    }

    public void setInventoryXUse(Collection<InventoryXUse> inventoryXUse) {
        this.inventoryXUse = inventoryXUse;
    }

    public void setClone() {
        try {
            original = (OrderItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ORDER_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("order_id", orderId, original.orderId)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId)
                 .setField("quantity", quantity, original.quantity)
                 .setField("catalog_number", catalogNumber, original.catalogNumber)
                 .setField("unit_cost", unitCost, original.unitCost);

        return audit;
    }
}
