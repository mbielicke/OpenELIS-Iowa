
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/*@NamedQueries( {
    @NamedQuery(name = "InventoryReceipt.InventoryReceiptsBy??", query = "select new org.openelis.domain.InventoryReceiptDO() " +
                                       "  from InventoryReceipt r where orgz.id = :id")})
  */            
@Entity
@Table(name="inventory_receipt")
@EntityListeners({AuditUtil.class})
public class InventoryReceipt implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item")
  private Integer inventoryItem;             

  @Column(name="organization")
  private Integer organization;             

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

  public Integer getInventoryItem() {
    return inventoryItem;
  }
  public void setInventoryItem(Integer inventoryItem) {
    if((inventoryItem == null && this.inventoryItem != null) || 
       (inventoryItem != null && !inventoryItem.equals(this.inventoryItem)))
      this.inventoryItem = inventoryItem;
  }

  public Integer getOrganization() {
    return organization;
  }
  public void setOrganization(Integer organization) {
    if((organization == null && this.organization != null) || 
       (organization != null && !organization.equals(this.organization)))
      this.organization = organization;
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

  
  public void setClone() {
    try {
      original = (InventoryReceipt)this.clone();
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

      if((organization == null && original.organization != null) || 
         (organization != null && !organization.equals(original.organization))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organization.toString().trim()));
        root.appendChild(elem);
      }      

      if((receivedDate == null && original.receivedDate != null) || 
         (receivedDate != null && !receivedDate.equals(original.receivedDate))){
        Element elem = doc.createElement("received_date");
        elem.appendChild(doc.createTextNode(original.receivedDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantityReceived == null && original.quantityReceived != null) || 
         (quantityReceived != null && !quantityReceived.equals(original.quantityReceived))){
        Element elem = doc.createElement("quantity_received");
        elem.appendChild(doc.createTextNode(original.quantityReceived.toString().trim()));
        root.appendChild(elem);
      }      

      if((unitCost == null && original.unitCost != null) || 
         (unitCost != null && !unitCost.equals(original.unitCost))){
        Element elem = doc.createElement("unit_cost");
        elem.appendChild(doc.createTextNode(original.unitCost.toString().trim()));
        root.appendChild(elem);
      }      

      if((qcReference == null && original.qcReference != null) || 
         (qcReference != null && !qcReference.equals(original.qcReference))){
        Element elem = doc.createElement("qc_reference");
        elem.appendChild(doc.createTextNode(original.qcReference.toString().trim()));
        root.appendChild(elem);
      }      

      if((externalReference == null && original.externalReference != null) || 
         (externalReference != null && !externalReference.equals(original.externalReference))){
        Element elem = doc.createElement("external_reference");
        elem.appendChild(doc.createTextNode(original.externalReference.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((upc == null && original.upc != null) || 
         (upc != null && !upc.equals(original.upc))){
        Element elem = doc.createElement("upc");
        elem.appendChild(doc.createTextNode(original.upc.toString().trim()));
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
    return "inventory_receipt";
  }
public String getUpc() {
    return upc;
}
public void setUpc(String upc) {
    if((upc == null && this.upc != null) || 
       (upc != null && !upc.equals(this.upc)))
      this.upc = upc;
}
  
}   
