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

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "InventoryItem.FetchById",
                query = "select new org.openelis.domain.InventoryItemViewDO(i.id,i.name,i.description," +
                		"i.categoryId,i.storeId,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
                		"i.dispensedUnitsId,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive," +
                        "i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNotInventoried,i.productUri," +
                        "i.averageLeadTime,i.averageCost,i.averageDailyUse,i.parentInventoryItemId,"+
                        "i.parentRatio,p.name)"
                      + " from InventoryItem i LEFT JOIN i.parentInventoryItem p where i.id = :id"),
    @NamedQuery( name = "InventoryItem.FetchActiveByName",
                query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description," +
                        "i.categoryId,i.storeId,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
                        "i.dispensedUnitsId,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive," +
                        "i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNotInventoried,i.productUri," +
                        "i.averageLeadTime,i.averageCost,i.averageDailyUse,i.parentInventoryItemId,"+
                        "i.parentRatio)"
                      + " from InventoryItem i where i.isActive='Y' and i.name like :name"),
    @NamedQuery( name = "InventoryItem.FetchActiveByNameAndStore",
                query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description," +
                        "i.categoryId,i.storeId,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
                        "i.dispensedUnitsId,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive," +
                        "i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNotInventoried,i.productUri," +
                        "i.averageLeadTime,i.averageCost,i.averageDailyUse,i.parentInventoryItemId,"+
                        "i.parentRatio)"
                      + " from InventoryItem i where i.isActive='Y' and i.storeId = :storeId and i.name like :name"), 
    @NamedQuery( name = "InventoryItem.FetchActiveByNameAndParentInventoryItem",
                query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description," +
                        "i.categoryId,i.storeId,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
                        "i.dispensedUnitsId,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive," +
                        "i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNotInventoried,i.productUri," +
                        "i.averageLeadTime,i.averageCost,i.averageDailyUse,i.parentInventoryItemId,"+
                        "i.parentRatio)"
                      + " from InventoryItem i where i.isActive='Y' and"
                      + " (i.parentInventoryItemId = :parentInventoryItemId or i.id = :parentInventoryItemId)"
                      + " and i.name like :name ")                  
             })

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
@Table(name = "inventory_item")
@EntityListeners( {AuditUtil.class})
public class InventoryItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                        id;

    @Column(name = "name")
    private String                         name;

    @Column(name = "description")
    private String                         description;

    @Column(name = "category_id")
    private Integer                        categoryId;

    @Column(name = "store_id")
    private Integer                        storeId;

    @Column(name = "quantity_min_level")
    private Integer                        quantityMinLevel;

    @Column(name = "quantity_max_level")
    private Integer                        quantityMaxLevel;

    @Column(name = "quantity_to_reorder")
    private Integer                        quantityToReorder;

    @Column(name = "dispensed_units_id")
    private Integer                        dispensedUnitsId;

    @Column(name = "is_reorder_auto")
    private String                         isReorderAuto;

    @Column(name = "is_lot_maintained")
    private String                         isLotMaintained;

    @Column(name = "is_serial_maintained")
    private String                         isSerialMaintained;

    @Column(name = "is_active")
    private String                         isActive;

    @Column(name = "is_bulk")
    private String                         isBulk;

    @Column(name = "is_not_for_sale")
    private String                         isNotForSale;

    @Column(name = "is_sub_assembly")
    private String                         isSubAssembly;

    @Column(name = "is_labor")
    private String                         isLabor;

    @Column(name = "is_not_inventoried")
    private String                         isNotInventoried;

    @Column(name = "product_uri")
    private String                         productUri;

    @Column(name = "average_lead_time")
    private Integer                        averageLeadTime;

    @Column(name = "average_cost")
    private Double                         averageCost;

    @Column(name = "average_daily_use")
    private Integer                        averageDailyUse;

    @Column(name = "parent_inventory_item_id")
    private Integer                        parentInventoryItemId;

    @Column(name = "parent_ratio")
    private Integer                        parentRatio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_inventory_item_id", insertable = false, updatable = false)
    private InventoryItem                  parentInventoryItem;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private Collection<InventoryComponent> inventoryComponent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private Collection<InventoryLocation>  inventoryLocation;

    @Transient
    private InventoryItem                  original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        if (DataBaseUtil.isDifferent(categoryId, this.categoryId))
            this.categoryId = categoryId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        if (DataBaseUtil.isDifferent(storeId, this.storeId))
            this.storeId = storeId;
    }

    public Integer getQuantityMinLevel() {
        return quantityMinLevel;
    }

    public void setQuantityMinLevel(Integer quantityMinLevel) {
        if (DataBaseUtil.isDifferent(quantityMinLevel, this.quantityMinLevel))
            this.quantityMinLevel = quantityMinLevel;
    }

    public Integer getQuantityMaxLevel() {
        return quantityMaxLevel;
    }

    public void setQuantityMaxLevel(Integer quantityMaxLevel) {
        if (DataBaseUtil.isDifferent(quantityMaxLevel, this.quantityMaxLevel))
            this.quantityMaxLevel = quantityMaxLevel;
    }

    public Integer getQuantityToReorder() {
        return quantityToReorder;
    }

    public void setQuantityToReorder(Integer quantityToReorder) {
        if (DataBaseUtil.isDifferent(quantityToReorder, this.quantityToReorder))
            this.quantityToReorder = quantityToReorder;
    }

    public Integer getDispensedUnitsId() {
        return dispensedUnitsId;
    }

    public void setDispensedUnitsId(Integer dispensedUnitsId) {
        if (DataBaseUtil.isDifferent(dispensedUnitsId, this.dispensedUnitsId))
            this.dispensedUnitsId = dispensedUnitsId;
    }

    public String getIsReorderAuto() {
        return isReorderAuto;
    }

    public void setIsReorderAuto(String isReorderAuto) {
        if (DataBaseUtil.isDifferent(isReorderAuto, this.isReorderAuto))
            this.isReorderAuto = isReorderAuto;
    }

    public String getIsLotMaintained() {
        return isLotMaintained;
    }

    public void setIsLotMaintained(String isLotMaintained) {
        if (DataBaseUtil.isDifferent(isLotMaintained, this.isLotMaintained))
            this.isLotMaintained = isLotMaintained;
    }

    public String getIsSerialMaintained() {
        return isSerialMaintained;
    }

    public void setIsSerialMaintained(String isSerialMaintained) {
        if (DataBaseUtil.isDifferent(isSerialMaintained, this.isSerialMaintained))
            this.isSerialMaintained = isSerialMaintained;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public String getIsBulk() {
        return isBulk;
    }

    public void setIsBulk(String isBulk) {
        if (DataBaseUtil.isDifferent(isBulk, this.isBulk))
            this.isBulk = isBulk;
    }

    public String getIsNotForSale() {
        return isNotForSale;
    }

    public void setIsNotForSale(String isNotForSale) {
        if (DataBaseUtil.isDifferent(isNotForSale, this.isNotForSale))
            this.isNotForSale = isNotForSale;
    }

    public String getIsSubAssembly() {
        return isSubAssembly;
    }

    public void setIsSubAssembly(String isSubAssembly) {
        if (DataBaseUtil.isDifferent(isSubAssembly, this.isSubAssembly))
            this.isSubAssembly = isSubAssembly;
    }

    public String getIsLabor() {
        return isLabor;
    }

    public void setIsLabor(String isLabor) {
        if (DataBaseUtil.isDifferent(isLabor, this.isLabor))
            this.isLabor = isLabor;
    }

    public String getIsNotInventoried() {
        return isNotInventoried;
    }

    public void setIsNotInventoried(String isNotInventoried) {
        if (DataBaseUtil.isDifferent(isNotInventoried, this.isNotInventoried))
            this.isNotInventoried = isNotInventoried;
    }

    public String getProductUri() {
        return productUri;
    }

    public void setProductUri(String productUri) {
        if (DataBaseUtil.isDifferent(productUri, this.productUri))
            this.productUri = productUri;
    }

    public Integer getAverageLeadTime() {
        return averageLeadTime;
    }

    public void setAverageLeadTime(Integer averageLeadTime) {
        if (DataBaseUtil.isDifferent(averageLeadTime, this.averageLeadTime))
            this.averageLeadTime = averageLeadTime;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        if (DataBaseUtil.isDifferent(averageCost, this.averageCost))
            this.averageCost = averageCost;
    }

    public Integer getAverageDailyUse() {
        return averageDailyUse;
    }

    public void setAverageDailyUse(Integer averageDailyUse) {
        if (DataBaseUtil.isDifferent(averageDailyUse, this.averageDailyUse))
            this.averageDailyUse = averageDailyUse;
    }

    public Integer getParentInventoryItemId() {
        return parentInventoryItemId;
    }

    public void setParentInventoryItemId(Integer parentInventoryItemId) {
        if (DataBaseUtil.isDifferent(parentInventoryItemId, this.parentInventoryItemId))
            this.parentInventoryItemId = parentInventoryItemId;
    }

    public Integer getParentRatio() {
        return parentRatio;
    }

    public void setParentRatio(Integer parentRatio) {
        if (DataBaseUtil.isDifferent(parentRatio, this.parentRatio))
            this.parentRatio = parentRatio;
    }

    public InventoryItem getParentInventoryItem() {
        return parentInventoryItem;
    }

    public void setParentInventoryItem(InventoryItem parentInventoryItem) {
        this.parentInventoryItem = parentInventoryItem;
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

    public void setClone() {
        try {
            original = (InventoryItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("category_id", categoryId, original.categoryId)
                 .setField("store_id", storeId, original.storeId)
                 .setField("quantity_min_level", quantityMinLevel, original.quantityMinLevel)
                 .setField("quantity_max_level", quantityMaxLevel, original.quantityMaxLevel)
                 .setField("quantity_to_reorder", quantityToReorder, original.quantityToReorder)
                 .setField("dispensed_units_id", dispensedUnitsId, original.dispensedUnitsId)
                 .setField("is_reorder_auto", isReorderAuto, original.isReorderAuto)
                 .setField("is_lot_maintained", isLotMaintained, original.isLotMaintained)
                 .setField("is_serial_maintained", isSerialMaintained, original.isSerialMaintained)
                 .setField("is_active", isActive, original.isActive)
                 .setField("is_bulk", isBulk, original.isBulk)
                 .setField("is_not_for_sale", isNotForSale, original.isNotForSale)
                 .setField("is_sub_assembly", isSubAssembly, original.isSubAssembly)
                 .setField("is_labor", isLabor, original.isLabor)
                 .setField("is_not_inventoried", isNotInventoried, original.isNotInventoried)
                 .setField("product_uri", productUri, original.productUri)
                 .setField("average_lead_time", averageLeadTime, original.averageLeadTime)
                 .setField("average_cost", averageCost, original.averageCost)
                 .setField("average_daily_use", averageDailyUse, original.averageDailyUse)
                 .setField("parent_inventory_item_id", parentInventoryItemId, original.parentInventoryItemId)
                 .setField("parent_ratio", parentRatio, original.parentRatio);

        return audit;

    }
}
