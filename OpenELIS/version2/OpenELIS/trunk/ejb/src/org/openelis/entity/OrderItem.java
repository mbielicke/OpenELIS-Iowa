
package org.openelis.entity;

/**
  * OrderItem Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries( {
    @NamedQuery(name = "OrderItem.OrderItemsWithLocByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(o.id, o.orderId, o.inventoryItemId, ii.name,o.quantityRequested, " +
                            " d.entry, it.fromLocationId, childLoc.name, childLoc.location, parentLoc.name, childLoc.storageUnit.description, it.id) from InventoryTransaction it left join it.toOrder o " +
                            " left join o.inventoryItem ii left join it.fromLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where o.inventoryItem.storeId = d.id and o.orderId = :id"),
    @NamedQuery(name = "OrderItem.OrderItemsByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(o.id, o.orderId, o.inventoryItemId, ii.name,o.quantityRequested, " +
                            " d.entry) from OrderItem o left join o.inventoryItem ii , Dictionary d where o.inventoryItem.storeId = d.id and o.orderId = :id")})
                            
                            
@Entity
@Table(name="order_item")
@EntityListeners({AuditUtil.class})
public class OrderItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="order_id")
  private Integer orderId;             

  @Column(name="inventory_item_id")
  private Integer inventoryItemId;             

  @Column(name="quantity_requested")
  private Integer quantityRequested;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
  private InventoryItem inventoryItem;
  
  @Transient
  private OrderItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getOrderId() {
    return orderId;
  }
  public void setOrderId(Integer orderId) {
    if((orderId == null && this.orderId != null) || 
       (orderId != null && !orderId.equals(this.orderId)))
      this.orderId = orderId;
  }

  public Integer getInventoryItemId() {
    return inventoryItemId;
  }
  public void setInventoryItemId(Integer inventoryItemId) {
    if((inventoryItemId == null && this.inventoryItemId != null) || 
       (inventoryItemId != null && !inventoryItemId.equals(this.inventoryItemId)))
      this.inventoryItemId = inventoryItemId;
  }

  public Integer getQuantityRequested() {
    return quantityRequested;
  }
  public void setQuantityRequested(Integer quantityRequested) {
    if((quantityRequested == null && this.quantityRequested != null) || 
       (quantityRequested != null && !quantityRequested.equals(this.quantityRequested)))
      this.quantityRequested = quantityRequested;
  }

  
  public void setClone() {
    try {
      original = (OrderItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(orderId,original.orderId,doc,"order_id");

      AuditUtil.getChangeXML(inventoryItemId,original.inventoryItemId,doc,"inventory_item_id");

      AuditUtil.getChangeXML(quantityRequested,original.quantityRequested,doc,"quantity_requested");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "order_item";
  }
  
  public InventoryItem getInventoryItem() {
      return inventoryItem;
  }
  public void setInventoryItem(InventoryItem inventoryItem) {
      this.inventoryItem = inventoryItem;
  }
  
}   
