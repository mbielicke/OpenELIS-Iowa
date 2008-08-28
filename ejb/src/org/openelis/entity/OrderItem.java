/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

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
    @NamedQuery(name = "OrderItem.OrderItemsWithLocByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(oi.id, oi.orderId, oi.inventoryItemId, ii.name,oi.quantityRequested, " +
                            " d.entry, t.inventoryLocationId, childLoc.name, childLoc.location, parentLoc.name, childLoc.storageUnit.description, t.id, il.quantityOnhand) from TransLocationOrder t left join t.orderItem oi " +
                            " left join oi.inventoryItem ii left join t.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where oi.inventoryItem.storeId = d.id and oi.orderId = :id"),
    @NamedQuery(name = "OrderItem.OrderItemsByOrderId", query = "select distinct new org.openelis.domain.OrderItemDO(o.id, o.orderId, o.inventoryItemId, ii.name,o.quantityRequested, " +
                            " d.entry, o.catalogNumber, o.unitCost) from OrderItem o left join o.inventoryItem ii , Dictionary d where o.inventoryItem.storeId = d.id and o.orderId = :id")})
                            
                            
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
  
  @Column(name="catalog_number")
  private String catalogNumber;   
  
  @Column(name="unit_cost")
  private Double unitCost;   

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
  private InventoryItem inventoryItem;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Order order;
  
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
  
  public Double getUnitCost() {
      return unitCost;
    }
  
  public void setUnitCost(Double unitCost) {
      if((unitCost == null && this.unitCost != null) || 
         (unitCost != null && !unitCost.equals(this.unitCost)))
        this.unitCost = unitCost;
    }
    
  public String getCatalogNumber() {
    return catalogNumber;
  }
  
  public void setCatalogNumber(String catalogNumber) {
      if((catalogNumber == null && this.catalogNumber != null) || 
         (catalogNumber != null && !catalogNumber.equals(this.catalogNumber)))
          this.catalogNumber = catalogNumber;
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
      
      AuditUtil.getChangeXML(catalogNumber,original.catalogNumber,doc,"catalog_number");
      
      AuditUtil.getChangeXML(unitCost,original.unitCost,doc,"unit_cost");

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
public Order getOrder() {
    return order;
}
  
}   
