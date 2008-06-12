
package org.openelis.entity;

/**
  * InventoryComponent Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.InventoryItem;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({ @NamedQuery(name = "InventoryComponent.InventoryComponent", query = "select new org.openelis.domain.InventoryComponentDO(c.id,c.inventoryItemId,c.componentId,i.name,i.description, " +
    " c.quantity) from InventoryComponent c left join c.componentInventoryItem i  where c.id = :id"),
@NamedQuery(name = "InventoryComponent.InventoryComponentsByItem", query = "select new org.openelis.domain.InventoryComponentDO(c.id,c.inventoryItemId,c.componentId,i.name,i.description, " +
" c.quantity) from InventoryComponent c left join c.componentInventoryItem i  where c.inventoryItemId = :id"),
@NamedQuery(name = "InventoryComponent.ValidateComponentWithItemStore", query = "select c.id from InventoryComponent c left join c.componentInventoryItem i where " +
" i.storeId = :store AND c.id = :id")})


@Entity
@Table(name="inventory_component")
@EntityListeners({AuditUtil.class})
public class InventoryComponent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item_id")
  private Integer inventoryItemId;             

  @Column(name="component_id")
  private Integer componentId;             

  @Column(name="quantity")
  private Double quantity; 
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "component_id", insertable = false, updatable = false)
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

  public Integer getInventoryItemId() {
    return inventoryItemId;
  }
  public void setInventoryItemId(Integer inventoryItemId) {
    if((inventoryItemId == null && this.inventoryItemId != null) || 
       (inventoryItemId != null && !inventoryItemId.equals(this.inventoryItemId)))
      this.inventoryItemId = inventoryItemId;
  }

  public Integer getComponentId() {
    return componentId;
  }
  public void setComponentId(Integer componentId) {
    if((componentId == null && this.componentId != null) || 
       (componentId != null && !componentId.equals(this.componentId)))
      this.componentId = componentId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryItemId,original.inventoryItemId,doc,"inventory_item_id");

      AuditUtil.getChangeXML(componentId,original.componentId,doc,"component_id");

      AuditUtil.getChangeXML(quantity,original.quantity,doc,"quantity");

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
