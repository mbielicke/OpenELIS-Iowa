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
  * InventoryAdjustment Entity POJO for database 
  */

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "InventoryAdjustment.InventoryAdjustment", query = "select distinct new org.openelis.domain.InventoryAdjustmentDO(ia.id, ia.description, ia.systemUserId, " +
                                " ia.adjustmentDate, ii.storeId) from InventoryAdjustment ia LEFT JOIN ia.transAdjustmentLocation trans LEFT JOIN " +
                                " trans.inventoryLocation.inventoryItem ii where ia.id = :id ")})
    
@Entity
@Table(name="inventory_adjustment")
@EntityListeners({AuditUtil.class})
public class InventoryAdjustment implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="description")
  private String description;             

  @Column(name="system_user_id")
  private Integer systemUserId;             

  @Column(name="adjustment_date")
  private Date adjustmentDate;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_adjustment_id", insertable = false, updatable = false)
  private Collection<InventoryXAdjust> transAdjustmentLocation;
  

  @Transient
  private InventoryAdjustment original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getSystemUserId() {
    return systemUserId;
  }
  public void setSystemUserId(Integer systemUserId) {
    if((systemUserId == null && this.systemUserId != null) || 
       (systemUserId != null && !systemUserId.equals(this.systemUserId)))
      this.systemUserId = systemUserId;
  }

  public Datetime getAdjustmentDate() {
    if(adjustmentDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.DAY,adjustmentDate);
  }
      
  public void setAdjustmentDate (Datetime adjustmentDate){
    if((adjustmentDate == null && this.adjustmentDate != null) || (adjustmentDate != null && this.adjustmentDate == null) || 
       (adjustmentDate != null && !adjustmentDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.adjustmentDate))))
      this.adjustmentDate = adjustmentDate.getDate();
  }
  
  public Collection<InventoryXAdjust> getTransAdjustmentLocation() {
      return transAdjustmentLocation;
  }

  public void setClone() {
    try {
        original = (InventoryAdjustment)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_ADJUSTMENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("description", description, original.description)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("adjustment_date", adjustmentDate, original.adjustmentDate);

        return audit;
    }
  
}   
