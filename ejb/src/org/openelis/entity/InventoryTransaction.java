package org.openelis.entity;

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

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Entity
@Table(name="inventory_transaction")
@EntityListeners({AuditUtil.class})
public class InventoryTransaction implements Auditable, Cloneable {
    
    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;             

    @Column(name="from_location")
    private Integer fromLocationId;
    
    @Column(name="from_receipt")
    private Integer fromReceiptId;
    
    @Column(name="from_adjustment")
    private Integer fromAdjustmentId;
    
    @Column(name="to_order")
    private Integer toOrderId;
    
    @Column(name="to_location")
    private Integer toLocationId;
    
    @Column(name="type")
    private Integer type; 
    @Column(name="quantity")
    private Integer quantity;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location", insertable = false, updatable = false)
    private InventoryLocation fromLocation;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_receipt", insertable = false, updatable = false)
    private InventoryReceipt fromReceipt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_order", insertable = false, updatable = false)
    private OrderItem toOrder;
    
    @Transient
    private InventoryTransaction original;

    public Integer getFromAdjustmentId() {
        return fromAdjustmentId;
    }

    public void setFromAdjustmentId(Integer fromAdjustment) {
        this.fromAdjustmentId = fromAdjustment;
    }

    public Integer getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Integer fromLocation) {
        this.fromLocationId = fromLocation;
    }

    public Integer getFromReceiptId() {
        return fromReceiptId;
    }

    public void setFromReceiptId(Integer fromReceipt) {
        this.fromReceiptId = fromReceipt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Integer toLocation) {
        this.toLocationId = toLocation;
    }

    public Integer getToOrderId() {
        return toOrderId;
    }

    public void setToOrderId(Integer toOrder) {
        this.toOrderId = toOrder;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
          
          if((id == null && original.id != null) || 
             (id != null && !id.equals(original.id))){
            Element elem = doc.createElement("id");
            elem.appendChild(doc.createTextNode(original.id.toString().trim()));
            root.appendChild(elem);
          }      

          if((fromLocationId == null && original.fromLocationId != null) || 
             (fromLocationId != null && !fromLocationId.equals(original.fromLocationId))){
            Element elem = doc.createElement("from_location");
            elem.appendChild(doc.createTextNode(original.fromLocationId.toString().trim()));
            root.appendChild(elem);
          }      

          if((fromReceiptId == null && original.fromReceiptId != null) || 
             (fromReceiptId != null && !fromReceiptId.equals(original.fromReceiptId))){
            Element elem = doc.createElement("from_receipt");
            elem.appendChild(doc.createTextNode(original.fromReceiptId.toString().trim()));
            root.appendChild(elem);
          }      

          if((fromAdjustmentId == null && original.fromAdjustmentId != null) || 
             (fromAdjustmentId != null && !fromAdjustmentId.equals(original.fromAdjustmentId))){
            Element elem = doc.createElement("from_adjustment");
            elem.appendChild(doc.createTextNode(original.fromAdjustmentId.toString().trim()));
            root.appendChild(elem);
          } 
          
          if((toOrderId == null && original.toOrderId != null) || 
             (toOrderId != null && !toOrderId.equals(original.toOrderId))){
            Element elem = doc.createElement("to_order");
            elem.appendChild(doc.createTextNode(original.toOrderId.toString().trim()));
            root.appendChild(elem);
          }   
          
          if((toLocationId == null && original.toLocationId != null) || 
             (toLocationId != null && !toLocationId.equals(original.toLocationId))){
            Element elem = doc.createElement("to_location");
            elem.appendChild(doc.createTextNode(original.toLocationId.toString().trim()));
            root.appendChild(elem);
          }   
          
          if((type == null && original.type != null) || 
             (type != null && !type.equals(original.type))){
            Element elem = doc.createElement("type");
            elem.appendChild(doc.createTextNode(original.type.toString().trim()));
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
