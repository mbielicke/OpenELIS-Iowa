
package org.openelis.entity;

/**
  * StorageLocation Entity POJO for database 
  */

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({@NamedQuery(name = "getStorageLocation", query = "select new org.openelis.domain.StorageLocationDO(s.id,s.sortOrder,s.name, " +
" s.location,s.parentStorageLocation,s.storageUnit,s.isAvailable) from StorageLocation s where s.id = :id"),
@NamedQuery(name = "getStorageLocationChildren", query = "select new org.openelis.domain.StorageLocationDO(s.id,s.sortOrder,s.name, " +
" s.location,s.parentStorageLocation,s.storageUnit,s.isAvailable) from StorageLocation s where s.parentStorageLocation = :id"),
@NamedQuery(name = "getStorageLocationByParentId", query = "select s.id " +
							 " from StorageLocation s where s.parentStorageLocation = :id"),
@NamedQuery(name = "getStorageLocationByStorageUnitId", query = "select s.id " +
							 " from StorageLocation s where s.storageUnit = :id"),
@NamedQuery(name = "getStorageLocationAutoCompleteByName", query = "select s.id, s.name, s.location " +
							 " from StorageLocation s where UPPER(s.name) like :name order by s.name"),
@NamedQuery(name = "getStorageLocationAutoCompleteById", query = "select s.id, s.name, s.location " +
							 " from StorageLocation s where s.id = :id")})
							 
							 
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_storage_location", insertable = false, updatable = false)
  private StorageLocation parentStorageLocationName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "storage_unit", insertable = false, updatable = false)
  private StorageUnit storageUnitName;
  
  @Transient
  private StorageLocation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    if((location == null && this.location != null) || 
       (location != null && !location.equals(this.location)))
      this.location = location;
  }

  public Integer getParentStorageLocation() {
    return parentStorageLocation;
  }
  public void setParentStorageLocation(Integer parentStorageLocation) {
    if((parentStorageLocation == null && this.parentStorageLocation != null) || 
       (parentStorageLocation != null && !parentStorageLocation.equals(this.parentStorageLocation)))
      this.parentStorageLocation = parentStorageLocation;
  }

  public Integer getStorageUnit() {
    return storageUnit;
  }
  public void setStorageUnit(Integer storageUnit) {
    if((storageUnit == null && this.storageUnit != null) || 
       (storageUnit != null && !storageUnit.equals(this.storageUnit)))
      this.storageUnit = storageUnit;
  }

  public String getIsAvailable() {
    return isAvailable;
  }
  public void setIsAvailable(String isAvailable) {
    if((isAvailable == null && this.isAvailable != null) || 
       (isAvailable != null && !isAvailable.equals(this.isAvailable)))
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
          elem.appendChild(doc.createTextNode(original.id.toString().trim()));
          root.appendChild(elem);
        }      

        if((sortOrder == null && original.sortOrder != null) || 
           (sortOrder != null && !sortOrder.equals(original.sortOrder))){
          Element elem = doc.createElement("sort_order");
          elem.appendChild(doc.createTextNode(original.sortOrder.toString().trim()));
          root.appendChild(elem);
        }      

        if((name == null && original.name != null) || 
           (name != null && !name.equals(original.name))){
          Element elem = doc.createElement("name");
          elem.appendChild(doc.createTextNode(original.name.toString().trim()));
          root.appendChild(elem);
        }      

        if((location == null && original.location != null) || 
           (location != null && !location.equals(original.location))){
          Element elem = doc.createElement("location");
          elem.appendChild(doc.createTextNode(original.location.toString().trim()));
          root.appendChild(elem);
        }      

        if((parentStorageLocation == null && original.parentStorageLocation != null) || 
           (parentStorageLocation != null && !parentStorageLocation.equals(original.parentStorageLocation))){
          Element elem = doc.createElement("parent_storage_location");
          elem.appendChild(doc.createTextNode(original.parentStorageLocation.toString().trim()));
          root.appendChild(elem);
        }      

        if((storageUnit == null && original.storageUnit != null) || 
           (storageUnit != null && !storageUnit.equals(original.storageUnit))){
          Element elem = doc.createElement("storage_unit");
          elem.appendChild(doc.createTextNode(original.storageUnit.toString().trim()));
          root.appendChild(elem);
        }      

        if((isAvailable == null && original.isAvailable != null) || 
           (isAvailable != null && !isAvailable.equals(original.isAvailable))){
          Element elem = doc.createElement("is_available");
          elem.appendChild(doc.createTextNode(original.isAvailable.toString().trim()));
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
public StorageLocation getParentStorageLocationName() {
	return parentStorageLocationName;
}
public void setParentStorageLocationName(
		StorageLocation parentStorageLocationName) {
	this.parentStorageLocationName = parentStorageLocationName;
}
public StorageUnit getStorageUnitName() {
	return storageUnitName;
}
public void setStorageUnitName(StorageUnit storageUnitName) {
	this.storageUnitName = storageUnitName;
}
  
}   
