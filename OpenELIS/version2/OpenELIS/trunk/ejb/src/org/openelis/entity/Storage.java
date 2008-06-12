
package org.openelis.entity;

/**
  * Storage Entity POJO for database 
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

@NamedQueries({@NamedQuery(name = "Storage.IdByStorageLocation", query = "select s.id from Storage s where s.storageLocationId = :id")})

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

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="storage_location_id")
  private Integer storageLocationId;             

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

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  public Integer getStorageLocationId() {
    return storageLocationId;
  }
  public void setStorageLocationId(Integer storageLocationId) {
    if((storageLocationId == null && this.storageLocationId != null) || 
       (storageLocationId != null && !storageLocationId.equals(this.storageLocationId)))
      this.storageLocationId = storageLocationId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(referenceId,original.referenceId,doc,"reference_id");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      AuditUtil.getChangeXML(storageLocationId,original.storageLocationId,doc,"storage_location_id");

      AuditUtil.getChangeXML(checkin,original.checkin,doc,"checkin");

      AuditUtil.getChangeXML(checkout,original.checkout,doc,"checkout");

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
