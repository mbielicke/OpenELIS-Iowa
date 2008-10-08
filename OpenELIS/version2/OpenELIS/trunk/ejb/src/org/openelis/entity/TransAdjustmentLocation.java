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
  * TransAdjustmentLocation Entity POJO for database 
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
    @NamedQuery(name = "TransAdjustmentLocation.TransAdjustmentLocation", query = "select distinct new org.openelis.domain.InventoryAdjustmentChildDO(trans.id, il.id, " +
                                " il.inventoryItemId, ii.name, storLoc.name, storLoc.storageUnit.description, storLoc.location, " +
                                " trans.physicalCount, trans.quantity)  from TransAdjustmentLocation trans LEFT JOIN trans.inventoryLocation il " +
                                " LEFT JOIN il.inventoryItem ii LEFT JOIN il.storageLocation storLoc " +
                                " where trans.inventoryAdjustmentId = :id ORDER BY il.id ")})
                                
@Entity
@Table(name="trans_adjustment_location")
@EntityListeners({AuditUtil.class})
public class TransAdjustmentLocation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_adjustment_id")
  private Integer inventoryAdjustmentId;             

  @Column(name="inventory_location_id")
  private Integer inventoryLocationId;             

  @Column(name="quantity")
  private Integer quantity;             

  @Column(name="physical_count")
  private Integer physicalCount;    
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
  private InventoryLocation inventoryLocation;


  @Transient
  private TransAdjustmentLocation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInventoryAdjustmentId() {
    return inventoryAdjustmentId;
  }
  public void setInventoryAdjustmentId(Integer inventoryAdjustmentId) {
    if((inventoryAdjustmentId == null && this.inventoryAdjustmentId != null) || 
       (inventoryAdjustmentId != null && !inventoryAdjustmentId.equals(this.inventoryAdjustmentId)))
      this.inventoryAdjustmentId = inventoryAdjustmentId;
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

  public Integer getPhysicalCount() {
    return physicalCount;
  }
  public void setPhysicalCount(Integer physicalCount) {
    if((physicalCount == null && this.physicalCount != null) || 
       (physicalCount != null && !physicalCount.equals(this.physicalCount)))
      this.physicalCount = physicalCount;
  }

  
  public void setClone() {
    try {
      original = (TransAdjustmentLocation)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryAdjustmentId,original.inventoryAdjustmentId,doc,"inventory_adjustment_id");

      AuditUtil.getChangeXML(inventoryLocationId,original.inventoryLocationId,doc,"inventory_location_id");

      AuditUtil.getChangeXML(quantity,original.quantity,doc,"quantity");

      AuditUtil.getChangeXML(physicalCount,original.physicalCount,doc,"physical_count");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "trans_adjustment_location";
  }
public InventoryLocation getInventoryLocation() {
    return inventoryLocation;
}
  
}   
