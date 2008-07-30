
package org.openelis.entity;

/**
  * TransReceiptLocation Entity POJO for database 
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

@NamedQueries({ @NamedQuery(name = "TransReceiptLocation.TransIdsLocIdsByReceiptId", query = "select tr.id, tr.inventoryLocationId from TransReceiptLocation tr where tr.inventoryReceiptId = :id " +
                                            " and quantity > 0 order by tr.quantity desc")})


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
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_receipt_id", insertable = false, updatable = false)
  private InventoryReceipt inventoryReceipt;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
  private InventoryLocation inventoryLocation;


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
public InventoryLocation getInventoryLocation() {
    return inventoryLocation;
}
public InventoryReceipt getInventoryReceipt() {
    return inventoryReceipt;
}
  
}   
