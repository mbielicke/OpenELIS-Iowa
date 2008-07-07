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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.entity;

/**
  * InventoryReceipt Entity POJO for database 
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
    return new Datetime(Datetime.YEAR,Datetime.SECOND,receivedDate);
  }
  public void setReceivedDate (Datetime received_date){
    if((receivedDate == null && this.receivedDate != null) || 
       (receivedDate != null && !receivedDate.equals(this.receivedDate)))
      this.receivedDate = received_date.getDate();
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
  
}   
