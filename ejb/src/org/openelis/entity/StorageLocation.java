
package org.openelis.entity;

/**
  * StorageLocation Entity POJO for database 
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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="storage_location")
@EntityListeners({AuditUtil.class})
public class StorageLocation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="name")
  private String name;             

  @Column(name="location")
  private String location;             

  @Column(name="parent_storage_location")
  private Integer parentStorageLocation;             

  @Column(name="storage_unit")
  private Integer storageUnit;             

  @Column(name="is_available")
  private String isAvailable;             


  @Transient
  private StorageLocation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getParentStorageLocation() {
    return parentStorageLocation;
  }
  public void setParentStorageLocation(Integer parentStorageLocation) {
    this.parentStorageLocation = parentStorageLocation;
  }

  public Integer getStorageUnit() {
    return storageUnit;
  }
  public void setStorageUnit(Integer storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getIsAvailable() {
    return isAvailable;
  }
  public void setIsAvailable(String isAvailable) {
    this.isAvailable = isAvailable;
  }

  
  public void setClone() {
    try {
      original = (StorageLocation)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString()));
        root.appendChild(elem);
      }      

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
        root.appendChild(elem);
      }      

      if((location == null && original.location != null) || 
         (location != null && !location.equals(original.location))){
        Element elem = doc.createElement("location");
        elem.appendChild(doc.createTextNode(original.location.toString()));
        root.appendChild(elem);
      }      

      if((parentStorageLocation == null && original.parentStorageLocation != null) || 
         (parentStorageLocation != null && !parentStorageLocation.equals(original.parentStorageLocation))){
        Element elem = doc.createElement("parent_storage_location");
        elem.appendChild(doc.createTextNode(original.parentStorageLocation.toString()));
        root.appendChild(elem);
      }      

      if((storageUnit == null && original.storageUnit != null) || 
         (storageUnit != null && !storageUnit.equals(original.storageUnit))){
        Element elem = doc.createElement("storage_unit");
        elem.appendChild(doc.createTextNode(original.storageUnit.toString()));
        root.appendChild(elem);
      }      

      if((isAvailable == null && original.isAvailable != null) || 
         (isAvailable != null && !isAvailable.equals(original.isAvailable))){
        Element elem = doc.createElement("is_available");
        elem.appendChild(doc.createTextNode(original.isAvailable.toString()));
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
    return "storage_location";
  }
  
}   
