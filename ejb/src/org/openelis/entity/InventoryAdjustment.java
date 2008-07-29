
package org.openelis.entity;

/**
  * InventoryAdjustment Entity POJO for database 
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
import org.openelis.utils.Auditable;

@Entity
@Table(name="inventory_adjustment")
@EntityListeners({AuditUtil.class})
public class InventoryAdjustment implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="description")
  private String description;             

  @Column(name="system_user_id")
  private Integer systemUserId;             

  @Column(name="adjustment_date")
  private Date adjustmentDate;             


  @Transient
  private InventoryAdjustment original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getSystemUserId() {
    return systemUserId;
  }
  public void setSystemUserId(Integer systemUserId) {
    if((systemUserId == null && this.systemUserId != null) || 
       (systemUserId != null && !systemUserId.equals(this.systemUserId)))
      this.systemUserId = systemUserId;
  }

  public Datetime getAdjustmentDate() {
    if(adjustmentDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.MINUTE,adjustmentDate);
  }
  public void setAdjustmentDate (Datetime adjustment_date){
    if((adjustmentDate == null && this.adjustmentDate != null) || 
       (adjustmentDate != null && !adjustmentDate.equals(this.adjustmentDate)))
      this.adjustmentDate = adjustment_date.getDate();
  }

  
  public void setClone() {
    try {
      original = (InventoryAdjustment)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(systemUserId,original.systemUserId,doc,"system_user_id");

      AuditUtil.getChangeXML(adjustmentDate,original.adjustmentDate,doc,"adjustment_date");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "inventory_adjustment";
  }
  
}   
