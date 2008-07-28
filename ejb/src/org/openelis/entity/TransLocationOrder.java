
package org.openelis.entity;

/**
  * TransLocationOrder Entity POJO for database 
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
@Table(name="trans_location_order")
@EntityListeners({AuditUtil.class})
public class TransLocationOrder implements Auditable, Cloneable {
  
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
  private TransLocationOrder original;

  
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

  
  public void setClone() {
    try {
      original = (TransLocationOrder)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryLocationId,original.inventoryLocationId,doc,"inventory_location_id");

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
    return "trans_location_order";
  }
public InventoryLocation getInventoryLocation() {
    return inventoryLocation;
}
  
}   
