
package org.openelis.entity;

/**
  * TransReceiptLocation Entity POJO for database 
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
@Table(name="trans_receipt_location")
@EntityListeners({AuditUtil.class})
public class TransReceiptLocation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_receipt_id")
  private Integer inventoryReceiptId;             

  @Column(name="inventory_location_id")
  private Integer inventoryLocationId;             

  @Column(name="quantity")
  private Integer quantity;             


  @Transient
  private TransReceiptLocation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInventoryReceiptId() {
    return inventoryReceiptId;
  }
  public void setInventoryReceiptId(Integer inventoryReceiptId) {
    if((inventoryReceiptId == null && this.inventoryReceiptId != null) || 
       (inventoryReceiptId != null && !inventoryReceiptId.equals(this.inventoryReceiptId)))
      this.inventoryReceiptId = inventoryReceiptId;
  }

  public Integer getInventoryLocationId() {
    return inventoryLocationId;
  }
  public void setInventoryLocationId(Integer inventoryLocationId) {
    if((inventoryLocationId == null && this.inventoryLocationId != null) || 
       (inventoryLocationId != null && !inventoryLocationId.equals(this.inventoryLocationId)))
      this.inventoryLocationId = inventoryLocationId;
  }

  public Integer getQuantity() {
    return quantity;
  }
  public void setQuantity(Integer quantity) {
    if((quantity == null && this.quantity != null) || 
       (quantity != null && !quantity.equals(this.quantity)))
      this.quantity = quantity;
  }

  
  public void setClone() {
    try {
      original = (TransReceiptLocation)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryReceiptId,original.inventoryReceiptId,doc,"inventory_receipt_id");

      AuditUtil.getChangeXML(inventoryLocationId,original.inventoryLocationId,doc,"inventory_location_id");

      AuditUtil.getChangeXML(quantity,original.quantity,doc,"quantity");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "trans_receipt_location";
  }
  
}   
