
package org.openelis.entity;

/**
  * Storage Entity POJO for database 
  */

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

import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

	@NamedQueries({@NamedQuery(name = "getStorageByStorageLocationId", query = "select s.id from Storage s where s.storageLocation = :id")})

@Entity
@Table(name="storage")
@EntityListeners({AuditUtil.class})
public class Storage implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="storage_location")
  private Integer storageLocation;             

  @Column(name="checkin")
  private Date checkin;             

  @Column(name="checkout")
  private Date checkout;             


  @Transient
  private Storage original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    if((referenceId == null && this.referenceId != null) || 
       (referenceId != null && !referenceId.equals(this.referenceId)))
      this.referenceId = referenceId;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    if((referenceTable == null && this.referenceTable != null) || 
       (referenceTable != null && !referenceTable.equals(this.referenceTable)))
      this.referenceTable = referenceTable;
  }

  public Integer getStorageLocation() {
    return storageLocation;
  }
  public void setStorageLocation(Integer storageLocation) {
    if((storageLocation == null && this.storageLocation != null) || 
       (storageLocation != null && !storageLocation.equals(this.storageLocation)))
      this.storageLocation = storageLocation;
  }

  public Datetime getCheckin() {
    if(checkin == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.MINUTE,checkin);
  }
  public void setCheckin (Datetime checkin){
    if((checkin == null && this.checkin != null) || 
       (checkin != null && !checkin.equals(this.checkin)))
      this.checkin = checkin.getDate();
  }

  public Datetime getCheckout() {
    if(checkout == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.MINUTE,checkout);
  }
  public void setCheckout (Datetime checkout){
    if((checkout == null && this.checkout != null) || 
       (checkout != null && !checkout.equals(this.checkout)))
      this.checkout = checkout.getDate();
  }

  
  public void setClone() {
    try {
      original = (Storage)this.clone();
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

        if((referenceId == null && original.referenceId != null) || 
           (referenceId != null && !referenceId.equals(original.referenceId))){
          Element elem = doc.createElement("reference_id");
          elem.appendChild(doc.createTextNode(original.referenceId.toString().trim()));
          root.appendChild(elem);
        }      

        if((referenceTable == null && original.referenceTable != null) || 
           (referenceTable != null && !referenceTable.equals(original.referenceTable))){
          Element elem = doc.createElement("reference_table");
          elem.appendChild(doc.createTextNode(original.referenceTable.toString().trim()));
          root.appendChild(elem);
        }      

        if((storageLocation == null && original.storageLocation != null) || 
           (storageLocation != null && !storageLocation.equals(original.storageLocation))){
          Element elem = doc.createElement("storage_location");
          elem.appendChild(doc.createTextNode(original.storageLocation.toString().trim()));
          root.appendChild(elem);
        }      

        if((checkin == null && original.checkin != null) || 
           (checkin != null && !checkin.equals(original.checkin))){
          Element elem = doc.createElement("checkin");
          elem.appendChild(doc.createTextNode(original.checkin.toString().trim()));
          root.appendChild(elem);
        }      

        if((checkout == null && original.checkout != null) || 
           (checkout != null && !checkout.equals(original.checkout))){
          Element elem = doc.createElement("checkout");
          elem.appendChild(doc.createTextNode(original.checkout.toString().trim()));
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
    return "storage";
  }
  
}   
