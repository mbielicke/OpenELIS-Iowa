
package org.openelis.entity;

/**
  * OrderItem Entity POJO for database 
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
import org.openelis.interfaces.Auditable;

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

  @Column(name="inventory_location")
  private Integer inventoryLocation;             

  @Column(name="quantity_requested")
  private Integer quantityRequested;             

  @Column(name="quantity_received")
  private Integer quantityReceived;             


  @Transient
  private OrderItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getOrder() {
    return order;
  }
  public void setOrder(Integer order) {
    this.order = order;
  }

  public Integer getInventoryLocation() {
    return inventoryLocation;
  }
  public void setInventoryLocation(Integer inventoryLocation) {
    this.inventoryLocation = inventoryLocation;
  }

  public Integer getQuantityRequested() {
    return quantityRequested;
  }
  public void setQuantityRequested(Integer quantityRequested) {
    this.quantityRequested = quantityRequested;
  }

  public Integer getQuantityReceived() {
    return quantityReceived;
  }
  public void setQuantityReceived(Integer quantityReceived) {
    this.quantityReceived = quantityReceived;
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
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((order == null && original.order != null) || 
         (order != null && !order.equals(original.order))){
        Element elem = doc.createElement("order");
        elem.appendChild(doc.createTextNode(original.order.toString()));
        root.appendChild(elem);
      }      

      if((inventoryLocation == null && original.inventoryLocation != null) || 
         (inventoryLocation != null && !inventoryLocation.equals(original.inventoryLocation))){
        Element elem = doc.createElement("inventory_location");
        elem.appendChild(doc.createTextNode(original.inventoryLocation.toString()));
        root.appendChild(elem);
      }      

      if((quantityRequested == null && original.quantityRequested != null) || 
         (quantityRequested != null && !quantityRequested.equals(original.quantityRequested))){
        Element elem = doc.createElement("quantity_requested");
        elem.appendChild(doc.createTextNode(original.quantityRequested.toString()));
        root.appendChild(elem);
      }      

      if((quantityReceived == null && original.quantityReceived != null) || 
         (quantityReceived != null && !quantityReceived.equals(original.quantityReceived))){
        Element elem = doc.createElement("quantity_received");
        elem.appendChild(doc.createTextNode(original.quantityReceived.toString()));
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
  
}   
