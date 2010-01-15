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
  * TransLocationOrder Entity POJO for database 
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
@Table(name="inventory_x_use")
@EntityListeners({AuditUtil.class})
public class InventoryXUse implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_location_id")
  private Integer inventoryLocationId;             

  @Column(name="order_item_id")
  private Integer orderItemId;             

  @Column(name="quantity")
  private Integer quantity; 
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
  private OrderItem orderItem;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
  private InventoryLocation inventoryLocation;


  @Transient
  private InventoryXUse original;

  
  public OrderItem getOrderItem() {
    return orderItem;
}
public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInventoryLocationId() {
    return inventoryLocationId;
  }
  public void setInventoryLocationId(Integer inventoryLocationId) {
    if((inventoryLocationId == null && this.inventoryLocationId != null) || 
       (inventoryLocationId != null && !inventoryLocationId.equals(this.inventoryLocationId)))
      this.inventoryLocationId = inventoryLocationId;
  }

  public Integer getOrderItemId() {
    return orderItemId;
  }
  public void setOrderItemId(Integer orderItemId) {
    if((orderItemId == null && this.orderItemId != null) || 
       (orderItemId != null && !orderItemId.equals(this.orderItemId)))
      this.orderItemId = orderItemId;
  }

  public Integer getQuantity() {
    return quantity;
  }
  public void setQuantity(Integer quantity) {
    if((quantity == null && this.quantity != null) || 
       (quantity != null && !quantity.equals(this.quantity)))
      this.quantity = quantity;
  }

  public InventoryLocation getInventoryLocation() {
      return inventoryLocation;
  }
  
  public void setClone() {
    try {
        original = (InventoryXUse)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_X_USE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_location_id", inventoryLocationId, original.inventoryLocationId)
                 .setField("order_item_id", orderItemId, original.orderItemId)
                 .setField("quantity", quantity, original.quantity);

        return audit;
    }
  
}   
