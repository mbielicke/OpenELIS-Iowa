
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

	/*@NamedQueries({	@NamedQuery(name = "getInventoryItem", query = "select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
			"					i.unitOfMeasure,i.isReorderAuto,i.isLotMaintained,i.isActive, i.averageLeadTime, i.averageCost, i.averageDailyUse) from InventoryItem i where i.id = :id")})
		*/	
			/*QUERY AFTER THE FIELDS GET PUT INTO THE DB
			 * 
			 * select new org.openelis.domain.InventoryItemDO(i.id,i.name,i.description,i.quantityMinLevel,i.quantityMaxLevel,i.quantityToReorder," +
						" i.unitOfMeasure,i.isReorderAuto,i.isLotMaintained,i.isActive, i.averageLeadTime, i.averageCost, i.averageDailyUse, i.store" +
						" i.purchasedUnit, i.dispensedUnit, i.isBulk, i.isNotForSale, i.isSubassembly, i.isComponent) from InventoryItem i where i.id = :id
			 * 
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

  @Column(name="quantity_min_level")
  private Integer quantityMinLevel;             

  @Column(name="quantity_max_level")
  private Integer quantityMaxLevel;             

  @Column(name="quantity_to_reorder")
  private Integer quantityToReorder;             

  @Column(name="unit_of_measure")
  private Integer unitOfMeasure;             

  @Column(name="is_reorder_auto")
  private String isReorderAuto;             

  @Column(name="is_lot_maintained")
  private String isLotMaintained;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="average_lead_time")
  private Integer averageLeadTime;             

  @Column(name="average_cost")
  private Double averageCost;             

  @Column(name="average_daily_use")
  private Integer averageDailyUse;             

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

  public Integer getUnitOfMeasure() {
    return unitOfMeasure;
  }
  public void setUnitOfMeasure(Integer unitOfMeasure) {
    if((unitOfMeasure == null && this.unitOfMeasure != null) || 
       (unitOfMeasure != null && !unitOfMeasure.equals(this.unitOfMeasure)))
      this.unitOfMeasure = unitOfMeasure;
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

      if((unitOfMeasure == null && original.unitOfMeasure != null) || 
         (unitOfMeasure != null && !unitOfMeasure.equals(original.unitOfMeasure))){
        Element elem = doc.createElement("unit_of_measure");
        elem.appendChild(doc.createTextNode(original.unitOfMeasure.toString().trim()));
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

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString().trim()));
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
  
}   
