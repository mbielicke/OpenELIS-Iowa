
package org.openelis.entity;

/**
  * InventoryReceiptOrderItem Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="inventory_receipt_order_item")
@EntityListeners({AuditUtil.class})
public class InventoryReceiptOrderItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_receipt_id")
  private Integer inventoryReceiptId;             

  @Column(name="order_item_id")
  private Integer orderItemId;             


  @Transient
  private InventoryReceiptOrderItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInventoryReceiptId() {
    return inventoryReceiptId;
  }
  public void setInventoryReceiptId(Integer inventoryReceiptId) {
    if((inventoryReceiptId == null && this.inventoryReceiptId != null) || 
       (inventoryReceiptId != null && !inventoryReceiptId.equals(this.inventoryReceiptId)))
      this.inventoryReceiptId = inventoryReceiptId;
  }

  public Integer getOrderItemId() {
    return orderItemId;
  }
  public void setOrderItemId(Integer orderItemId) {
    if((orderItemId == null && this.orderItemId != null) || 
       (orderItemId != null && !orderItemId.equals(this.orderItemId)))
      this.orderItemId = orderItemId;
  }

  
  public void setClone() {
    try {
        original = (InventoryReceiptOrderItem)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_RECEIPT_ORDER_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_receipt_id", inventoryReceiptId, original.inventoryReceiptId)
                 .setField("order_item_id", orderItemId, original.orderItemId);

        return audit;
    }
  
}   
