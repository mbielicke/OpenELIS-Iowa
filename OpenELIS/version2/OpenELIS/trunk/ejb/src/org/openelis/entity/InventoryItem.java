
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

	@NamedQueries({	@NamedQuery(name = "InventoryItem.InventoryItem", query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description,i.category,i.store,i.quantityMinLevel, " +
	               	                       " i.quantityMaxLevel,i.quantityToReorder, i.purchasedUnits,i.dispensedUnits,i.isReorderAuto,i.isLotMaintained,i.isSerialMaintained,i.isActive, " +
                                           " i.isBulk,i.isNotForSale,i.isSubAssembly,i.isLabor,i.isNoInventory,i.productUri,i.averageLeadTime, i.averageCost, i.averageDailyUse) " +
                                           " from InventoryItem i where i.id = :id"),
                    @NamedQuery(name = "InventoryItem.AutocompleteByNameStoreCurrentName", query = "select new org.openelis.domain.IdNameDO(i.id, i.name) " +
                                           "  from InventoryItem i where i.name like :name and i.store = :store and i.name != :currentName and i.isActive = 'Y' order by i.name"),
                    @NamedQuery(name = "InventoryItem.AutocompleteByName", query = "select new org.openelis.domain.IdNameDO(i.id, i.name) " +
                                           "  from InventoryItem i where i.name like :name and i.isActive = 'Y' order by i.name"),
                    @NamedQuery(name = "InventoryItem.AutocompleteItemStoreLocByName", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, d.entry, il.id, childLoc.name, " +
                                           " childLoc.location, parentLoc.name, childLoc.storageUnit.description, il.quantityOnhand) " +
                                           "  from InventoryItem i left join i.inventoryLocation il left join il.storageLocation childLoc " +
                                           " left join childLoc.parentStorageLocation parentLoc, Dictionary d where i.store = d.id and i.name like :name and i.isActive = 'Y' and il.quantityOnhand > 0 order by i.name"),
                    @NamedQuery(name = "InventoryItem.AutocompleteItemStoreByName", query = "select distinct new org.openelis.domain.InventoryItemAutoDO(i.id, i.name, d.entry) " +
                                           "  from InventoryItem i, Dictionary d where i.store = d.id and i.name like :name and i.isActive = 'Y' order by i.name"),
                    @NamedQuery(name = "InventoryItem.DescriptionById", query = "select i.description " +
                                           "  from InventoryItem i where i.id = :id"),
                    @NamedQuery(name = "InventoryItem.Notes", query = "select new org.openelis.domain.NoteDO(n.id, n.systemUser, n.text, n.timestamp, n.subject) "
                                         + "  from Note n where n.referenceTable = (select id from ReferenceTable where name='inventory_item') and n.referenceId = :id ORDER BY n.timestamp DESC"),
                    @NamedQuery(name = "InventoryItem.UpdateNameStoreCompare", query = "select i.id from InventoryItem i where i.name = :name and i.store = :store AND i.id != :id"),
                    @NamedQuery(name = "InventoryItem.AddNameStoreCompare", query = "select i.id from InventoryItem i where i.name = :name AND i.store = :store")})
	
			   
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
  
  @Column(name="category")
  private Integer category;
  
  @Column(name="store")
  private Integer store;

  @Column(name="quantity_min_level")
  private Integer quantityMinLevel;             

  @Column(name="quantity_max_level")
  private Integer quantityMaxLevel;             

  @Column(name="quantity_to_reorder")
  private Integer quantityToReorder;             

  @Column(name="purchased_units")
  private Integer purchasedUnits;
  
  @Column(name="dispensed_units")
  private Integer dispensedUnits;
  
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
  
  @Column(name=" is_sub_assembly")
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

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item")
  private Collection<InventoryComponent> inventoryComponent;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item")
  private Collection<InventoryLocation> inventoryLocation;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private Collection<Note> note;

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
  
  public Integer getCategory() {
    return category;
  }
  
    public void setCategory(Integer category) {
        if((category == null && this.category != null) || 
           (category != null && !category.equals(this.category)))
         this.category = category;
    }
    
    public Integer getDispensedUnits() {
        return dispensedUnits;
    }
    
    public void setDispensedUnits(Integer dispensedUnits) {
        if((dispensedUnits == null && this.dispensedUnits != null) || 
           (dispensedUnits != null && !dispensedUnits.equals(this.dispensedUnits)))
          this.dispensedUnits = dispensedUnits;
    }
    public String getIsBulk() {
        return isBulk;
    }
    public void setIsBulk(String isBulk) {
        if((isBulk == null && this.isBulk != null) || 
           (isBulk != null && !isBulk.equals(this.isBulk)))
          this.isBulk = isBulk;
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
    public String getIsNotForSale() {
        return isNotForSale;
    }
    public void setIsNotForSale(String isNotForSale) {
        if((isNotForSale == null && this.isNotForSale != null) || 
           (isNotForSale != null && !isNotForSale.equals(this.isNotForSale)))
          this.isNotForSale = isNotForSale;
    }
    public String getIsSerialMaintained() {
        return isSerialMaintained;
    }
    public void setIsSerialMaintained(String isSerialMaintained) {
        if((isSerialMaintained == null && this.isSerialMaintained != null) || 
           (isSerialMaintained != null && !isSerialMaintained.equals(this.isSerialMaintained)))
          this.isSerialMaintained = isSerialMaintained;
    }
    public String getIsSubAssembly() {
        return isSubAssembly;
    }
    public void setIsSubAssembly(String isSubAssembly) {
        if((isSubAssembly == null && this.isSubAssembly != null) || 
           (isSubAssembly != null && !isSubAssembly.equals(this.isSubAssembly)))
          this.isSubAssembly = isSubAssembly;
    }
    public String getProductUri() {
        return productUri;
    }
    public void setProductUri(String productUri) {
        if((productUri == null && this.productUri != null) || 
           (productUri != null && !productUri.equals(this.productUri)))
          this.productUri = productUri;
    }
    public Integer getPurchasedUnits() {
        return purchasedUnits;
    }
    public void setPurchasedUnits(Integer purchasedUnits) {
        if((purchasedUnits == null && this.purchasedUnits != null) || 
           (purchasedUnits != null && !purchasedUnits.equals(this.purchasedUnits)))
          this.purchasedUnits = purchasedUnits;
    }
    public Integer getStore() {
        return store;
    }
    public void setStore(Integer store) {
        if((store == null && this.store != null) || 
           (store != null && !store.equals(this.store)))
          this.store = store;
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

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
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

  
  public void setClone() {
    try {
      original = (InventoryItem)this.clone();
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

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString().trim()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString().trim()));
        root.appendChild(elem);
      }
      
      if((category == null && original.category != null) || 
         (category != null && !category.equals(original.category))){
        Element elem = doc.createElement("category");
        elem.appendChild(doc.createTextNode(original.category.toString().trim()));
        root.appendChild(elem);
      }
      
      if((store == null && original.store != null) || 
         (store != null && !store.equals(original.store))){
        Element elem = doc.createElement("store");
        elem.appendChild(doc.createTextNode(original.store.toString().trim()));
        root.appendChild(elem);
     }

      if((quantityMinLevel == null && original.quantityMinLevel != null) || 
         (quantityMinLevel != null && !quantityMinLevel.equals(original.quantityMinLevel))){
        Element elem = doc.createElement("quantity_min_level");
        elem.appendChild(doc.createTextNode(original.quantityMinLevel.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantityMaxLevel == null && original.quantityMaxLevel != null) || 
         (quantityMaxLevel != null && !quantityMaxLevel.equals(original.quantityMaxLevel))){
        Element elem = doc.createElement("quantity_max_level");
        elem.appendChild(doc.createTextNode(original.quantityMaxLevel.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantityToReorder == null && original.quantityToReorder != null) || 
         (quantityToReorder != null && !quantityToReorder.equals(original.quantityToReorder))){
        Element elem = doc.createElement("quantity_to_reorder");
        elem.appendChild(doc.createTextNode(original.quantityToReorder.toString().trim()));
        root.appendChild(elem);
      }
      
      if((purchasedUnits == null && original.purchasedUnits != null) || 
         (purchasedUnits != null && !purchasedUnits.equals(original.purchasedUnits))){
        Element elem = doc.createElement("purchased_units");
        elem.appendChild(doc.createTextNode(original.purchasedUnits.toString().trim()));
        root.appendChild(elem);
      }
      
      if((dispensedUnits == null && original.dispensedUnits != null) || 
         (dispensedUnits != null && !dispensedUnits.equals(original.dispensedUnits))){
        Element elem = doc.createElement("dispensed_units");
        elem.appendChild(doc.createTextNode(original.dispensedUnits.toString().trim()));
        root.appendChild(elem);
      }

      if((isReorderAuto == null && original.isReorderAuto != null) || 
         (isReorderAuto != null && !isReorderAuto.equals(original.isReorderAuto))){
        Element elem = doc.createElement("is_reorder_auto");
        elem.appendChild(doc.createTextNode(original.isReorderAuto.toString().trim()));
        root.appendChild(elem);
      }      

      if((isLotMaintained == null && original.isLotMaintained != null) || 
         (isLotMaintained != null && !isLotMaintained.equals(original.isLotMaintained))){
        Element elem = doc.createElement("is_lot_maintained");
        elem.appendChild(doc.createTextNode(original.isLotMaintained.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((isSerialMaintained == null && original.isSerialMaintained != null) || 
         (isSerialMaintained != null && !isSerialMaintained.equals(original.isSerialMaintained))){
        Element elem = doc.createElement("is_serial_maintained");
        elem.appendChild(doc.createTextNode(original.isSerialMaintained.toString().trim()));
        root.appendChild(elem);
      } 
    
      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString().trim()));
        root.appendChild(elem);
      }  
      
      if((isBulk == null && original.isBulk != null) || 
         (isBulk != null && !isBulk.equals(original.isBulk))){
        Element elem = doc.createElement("is_bulk");
        elem.appendChild(doc.createTextNode(original.isBulk.toString().trim()));
        root.appendChild(elem);
      }  
      
      if((isNotForSale == null && original.isNotForSale != null) || 
         (isNotForSale != null && !isNotForSale.equals(original.isNotForSale))){
        Element elem = doc.createElement("is_not_for_sale");
        elem.appendChild(doc.createTextNode(original.isNotForSale.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((isSubAssembly == null && original.isSubAssembly != null) || 
         (isSubAssembly != null && !isSubAssembly.equals(original.isSubAssembly))){
        Element elem = doc.createElement("is_sub_assembly");
        elem.appendChild(doc.createTextNode(original.isSubAssembly.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((isLabor == null && original.isLabor != null) || 
         (isLabor != null && !isLabor.equals(original.isLabor))){
        Element elem = doc.createElement("is_labor");
        elem.appendChild(doc.createTextNode(original.isLabor.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((isNoInventory == null && original.isNoInventory != null) || 
         (isNoInventory != null && !isNoInventory.equals(original.isNoInventory))){
        Element elem = doc.createElement("is_no_inventory");
        elem.appendChild(doc.createTextNode(original.isNoInventory.toString().trim()));
        root.appendChild(elem);
      }

      if((productUri == null && original.productUri != null) || 
         (productUri != null && !productUri.equals(original.productUri))){
        Element elem = doc.createElement("product_uri");
        elem.appendChild(doc.createTextNode(original.productUri.toString().trim()));
        root.appendChild(elem);
      }

      if((averageLeadTime == null && original.averageLeadTime != null) || 
         (averageLeadTime != null && !averageLeadTime.equals(original.averageLeadTime))){
        Element elem = doc.createElement("average_lead_time");
        elem.appendChild(doc.createTextNode(original.averageLeadTime.toString().trim()));
        root.appendChild(elem);
      }      

      if((averageCost == null && original.averageCost != null) || 
         (averageCost != null && !averageCost.equals(original.averageCost))){
        Element elem = doc.createElement("average_cost");
        elem.appendChild(doc.createTextNode(original.averageCost.toString().trim()));
        root.appendChild(elem);
      }      

      if((averageDailyUse == null && original.averageDailyUse != null) || 
         (averageDailyUse != null && !averageDailyUse.equals(original.averageDailyUse))){
        Element elem = doc.createElement("average_daily_use");
        elem.appendChild(doc.createTextNode(original.averageDailyUse.toString().trim()));
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
  
}   
