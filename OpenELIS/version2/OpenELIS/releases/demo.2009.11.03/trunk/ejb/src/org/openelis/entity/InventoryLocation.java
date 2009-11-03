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
  * InventoryLocation Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({ @NamedQuery(name = "InventoryLocation.InventoryLocation", query = "select new org.openelis.domain.InventoryLocationDO(i.id,i.inventoryItemId, i.inventoryItem.name,i.lotNumber, " +
                            " s.name,i.quantityOnhand, i.expirationDate) from InventoryLocation i left join i.storageLocation s " +
                            " where i.id = :id"),
@NamedQuery(name = "InventoryLocation.InventoryLocationByItem", query = "select new org.openelis.domain.InventoryLocationDO(i.id,i.inventoryItemId,i.inventoryItem.name,i.lotNumber, " +
                                        " childLoc.name,childLoc.location, parentLoc.name, childLoc.storageUnit.description, i.quantityOnhand, i.expirationDate) " +
                                        " from InventoryLocation i left join i.storageLocation childLoc " +
                                        " left join childLoc.parentStorageLocation parentLoc " +
                                        " where i.inventoryItemId = :id and i.quantityOnhand > 0"),
@NamedQuery(name = "InventoryLocation.AutoCompleteByName", query = "select new org.openelis.domain.StorageLocationAutoDO(childLoc.id, childLoc.name, i.id, childLoc.location, " +
                                        " parentLoc.name, childLoc.storageUnit.description) " +
                                        " from InventoryLocation i left join i.storageLocation childLoc left join childLoc.parentStorageLocation parentLoc where " +
                                        " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))" +
                                        " and (childLoc.name like :name OR childLoc.location like :loc OR childLoc.storageUnit.description like :desc) " +
                                        " order by childLoc.name"),
@NamedQuery(name = "InventoryLocation.AutoCompleteByNameInvId", query = "select new org.openelis.domain.StorageLocationAutoDO(childLoc.id, childLoc.name, childLoc.location, " +
                                        " parentLoc.name, childLoc.storageUnit.description, i.id, i.quantityOnhand, i.lotNumber) " +
                                        " from InventoryLocation i left join i.storageLocation childLoc left join childLoc.parentStorageLocation parentLoc where " +
                                        " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))" +
                                        " and (childLoc.name like :name OR childLoc.location like :loc OR childLoc.storageUnit.description like :desc) " +
                                        " and i.inventoryItemId = :id order by childLoc.name"),
@NamedQuery(name = "InventoryLocation.IdByStorageLocation", query = "select i.id from InventoryLocation i where i.storageLocationId = :id"),
@NamedQuery(name = "InventoryLocation.LocationInfoForAdjustmentFromId", query = "select distinct new org.openelis.domain.InventoryAdjLocationAutoDO(loc.id, ii.id, ii.name, dictStore.entry, " +
                                        " sl.name, sl.location, sl.storageUnit.description, loc.quantityOnhand) " + 
                                        " from InventoryLocation loc LEFT JOIN loc.inventoryItem ii LEFT JOIN loc.storageLocation sl, Dictionary dictStore where ii.storeId = dictStore.id AND loc.id = :id " + 
                                        " AND ii.storeId = :store ")})

@Entity
@Table(name="inventory_location")
@EntityListeners({AuditUtil.class})
public class InventoryLocation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item_id")
  private Integer inventoryItemId;             

  @Column(name="lot_number")
  private String lotNumber;             

  @Column(name="storage_location_id")
  private Integer storageLocationId;             

  @Column(name="quantity_onhand")
  private Integer quantityOnhand;             

  @Column(name="expiration_date")
  private Date expirationDate;  
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "storage_location_id", insertable = false, updatable = false)
  private StorageLocation storageLocation;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
  private InventoryItem inventoryItem;


  @Transient
  private InventoryLocation original;

  
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

  public String getLotNumber() {
    return lotNumber;
  }
  public void setLotNumber(String lotNumber) {
    if((lotNumber == null && this.lotNumber != null) || 
       (lotNumber != null && !lotNumber.equals(this.lotNumber)))
      this.lotNumber = lotNumber;
  }

  public Integer getStorageLocationId() {
    return storageLocationId;
  }
  public void setStorageLocationId(Integer storageLocationId) {
    if((storageLocationId == null && this.storageLocationId != null) || 
       (storageLocationId != null && !storageLocationId.equals(this.storageLocationId)))
      this.storageLocationId = storageLocationId;
  }

  public Integer getQuantityOnhand() {
    return quantityOnhand;
  }
  public void setQuantityOnhand(Integer quantityOnhand) {
    if((quantityOnhand == null && this.quantityOnhand != null) || 
       (quantityOnhand != null && !quantityOnhand.equals(this.quantityOnhand)))
      this.quantityOnhand = quantityOnhand;
  }
    
  public Datetime getExpirationDate() {
    if(expirationDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,expirationDate);
  }
  public void setExpirationDate (Datetime expirationDate){
    if((expirationDate == null && this.expirationDate != null) || (expirationDate != null && this.expirationDate == null) || 
       (expirationDate != null && !expirationDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.expirationDate))))
      this.expirationDate = expirationDate.getDate();
  }

  
  public void setClone() {
    try {
      original = (InventoryLocation)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(inventoryItemId,original.inventoryItemId,doc,"inventory_item_id");

      AuditUtil.getChangeXML(lotNumber,original.lotNumber,doc,"lot_number");

      AuditUtil.getChangeXML(storageLocationId,original.storageLocationId,doc,"storage_location_id");

      AuditUtil.getChangeXML(quantityOnhand,original.quantityOnhand,doc,"quantity_onhand");

      AuditUtil.getChangeXML(expirationDate,original.expirationDate,doc,"expiration_date");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_location";
  }
  
  public StorageLocation getStorageLocation() {
      return storageLocation;
  }
  public void setStorageLocation(StorageLocation storageLocation) {
      this.storageLocation = storageLocation;
  }
public InventoryItem getInventoryItem() {
    return inventoryItem;
}
  
}   
