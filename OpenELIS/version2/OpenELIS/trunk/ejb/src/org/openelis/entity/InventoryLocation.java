
package org.openelis.entity;

/**
  * InventoryLocation Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

	/*@NamedQueries({	@NamedQuery(name = "getInventoryLocations", query = "select new org.openelis.domain.InventoryLocationDO() from InventoryLocation i where i.id = :id"),
					@NamedQuery(name = "getInventoryLocationByStorageLocationId", query = "select i.id from InventoryLocation i where i.storageLocation = :id")})
*/
@Entity
@Table(name="inventory_location")
@EntityListeners({AuditUtil.class})
public class InventoryLocation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="inventory_item")
  private Integer inventoryItem;             

  @Column(name="lot_number")
  private String lotNumber;             

  @Column(name="storage_location")
  private Integer storageLocation;             

  @Column(name="quantity_onhand")
  private Integer quantityOnhand;             

  @Column(name="expiration_date")
  private Date expirationDate;             


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

  public Integer getInventoryItem() {
    return inventoryItem;
  }
  public void setInventoryItem(Integer inventoryItem) {
    if((inventoryItem == null && this.inventoryItem != null) || 
       (inventoryItem != null && !inventoryItem.equals(this.inventoryItem)))
      this.inventoryItem = inventoryItem;
  }

  public String getLotNumber() {
    return lotNumber;
  }
  public void setLotNumber(String lotNumber) {
    if((lotNumber == null && this.lotNumber != null) || 
       (lotNumber != null && !lotNumber.equals(this.lotNumber)))
      this.lotNumber = lotNumber;
  }

  public Integer getStorageLocation() {
    return storageLocation;
  }
  public void setStorageLocation(Integer storageLocation) {
    if((storageLocation == null && this.storageLocation != null) || 
       (storageLocation != null && !storageLocation.equals(this.storageLocation)))
      this.storageLocation = storageLocation;
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
  public void setExpirationDate (Datetime expiration_date){
    if((expirationDate == null && this.expirationDate != null) || 
       (expirationDate != null && !expirationDate.equals(this.expirationDate)))
      this.expirationDate = expiration_date.getDate();
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((inventoryItem == null && original.inventoryItem != null) || 
         (inventoryItem != null && !inventoryItem.equals(original.inventoryItem))){
        Element elem = doc.createElement("inventory_item");
        elem.appendChild(doc.createTextNode(original.inventoryItem.toString().trim()));
        root.appendChild(elem);
      }      

      if((lotNumber == null && original.lotNumber != null) || 
         (lotNumber != null && !lotNumber.equals(original.lotNumber))){
        Element elem = doc.createElement("lot_number");
        elem.appendChild(doc.createTextNode(original.lotNumber.toString().trim()));
        root.appendChild(elem);
      }      

      if((storageLocation == null && original.storageLocation != null) || 
         (storageLocation != null && !storageLocation.equals(original.storageLocation))){
        Element elem = doc.createElement("storage_location");
        elem.appendChild(doc.createTextNode(original.storageLocation.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantityOnhand == null && original.quantityOnhand != null) || 
         (quantityOnhand != null && !quantityOnhand.equals(original.quantityOnhand))){
        Element elem = doc.createElement("quantity_onhand");
        elem.appendChild(doc.createTextNode(original.quantityOnhand.toString().trim()));
        root.appendChild(elem);
      }      

      if((expirationDate == null && original.expirationDate != null) || 
         (expirationDate != null && !expirationDate.equals(original.expirationDate))){
        Element elem = doc.createElement("expiration_date");
        elem.appendChild(doc.createTextNode(original.expirationDate.toString().trim()));
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
    return "inventory_location";
  }
  
}   
