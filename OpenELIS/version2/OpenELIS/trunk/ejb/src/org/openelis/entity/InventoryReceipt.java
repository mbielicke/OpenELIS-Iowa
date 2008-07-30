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
  * InventoryReceipt Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "InventoryReceipt.OrderItemListWithTransByOrderNum", query = "select distinct oi.id from InventoryTransaction tr LEFT JOIN tr.toOrder oi where " + 
                               " oi.order.id = :id and tr.fromReceiptId is not null "),
    @NamedQuery(name = "InventoryReceipt.OrderItemListByOrderNum", query = "select distinct oi.id from Order o LEFT JOIN o.orderItem oi where " + 
                               " (NOT EXISTS (select tr.id from TransReceiptOrder tr where tr.orderItemId = oi.id and tr.inventoryReceiptId is not null) OR " +
                               " oi.quantityRequested > (select sum(tr2.quantity) from TransReceiptOrder tr2 where tr2.orderItemId = oi.id and tr2.inventoryReceiptId is not null))" + 
                               " and o.id = :id and o.isExternal='Y'"),
    @NamedQuery(name = "InventoryReceipt.InventoryReceiptNotRecByOrderId", query = "select distinct new org.openelis.domain.InventoryReceiptDO(o.id, oi.inventoryItemId, oi.inventoryItem.name, " +
                               " oi.id, o.organizationId,orgz.name,oi.quantityRequested,orgz.address.streetAddress,orgz.address.multipleUnit,orgz.address.city,orgz.address.state, " +
                               " orgz.address.zipCode, ii.description, dictStore.entry, dicDisUnits.entry, ii.isBulk, ii.isLotMaintained, ii.isSerialMaintained) from Order o " +
                               " LEFT JOIN o.orderItem oi LEFT JOIN oi.inventoryItem ii LEFT JOIN o.organization orgz, " +
                               " Dictionary dictStore, Dictionary dicDisUnits where " + 
                               " ii.storeId = dictStore.id and ii.dispensedUnitsId = dicDisUnits.id " +                                
                               " and oi.id = :id order by o.id "),
    @NamedQuery(name = "InventoryReceipt.OrderItemsNotFilled", query = "SELECT oi.id FROM OrderItem oi, Order o, Dictionary d WHERE oi.orderId = o.id AND " + 
                            " d.id = o.statusId and d.systemName <> 'order_status_cancelled' and d.systemName <> 'order_status_completed' and " + 
                            " oi.quantityRequested > (SELECT sum(tr.quantity) FROM TransReceiptOrder tr where tr.orderItemId = oi.id) " + 
                            " and o.id = :id"),
    @NamedQuery(name = "InventoryReceipt.OrdersNotCompletedCanceled", query = "SELECT o.id FROM Order o, Dictionary d WHERE " + 
                            " d.id = o.statusId and d.systemName <> 'order_status_cancelled' and d.systemName <> 'order_status_completed' " +
                            " and o.id = :id"),
    @NamedQuery(name = "InventoryReceipt.InventoryItemByUPC", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " +
                            " i.isBulk, i.isLotMaintained, i.isSerialMaintained) " +
                            " from InventoryReceipt ir left join ir.inventoryItem i, Dictionary store, Dictionary disUnit " +
                            " where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and ir.upc like :upc and i.isActive = 'Y' " +
                            " order by i.name"),
    @NamedQuery(name = "InventoryReceipt.LocationIdsByReceiptId", query = "select distinct il.id  from TransReceiptLocation tr LEFT JOIN tr.inventoryLocation il where tr.inventoryReceiptId = :id ")})
                    
@Entity
@Table(name="inventory_receipt")
@EntityListeners({AuditUtil.class})
public class InventoryReceipt implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item_id")
  private Integer inventoryItemId;             

  @Column(name="organization_id")
  private Integer organizationId;             

  @Column(name="received_date")
  private Date receivedDate;             

  @Column(name="quantity_received")
  private Integer quantityReceived;             

  @Column(name="unit_cost")
  private Double unitCost;             

  @Column(name="qc_reference")
  private String qcReference;             

  @Column(name="external_reference")
  private String externalReference;             

  @Column(name="upc")
  private String upc;  
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
  private InventoryItem inventoryItem;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", insertable = false, updatable = false)
  private Organization organization;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
  private Collection<TransReceiptOrder> transReceiptOrders;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
  private Collection<TransReceiptLocation> transReceiptLocations;

  @Transient
  private InventoryReceipt original;

  
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

  public Integer getOrganizationId() {
    return organizationId;
  }
  public void setOrganizationId(Integer organizationId) {
    if((organizationId == null && this.organizationId != null) || 
       (organizationId != null && !organizationId.equals(this.organizationId)))
      this.organizationId = organizationId;
  }

  public Datetime getReceivedDate() {
    if(receivedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.DAY,receivedDate);
  }
  public void setReceivedDate (Datetime receivedDate){
    if((receivedDate == null && this.receivedDate != null) || (receivedDate != null && this.receivedDate == null) || 
       (receivedDate != null && !receivedDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.receivedDate))))
      this.receivedDate = receivedDate.getDate();
  }

  public Integer getQuantityReceived() {
    return quantityReceived;
  }
  public void setQuantityReceived(Integer quantityReceived) {
    if((quantityReceived == null && this.quantityReceived != null) || 
       (quantityReceived != null && !quantityReceived.equals(this.quantityReceived)))
      this.quantityReceived = quantityReceived;
  }

  public Double getUnitCost() {
    return unitCost;
  }
  public void setUnitCost(Double unitCost) {
    if((unitCost == null && this.unitCost != null) || 
       (unitCost != null && !unitCost.equals(this.unitCost)))
      this.unitCost = unitCost;
  }

  public String getQcReference() {
    return qcReference;
  }
  public void setQcReference(String qcReference) {
    if((qcReference == null && this.qcReference != null) || 
       (qcReference != null && !qcReference.equals(this.qcReference)))
      this.qcReference = qcReference;
  }

  public String getExternalReference() {
    return externalReference;
  }
  public void setExternalReference(String externalReference) {
    if((externalReference == null && this.externalReference != null) || 
       (externalReference != null && !externalReference.equals(this.externalReference)))
      this.externalReference = externalReference;
  }

  public String getUpc() {
    return upc;
  }
  public void setUpc(String upc) {
    if((upc == null && this.upc != null) || 
       (upc != null && !upc.equals(this.upc)))
      this.upc = upc;
  }

  
  public void setClone() {
    try {
      original = (InventoryReceipt)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryItemId,original.inventoryItemId,doc,"inventory_item_id");

      AuditUtil.getChangeXML(organizationId,original.organizationId,doc,"organization_id");

      AuditUtil.getChangeXML(receivedDate,original.receivedDate,doc,"received_date");

      AuditUtil.getChangeXML(quantityReceived,original.quantityReceived,doc,"quantity_received");

      AuditUtil.getChangeXML(unitCost,original.unitCost,doc,"unit_cost");

      AuditUtil.getChangeXML(qcReference,original.qcReference,doc,"qc_reference");

      AuditUtil.getChangeXML(externalReference,original.externalReference,doc,"external_reference");

      AuditUtil.getChangeXML(upc,original.upc,doc,"upc");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_receipt";
  }
public InventoryItem getInventoryItem() {
    return inventoryItem;
}
public void setInventoryItem(InventoryItem inventoryItem) {
    this.inventoryItem = inventoryItem;
}
public Organization getOrganization() {
    return organization;
}
public Collection<TransReceiptOrder> getFromReceiptTransactions() {
    return transReceiptOrders;
}
public Collection<TransReceiptLocation> getTransReceiptLocations() {
    return transReceiptLocations;
}
  
}   
