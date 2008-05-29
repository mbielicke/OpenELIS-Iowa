
package org.openelis.entity;

/**
  * InventoryComponent Entity POJO for database 
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

	@NamedQueries({	@NamedQuery(name = "InventoryComponent.InventoryComponent", query = "select new org.openelis.domain.InventoryComponentDO(c.id,c.inventoryItem,c.component,i.name,i.description, " +
			 											" c.quantity) from InventoryComponent c left join c.componentInventoryItem i  where c.id = :id"),
                    @NamedQuery(name = "InventoryComponent.InventoryComponentsByItem", query = "select new org.openelis.domain.InventoryComponentDO(c.id,c.inventoryItem,c.component,i.name,i.description, " +
                                                        " c.quantity) from InventoryComponent c left join c.componentInventoryItem i  where c.inventoryItem = :id"),
                    @NamedQuery(name = "InventoryComponent.ValidateComponentWithItemStore", query = "select c.id from InventoryComponent c left join c.componentInventoryItem i where " +
                                                        " i.store = :store AND c.id = :id")})

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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "component", insertable = false, updatable = false)
  private InventoryItem componentInventoryItem;
  
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
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((inventoryItem == null && original.inventoryItem != null) || 
         (inventoryItem != null && !inventoryItem.equals(original.inventoryItem))){
        Element elem = doc.createElement("inventory_item");
        elem.appendChild(doc.createTextNode(original.inventoryItem.toString().trim()));
        root.appendChild(elem);
      }      

      if((component == null && original.component != null) || 
         (component != null && !component.equals(original.component))){
        Element elem = doc.createElement("component");
        elem.appendChild(doc.createTextNode(original.component.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantity == null && original.quantity != null) || 
         (quantity != null && !quantity.equals(original.quantity))){
        Element elem = doc.createElement("quantity");
        elem.appendChild(doc.createTextNode(original.quantity.toString().trim()));
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
public InventoryItem getComponentInventoryItem() {
	return componentInventoryItem;
}
public void setComponentInventoryItem(InventoryItem componentInventoryItem) {
	this.componentInventoryItem = componentInventoryItem;
}
  
}   
