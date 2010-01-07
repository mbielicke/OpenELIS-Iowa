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
  * InventoryTransaction Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="inventory_transaction")
@EntityListeners({AuditUtil.class})
public class InventoryTransaction implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="from_location_id")
  private Integer fromLocationId;             

  @Column(name="from_receipt_id")
  private Integer fromReceiptId;             

  @Column(name="from_adjustment_id")
  private Integer fromAdjustmentId;             

  @Column(name="to_order_id")
  private Integer toOrderId;             

  @Column(name="to_location_id")
  private Integer toLocationId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="quantity")
  private Double quantity;             
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_location_id", insertable = false, updatable = false)
  private InventoryLocation fromLocation;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_receipt_id", insertable = false, updatable = false)
  private InventoryReceipt fromReceipt;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_order_id", insertable = false, updatable = false)
  private OrderItem toOrder;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_location_id", insertable = false, updatable = false)
  private InventoryLocation toLocation;

  @Transient
  private InventoryTransaction original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getFromLocationId() {
    return fromLocationId;
  }
  public void setFromLocationId(Integer fromLocationId) {
    if((fromLocationId == null && this.fromLocationId != null) || 
       (fromLocationId != null && !fromLocationId.equals(this.fromLocationId)))
      this.fromLocationId = fromLocationId;
  }

  public Integer getFromReceiptId() {
    return fromReceiptId;
  }
  public void setFromReceiptId(Integer fromReceiptId) {
    if((fromReceiptId == null && this.fromReceiptId != null) || 
       (fromReceiptId != null && !fromReceiptId.equals(this.fromReceiptId)))
      this.fromReceiptId = fromReceiptId;
  }

  public Integer getFromAdjustmentId() {
    return fromAdjustmentId;
  }
  public void setFromAdjustmentId(Integer fromAdjustmentId) {
    if((fromAdjustmentId == null && this.fromAdjustmentId != null) || 
       (fromAdjustmentId != null && !fromAdjustmentId.equals(this.fromAdjustmentId)))
      this.fromAdjustmentId = fromAdjustmentId;
  }

  public Integer getToOrderId() {
    return toOrderId;
  }
  public void setToOrderId(Integer toOrderId) {
    if((toOrderId == null && this.toOrderId != null) || 
       (toOrderId != null && !toOrderId.equals(this.toOrderId)))
      this.toOrderId = toOrderId;
  }

  public Integer getToLocationId() {
    return toLocationId;
  }
  public void setToLocationId(Integer toLocationId) {
    if((toLocationId == null && this.toLocationId != null) || 
       (toLocationId != null && !toLocationId.equals(this.toLocationId)))
      this.toLocationId = toLocationId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public Double getQuantity() {
    return quantity;
  }
  public void setQuantity(Double quantity) {
    if((quantity == null && this.quantity != null) || 
       (quantity != null && !quantity.equals(this.quantity)))
      this.quantity = quantity;
  }
  
  public InventoryLocation getFromLocation() {
      return fromLocation;
  }

  public void setFromLocation(InventoryLocation fromLocation) {
      this.fromLocation = fromLocation;
  }
    
  public OrderItem getToOrder() {
      return toOrder;
  }
    
  public void setToOrder(OrderItem toOrder) {
      this.toOrder = toOrder;
  }
    
  public InventoryReceipt getFromReceipt() {
      return fromReceipt;
  }
    
  public void setFromReceipt(InventoryReceipt fromReceipt) {
      this.fromReceipt = fromReceipt;
  }
    
  public InventoryLocation getToLocation() {
      return toLocation;
  }

  
  public void setClone() {
    try {
        original = (InventoryTransaction)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_TRANSACTION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("from_location_id", fromLocationId, original.fromLocationId)
                 .setField("from_receipt_id", fromReceiptId, original.fromReceiptId)
                 .setField("from_adjustment_id", fromAdjustmentId, original.fromAdjustmentId)
                 .setField("to_order_id", toOrderId, original.toOrderId)
                 .setField("to_location_id", toLocationId, original.toLocationId)
                 .setField("type_id", typeId, original.typeId)
                 .setField("quantity", quantity, original.quantity);

        return audit;

    }
  
}   
