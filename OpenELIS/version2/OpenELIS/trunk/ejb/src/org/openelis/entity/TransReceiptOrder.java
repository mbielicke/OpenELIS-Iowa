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
  * TransReceiptOrder Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
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
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="trans_receipt_order")
@EntityListeners({AuditUtil.class})
public class TransReceiptOrder implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_receipt_id")
  private Integer inventoryReceiptId;             

  @Column(name="order_item_id")
  private Integer orderItemId;             

  @Column(name="quantity")
  private Integer quantity;  
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
  private InventoryReceipt inventoryReceipt;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
  private OrderItem orderItem;


  @Transient
  private TransReceiptOrder original;

  
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

  public Integer getQuantity() {
    return quantity;
  }
  public void setQuantity(Integer quantity) {
    if((quantity == null && this.quantity != null) || 
       (quantity != null && !quantity.equals(this.quantity)))
      this.quantity = quantity;
  }

  
  public void setClone() {
    try {
      original = (TransReceiptOrder)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryReceiptId,original.inventoryReceiptId,doc,"inventory_receipt_id");

      AuditUtil.getChangeXML(orderItemId,original.orderItemId,doc,"order_item_id");

      AuditUtil.getChangeXML(quantity,original.quantity,doc,"quantity");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "trans_receipt_order";
  }
public InventoryReceipt getInventoryReceipt() {
    return inventoryReceipt;
}
public OrderItem getOrderItem() {
    return orderItem;
}
  
}   
