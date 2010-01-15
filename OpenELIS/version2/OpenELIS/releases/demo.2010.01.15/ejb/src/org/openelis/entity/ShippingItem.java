/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * ShippingItem Entity POJO for database 
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

import org.openelis.domain.ReferenceTable;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

    @NamedQueries( {
        @NamedQuery(name = "ShippingItem.ShippingItem", query = "select new org.openelis.domain.ShippingItemDO(s.id, s.shippingId, s.referenceTableId, s.referenceId, oi.quantity, loc.id, trans.id) "+
                               " from ShippingItem s LEFT JOIN s.orderItem oi LEFT JOIN oi.inventoryXUse trans LEFT JOIN trans.inventoryLocation loc where s.shippingId = :id")})
                               
@Entity
@Table(name="shipping_item")
@EntityListeners({AuditUtil.class})
public class ShippingItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="shipping_id")
  private Integer shippingId;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="reference_id")
  private Integer referenceId;          
  
  @Column(name="quantity")
  private Integer quantity;
  
  @Column(name="description")
  private String description;   
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private OrderItem orderItem;

  @Transient
  private ShippingItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getShippingId() {
    return shippingId;
  }
  public void setShippingId(Integer shippingId) {
    if((shippingId == null && this.shippingId != null) || 
       (shippingId != null && !shippingId.equals(this.shippingId)))
      this.shippingId = shippingId;
  }

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    if((referenceId == null && this.referenceId != null) || 
       (referenceId != null && !referenceId.equals(this.referenceId)))
      this.referenceId = referenceId;
  }

  public Integer getQuantity() {
      return quantity;
    }
  public void setQuantity(Integer quantity) {
      if((quantity == null && this.quantity != null) || 
         (quantity != null && !quantity.equals(this.quantity)))
        this.quantity = quantity;
  }
  
  public String getDescription() {
      return description;
    }
  
  public void setDescription(String description) {
      if((description == null && this.description != null) || 
         (description != null && !description.equals(this.description)))
          this.description = description;
  }
  
  public OrderItem getOrderItem() {
      return orderItem;
  }
  
  public void setOrderItem(OrderItem orderItem) {
      this.orderItem = orderItem;
  }
  
  public void setClone() {
    try {
        original = (ShippingItem)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SHIPPING_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("shipping_id", shippingId, original.shippingId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("quantity", quantity, original.quantity)
                 .setField("description", description, original.description);

        return audit;
  }
}   
