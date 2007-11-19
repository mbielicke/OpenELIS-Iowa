
package org.openelis.entity;

/**
  * InventoryComponent Entity POJO for database 
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
@Table(name="inventory_component")
@EntityListeners({AuditUtil.class})
public class InventoryComponent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item")
  private Integer inventoryItem;             

  @Column(name="component")
  private Integer component;             

  @Column(name="quantity")
  private Double quantity;             


  @Transient
  private InventoryComponent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInventoryItem() {
    return inventoryItem;
  }
  public void setInventoryItem(Integer inventoryItem) {
    if((inventoryItem == null && this.inventoryItem != null) || 
       (inventoryItem != null && !inventoryItem.equals(this.inventoryItem)))
      this.inventoryItem = inventoryItem;
  }

  public Integer getComponent() {
    return component;
  }
  public void setComponent(Integer component) {
    if((component == null && this.component != null) || 
       (component != null && !component.equals(this.component)))
      this.component = component;
  }

  public Double getQuantity() {
    return quantity;
  }
  public void setQuantity(Double quantity) {
    if((quantity == null && this.quantity != null) || 
       (quantity != null && !quantity.equals(this.quantity)))
      this.quantity = quantity;
  }

  
  public void setClone() {
    try {
      original = (InventoryComponent)this.clone();
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

      if((inventoryItem == null && original.inventoryItem != null) || 
         (inventoryItem != null && !inventoryItem.equals(original.inventoryItem))){
        Element elem = doc.createElement("inventory_item");
        elem.appendChild(doc.createTextNode(original.inventoryItem.toString()));
        root.appendChild(elem);
      }      

      if((component == null && original.component != null) || 
         (component != null && !component.equals(original.component))){
        Element elem = doc.createElement("component");
        elem.appendChild(doc.createTextNode(original.component.toString()));
        root.appendChild(elem);
      }      

      if((quantity == null && original.quantity != null) || 
         (quantity != null && !quantity.equals(original.quantity))){
        Element elem = doc.createElement("quantity");
        elem.appendChild(doc.createTextNode(original.quantity.toString()));
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
    return "inventory_component";
  }
  
}   
