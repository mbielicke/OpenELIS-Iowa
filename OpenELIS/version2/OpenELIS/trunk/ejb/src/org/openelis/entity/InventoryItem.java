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
  * InventoryItem Entity POJO for database 
  */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/*
@NamedQueries({ @NamedQuery(name = "InventoryItem.InventoryItem", query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description,i.categoryId,i.storeId,i.quantityMinLevel, " +
                               " i.quantityMaxLevel,i.quantityToReorder, i.dispensedUnitsId,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive, " +
                            " i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNoInventory,i.productUri,i.averageLeadTime, i.averageCost, i.averageDailyUse, i.parentInventoryItemId, "+
                            " pi.name, i.parentRatio) from InventoryItem i LEFT JOIN i.parentInventoryItem pi where i.id = :id"),
     @NamedQuery(name = "InventoryItem.AutocompleteByNameStoreCurrentName", query = "select new org.openelis.domain.IdNameDO(i.id, i.name) " +
                            "  from InventoryItem i where i.name like :name and i.storeId = :store and i.name != :currentName and i.isActive = 'Y' order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteByName", query = "select new org.openelis.domain.IdNameDO(i.id, i.name) " +
                            "  from InventoryItem i where i.name like :name and i.isActive = 'Y' order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreLocByName", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, d.entry, il.id, childLoc.name, " +
                            " childLoc.location, parentLoc.name, childLoc.storageUnit.description, il.lotNumber, il.expirationDate, il.quantityOnhand) " +
                            "  from InventoryItem i left join i.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where "+
                            " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))" +
                            " and i.storeId = d.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and i.isSubAssembly = 'N' order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreLocByNameSubItems", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, i.description, d.entry, il.id, childLoc.name, " +
                            " childLoc.location, parentLoc.name, childLoc.storageUnit.description, il.lotNumber, il.expirationDate, il.quantityOnhand, disUnit.entry, " +
                            " i.isBulk, i.isLotMaintained, i.isSerialMaintained, i.parentRatio, i.parentInventoryItemId) " +
                            "  from InventoryItem i left join i.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d, Dictionary disUnit where "+
                            " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))" +
                            " and i.storeId = d.id and i.dispensedUnitsId = disUnit.id " +
                            " and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreLocByNameMainStore", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, d.entry, il.id, childLoc.name, " +
                            " childLoc.location, parentLoc.name, childLoc.storageUnit.description, il.lotNumber, il.expirationDate, il.quantityOnhand) " +
                            "  from InventoryItem i left join i.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where " + 
                            " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))" +
                            " and i.storeId = d.id  and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and i.isSubAssembly = 'N' and d.systemName = 'inv_main_store' " +
                            " order by i.name"), 
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreByName", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " +
                            " i.isBulk, i.isSerialMaintained) " +
                            " from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and i.isSubAssembly = 'N' order by i.name"),    
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreByNameMainStoreSubItems", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " +
                            " i.isBulk, i.isLotMaintained, i.isSerialMaintained) " +
                            " from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and store.systemName = 'inv_main_store'  order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreChildrenByNameId", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " +
                            " i.isBulk, i.isLotMaintained, i.isSerialMaintained, i.parentRatio, i.parentInventoryItemId) " +
                            " from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.parentInventoryItemId = :id and " +
                            " i.name like :name and i.isActive = 'Y' and i.isNotForSale = 'N' order by i.name "),           
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreChildrenByNameParentId", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " +
                            " i.isBulk, i.isLotMaintained, i.isSerialMaintained, i.parentRatio, i.parentInventoryItemId) " +
                            " from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.id = :id and " +
                            " i.name like :name and i.isActive = 'Y' and i.isNotForSale = 'N' order by i.name "),
     @NamedQuery(name = "InventoryItem.AutocompleteItemStoreByNameReceipt", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, " + 
                            " i.isBulk, i.isSerialMaintained) " +
                            " from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemByNameStore", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, d.entry, il.id, childLoc.name, " +
                            " childLoc.location, parentLoc.name, childLoc.storageUnit.description, il.lotNumber, il.expirationDate, il.quantityOnhand) " +
                            "  from InventoryItem i left join i.inventoryLocation il left join il.storageLocation childLoc " +
                            " left join childLoc.parentStorageLocation parentLoc, Dictionary d where i.storeId = d.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and i.isSubAssembly = 'N' and ((i.isSerialMaintained = 'Y' and il.quantityOnhand > 0) or (i.isSerialMaintained = 'N')) and " +
                            " i.storeId = :store order by i.name"),
     @NamedQuery(name = "InventoryItem.AutocompleteItemByNameKits", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, store.entry, i.description, disUnit.entry, i.isBulk, i.isSerialMaintained) " +
                            "  from InventoryItem i, Dictionary store, Dictionary disUnit where i.storeId = store.id and i.dispensedUnitsId = disUnit.id and i.name like :name and i.isActive = 'Y' " +
                            " and i.isNotForSale = 'N' and i.isSubAssembly = 'N' and 0 < (select count(ic.id) from InventoryComponent ic where ic.inventoryItemId=i.id) order by i.name"),
     @NamedQuery(name = "InventoryItem.DescriptionById", query = "select i.description from InventoryItem i where i.id = :id"),
     @NamedQuery(name = "InventoryItem.UpdateNameStoreCompare", query = "select i.id from InventoryItem i where i.name = :name and i.storeId = :store AND i.id != :id"),
     @NamedQuery(name = "InventoryItem.AddNameStoreCompare", query = "select i.id from InventoryItem i where i.name = :name AND i.storeId = :store"),
     @NamedQuery(name = "InventoryItem.ValidateComponentWithItemStore", query = "select i.id from InventoryItem i where " +
                            " i.storeId = :store AND i.id = :id")})
*/
     
@Entity
@Table(name="inventory_item")
@EntityListeners({AuditUtil.class})
public class InventoryItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="category_id")
  private Integer categoryId;             

  @Column(name="store_id")
  private Integer storeId;             

  @Column(name="quantity_min_level")
  private Integer quantityMinLevel;             

  @Column(name="quantity_max_level")
  private Integer quantityMaxLevel;             

  @Column(name="quantity_to_reorder")
  private Integer quantityToReorder;             

  @Column(name="dispensed_units_id")
  private Integer dispensedUnitsId;             

  @Column(name="is_reorder_auto")
  private String isReorderAuto;             

  @Column(name="is_lot_maintained")
  private String isLotMaintained;             

  @Column(name="is_serial_maintained")
  private String isSerialMaintained;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="is_bulk")
  private String isBulk;             

  @Column(name="is_not_for_sale")
  private String isNotForSale;             

  @Column(name="is_sub_assembly")
  private String isSubAssembly;             

  @Column(name="is_labor")
  private String isLabor;             

  @Column(name="is_no_inventory")
  private String isNoInventory;             

  @Column(name="product_uri")
  private String productUri;             

  @Column(name="average_lead_time")
  private Integer averageLeadTime;             

  @Column(name="average_cost")
  private Double averageCost;             

  @Column(name="average_daily_use")
  private Integer averageDailyUse;
  
  @Column(name="parent_inventory_item")
  private Integer parentInventoryItemId;     
  
  @Column(name="parent_ratio")
  private Integer parentRatio;     

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id")
  private Collection<InventoryComponent> inventoryComponent;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id")
  private Collection<InventoryLocation> inventoryLocation;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private Collection<Note> note;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_inventory_item", insertable = false, updatable = false)
  private InventoryItem parentInventoryItem;

  @Transient
  private InventoryItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getCategoryId() {
    return categoryId;
  }
  public void setCategoryId(Integer categoryId) {
    if((categoryId == null && this.categoryId != null) || 
       (categoryId != null && !categoryId.equals(this.categoryId)))
      this.categoryId = categoryId;
  }

  public Integer getStoreId() {
    return storeId;
  }
  public void setStoreId(Integer storeId) {
    if((storeId == null && this.storeId != null) || 
       (storeId != null && !storeId.equals(this.storeId)))
      this.storeId = storeId;
  }

  public Integer getQuantityMinLevel() {
    return quantityMinLevel;
  }
  public void setQuantityMinLevel(Integer quantityMinLevel) {
    if((quantityMinLevel == null && this.quantityMinLevel != null) || 
       (quantityMinLevel != null && !quantityMinLevel.equals(this.quantityMinLevel)))
      this.quantityMinLevel = quantityMinLevel;
  }

  public Integer getQuantityMaxLevel() {
    return quantityMaxLevel;
  }
  public void setQuantityMaxLevel(Integer quantityMaxLevel) {
    if((quantityMaxLevel == null && this.quantityMaxLevel != null) || 
       (quantityMaxLevel != null && !quantityMaxLevel.equals(this.quantityMaxLevel)))
      this.quantityMaxLevel = quantityMaxLevel;
  }

  public Integer getQuantityToReorder() {
    return quantityToReorder;
  }
  public void setQuantityToReorder(Integer quantityToReorder) {
    if((quantityToReorder == null && this.quantityToReorder != null) || 
       (quantityToReorder != null && !quantityToReorder.equals(this.quantityToReorder)))
      this.quantityToReorder = quantityToReorder;
  }

  public Integer getDispensedUnitsId() {
    return dispensedUnitsId;
  }
  public void setDispensedUnitsId(Integer dispensedUnitsId) {
    if((dispensedUnitsId == null && this.dispensedUnitsId != null) || 
       (dispensedUnitsId != null && !dispensedUnitsId.equals(this.dispensedUnitsId)))
      this.dispensedUnitsId = dispensedUnitsId;
  }

  public String getIsReorderAuto() {
    return isReorderAuto;
  }
  public void setIsReorderAuto(String isReorderAuto) {
    if((isReorderAuto == null && this.isReorderAuto != null) || 
       (isReorderAuto != null && !isReorderAuto.equals(this.isReorderAuto)))
      this.isReorderAuto = isReorderAuto;
  }

  public String getIsLotMaintained() {
    return isLotMaintained;
  }
  public void setIsLotMaintained(String isLotMaintained) {
    if((isLotMaintained == null && this.isLotMaintained != null) || 
       (isLotMaintained != null && !isLotMaintained.equals(this.isLotMaintained)))
      this.isLotMaintained = isLotMaintained;
  }

  public String getIsSerialMaintained() {
    return isSerialMaintained;
  }
  public void setIsSerialMaintained(String isSerialMaintained) {
    if((isSerialMaintained == null && this.isSerialMaintained != null) || 
       (isSerialMaintained != null && !isSerialMaintained.equals(this.isSerialMaintained)))
      this.isSerialMaintained = isSerialMaintained;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getIsBulk() {
    return isBulk;
  }
  public void setIsBulk(String isBulk) {
    if((isBulk == null && this.isBulk != null) || 
       (isBulk != null && !isBulk.equals(this.isBulk)))
      this.isBulk = isBulk;
  }

  public String getIsNotForSale() {
    return isNotForSale;
  }
  public void setIsNotForSale(String isNotForSale) {
    if((isNotForSale == null && this.isNotForSale != null) || 
       (isNotForSale != null && !isNotForSale.equals(this.isNotForSale)))
      this.isNotForSale = isNotForSale;
  }

  public String getIsSubAssembly() {
    return isSubAssembly;
  }
  public void setIsSubAssembly(String isSubAssembly) {
    if((isSubAssembly == null && this.isSubAssembly != null) || 
       (isSubAssembly != null && !isSubAssembly.equals(this.isSubAssembly)))
      this.isSubAssembly = isSubAssembly;
  }

  public String getIsLabor() {
    return isLabor;
  }
  public void setIsLabor(String isLabor) {
    if((isLabor == null && this.isLabor != null) || 
       (isLabor != null && !isLabor.equals(this.isLabor)))
      this.isLabor = isLabor;
  }

  public String getIsNoInventory() {
    return isNoInventory;
  }
  public void setIsNoInventory(String isNoInventory) {
    if((isNoInventory == null && this.isNoInventory != null) || 
       (isNoInventory != null && !isNoInventory.equals(this.isNoInventory)))
      this.isNoInventory = isNoInventory;
  }

  public String getProductUri() {
    return productUri;
  }
  public void setProductUri(String productUri) {
    if((productUri == null && this.productUri != null) || 
       (productUri != null && !productUri.equals(this.productUri)))
      this.productUri = productUri;
  }

  public Integer getAverageLeadTime() {
    return averageLeadTime;
  }
  public void setAverageLeadTime(Integer averageLeadTime) {
    if((averageLeadTime == null && this.averageLeadTime != null) || 
       (averageLeadTime != null && !averageLeadTime.equals(this.averageLeadTime)))
      this.averageLeadTime = averageLeadTime;
  }

  public Double getAverageCost() {
    return averageCost;
  }
  public void setAverageCost(Double averageCost) {
    if((averageCost == null && this.averageCost != null) || 
       (averageCost != null && !averageCost.equals(this.averageCost)))
      this.averageCost = averageCost;
  }

  public Integer getAverageDailyUse() {
    return averageDailyUse;
  }
  public void setAverageDailyUse(Integer averageDailyUse) {
    if((averageDailyUse == null && this.averageDailyUse != null) || 
       (averageDailyUse != null && !averageDailyUse.equals(this.averageDailyUse)))
      this.averageDailyUse = averageDailyUse;
  }
  
  public Integer getParentInventoryItemId() {
      return parentInventoryItemId;
  }
  
  public void setParentInventoryItemId(Integer parentInventoryItemId) {
      if((parentInventoryItemId == null && this.parentInventoryItemId != null) || 
         (parentInventoryItemId != null && !parentInventoryItemId.equals(this.parentInventoryItemId)))
        this.parentInventoryItemId = parentInventoryItemId;
  }
    
    public Integer getParentRatio() {
        return parentRatio;
    }
  
    public void setParentRatio(Integer parentRatio) {
        if((parentRatio == null && this.parentRatio != null) || 
           (parentRatio != null && !parentRatio.equals(this.parentRatio)))
            this.parentRatio = parentRatio;
    }
  
  public void setClone() {
    try {
      original = (InventoryItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(categoryId,original.categoryId,doc,"category_id");

      AuditUtil.getChangeXML(storeId,original.storeId,doc,"store_id");

      AuditUtil.getChangeXML(quantityMinLevel,original.quantityMinLevel,doc,"quantity_min_level");

      AuditUtil.getChangeXML(quantityMaxLevel,original.quantityMaxLevel,doc,"quantity_max_level");

      AuditUtil.getChangeXML(quantityToReorder,original.quantityToReorder,doc,"quantity_to_reorder");

      AuditUtil.getChangeXML(dispensedUnitsId,original.dispensedUnitsId,doc,"dispensed_units_id");

      AuditUtil.getChangeXML(isReorderAuto,original.isReorderAuto,doc,"is_reorder_auto");

      AuditUtil.getChangeXML(isLotMaintained,original.isLotMaintained,doc,"is_lot_maintained");

      AuditUtil.getChangeXML(isSerialMaintained,original.isSerialMaintained,doc,"is_serial_maintained");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(isBulk,original.isBulk,doc,"is_bulk");

      AuditUtil.getChangeXML(isNotForSale,original.isNotForSale,doc,"is_not_for_sale");

      AuditUtil.getChangeXML(isSubAssembly,original.isSubAssembly,doc,"is_sub_assembly");

      AuditUtil.getChangeXML(isLabor,original.isLabor,doc,"is_labor");

      AuditUtil.getChangeXML(isNoInventory,original.isNoInventory,doc,"is_no_inventory");

      AuditUtil.getChangeXML(productUri,original.productUri,doc,"product_uri");

      AuditUtil.getChangeXML(averageLeadTime,original.averageLeadTime,doc,"average_lead_time");

      AuditUtil.getChangeXML(averageCost,original.averageCost,doc,"average_cost");

      AuditUtil.getChangeXML(averageDailyUse,original.averageDailyUse,doc,"average_daily_use");
      
      AuditUtil.getChangeXML(parentInventoryItemId,original.parentInventoryItemId,doc,"parent_inventory_item");
      
      AuditUtil.getChangeXML(parentRatio,original.parentRatio,doc,"parent_ratio");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_item";
  }
  
  public Collection<Note> getNote() {
    return note;
  }
  public void setNote(Collection<Note> note) {
    this.note = note;
  }
  public Collection<InventoryComponent> getInventoryComponent() {
      return inventoryComponent;
  }
  public void setInventoryComponent(Collection<InventoryComponent> inventoryComponent) {
      this.inventoryComponent = inventoryComponent;
  }
  public Collection<InventoryLocation> getInventoryLocation() {
      return inventoryLocation;
  }
  public void setInventoryLocation(Collection<InventoryLocation> inventoryLocation) {
      this.inventoryLocation = inventoryLocation;
  }
public InventoryItem getParentInventoryItem() {
    return parentInventoryItem;
}
public void setParentInventoryItem(InventoryItem parentInventoryItem) {
    this.parentInventoryItem = parentInventoryItem;
}
  
}   
