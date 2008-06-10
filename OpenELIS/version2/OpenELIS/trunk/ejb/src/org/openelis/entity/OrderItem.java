
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
    @NamedQuery(name = "OrderItem.OrderItemsWithLocByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(o.id, o.order, o.inventoryItemId, ii.name,o.quantityRequested, " +
                            " d.entry, it.fromLocationId, childLoc.name, childLoc.location, parentLoc.name, childLoc.storageUnit.description, it.id) from InventoryTransaction it left join it.toOrder o " +
                            " left join o.inventoryItem ii left join it.fromLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where o.inventoryItem.store = d.id and o.order = :id"),
    @NamedQuery(name = "OrderItem.OrderItemsByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(o.id, o.order, o.inventoryItemId, ii.name,o.quantityRequested, " +
                            " d.entry) from OrderItem o left join o.inventoryItem ii , Dictionary d where o.inventoryItem.store = d.id and o.order = :id")})
                            
@Entity
@Table(name="order_item")
@EntityListeners({AuditUtil.class})
public class OrderItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="order")
  private Integer order;             

  @Column(name="inventory_item")
  private Integer inventoryItemId;             

  @Column(name="quantity_requested")
  private Integer quantityRequested;     
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item", insertable = false, updatable = false)
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

  public Integer getOrder() {
    return order;
  }
  public void setOrder(Integer order) {
    if((order == null && this.order != null) || 
       (order != null && !order.equals(this.order)))
      this.order = order;
  }

  public Integer getInventoryItemId() {
    return inventoryItemId;
  }
  public void setInventoryItemId(Integer inventoryLocation) {
    if((inventoryLocation == null && this.inventoryItemId != null) || 
       (inventoryLocation != null && !inventoryLocation.equals(this.inventoryItemId)))
      this.inventoryItemId = inventoryLocation;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((order == null && original.order != null) || 
         (order != null && !order.equals(original.order))){
        Element elem = doc.createElement("order");
        elem.appendChild(doc.createTextNode(original.order.toString().trim()));
        root.appendChild(elem);
      }      

      if((inventoryItemId == null && original.inventoryItemId != null) || 
         (inventoryItemId != null && !inventoryItemId.equals(original.inventoryItemId))){
        Element elem = doc.createElement("inventory_location");
        elem.appendChild(doc.createTextNode(original.inventoryItemId.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantityRequested == null && original.quantityRequested != null) || 
         (quantityRequested != null && !quantityRequested.equals(original.quantityRequested))){
        Element elem = doc.createElement("quantity_requested");
        elem.appendChild(doc.createTextNode(original.quantityRequested.toString().trim()));
        root.appendChild(elem);
      }      
     
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
