
package org.openelis.entity;

/**
  * InventoryTransaction Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.OrderItem;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="inventory_transaction")
@EntityListeners({AuditUtil.class})
public class InventoryTransaction implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="from_location_id")
  private Integer fromLocationId;             

  @Column(name="from_receipt_id")
  private Integer fromReceiptId;             

  @Column(name="from_adjustment_id")
  private Integer fromAdjustmentId;             

  @Column(name="to_order_id")
  private Integer toOrderId;             

  @Column(name="to_location_id")
  private Integer toLocationId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="quantity")
  private Double quantity;             
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_location_id", insertable = false, updatable = false)
  private InventoryLocation fromLocation;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_receipt_id", insertable = false, updatable = false)
  private InventoryReceipt fromReceipt;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_order_id", insertable = false, updatable = false)
  private OrderItem toOrder;

  @Transient
  private InventoryTransaction original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getFromLocationId() {
    return fromLocationId;
  }
  public void setFromLocationId(Integer fromLocationId) {
    if((fromLocationId == null && this.fromLocationId != null) || 
       (fromLocationId != null && !fromLocationId.equals(this.fromLocationId)))
      this.fromLocationId = fromLocationId;
  }

  public Integer getFromReceiptId() {
    return fromReceiptId;
  }
  public void setFromReceiptId(Integer fromReceiptId) {
    if((fromReceiptId == null && this.fromReceiptId != null) || 
       (fromReceiptId != null && !fromReceiptId.equals(this.fromReceiptId)))
      this.fromReceiptId = fromReceiptId;
  }

  public Integer getFromAdjustmentId() {
    return fromAdjustmentId;
  }
  public void setFromAdjustmentId(Integer fromAdjustmentId) {
    if((fromAdjustmentId == null && this.fromAdjustmentId != null) || 
       (fromAdjustmentId != null && !fromAdjustmentId.equals(this.fromAdjustmentId)))
      this.fromAdjustmentId = fromAdjustmentId;
  }

  public Integer getToOrderId() {
    return toOrderId;
  }
  public void setToOrderId(Integer toOrderId) {
    if((toOrderId == null && this.toOrderId != null) || 
       (toOrderId != null && !toOrderId.equals(this.toOrderId)))
      this.toOrderId = toOrderId;
  }

  public Integer getToLocationId() {
    return toLocationId;
  }
  public void setToLocationId(Integer toLocationId) {
    if((toLocationId == null && this.toLocationId != null) || 
       (toLocationId != null && !toLocationId.equals(this.toLocationId)))
      this.toLocationId = toLocationId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
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
      original = (InventoryTransaction)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(fromLocationId,original.fromLocationId,doc,"from_location_id");

      AuditUtil.getChangeXML(fromReceiptId,original.fromReceiptId,doc,"from_receipt_id");

      AuditUtil.getChangeXML(fromAdjustmentId,original.fromAdjustmentId,doc,"from_adjustment_id");

      AuditUtil.getChangeXML(toOrderId,original.toOrderId,doc,"to_order_id");

      AuditUtil.getChangeXML(toLocationId,original.toLocationId,doc,"to_location_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(quantity,original.quantity,doc,"quantity");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_transaction";
  }
  
  public InventoryLocation getFromLocation() {
      return fromLocation;
  }

  public void setFromLocation(InventoryLocation fromLocation) {
      this.fromLocation = fromLocation;
  }

  public OrderItem getToOrder() {
      return toOrder;
  }

  public void setToOrder(OrderItem toOrder) {
      this.toOrder = toOrder;
  }

  public InventoryReceipt getFromReceipt() {
      return fromReceipt;
  }

  public void setFromReceipt(InventoryReceipt fromReceipt) {
      this.fromReceipt = fromReceipt;
  }
  
}   
