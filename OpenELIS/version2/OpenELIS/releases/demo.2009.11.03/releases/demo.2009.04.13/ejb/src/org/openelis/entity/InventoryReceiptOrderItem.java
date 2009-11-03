
package org.openelis.entity;

/**
  * InventoryReceiptOrderItem Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
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
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryReceiptId,original.inventoryReceiptId,doc,"inventory_receipt_id");

      AuditUtil.getChangeXML(orderItemId,original.orderItemId,doc,"order_item_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_receipt_order_item";
  }
  
}   
