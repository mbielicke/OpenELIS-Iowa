
package org.openelis.entity;

/**
  * ShippingItem Entity POJO for database 
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
@Table(name="shipping_item")
@EntityListeners({AuditUtil.class})
public class ShippingItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="shipping_id")
  private Integer shippingId;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="reference_id")
  private Integer referenceId;             


  @Transient
  private ShippingItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getShippingId() {
    return shippingId;
  }
  public void setShippingId(Integer shippingId) {
    if((shippingId == null && this.shippingId != null) || 
       (shippingId != null && !shippingId.equals(this.shippingId)))
      this.shippingId = shippingId;
  }

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    if((referenceId == null && this.referenceId != null) || 
       (referenceId != null && !referenceId.equals(this.referenceId)))
      this.referenceId = referenceId;
  }

  
  public void setClone() {
    try {
      original = (ShippingItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(shippingId,original.shippingId,doc,"shipping_id");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      AuditUtil.getChangeXML(referenceId,original.referenceId,doc,"reference_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "shipping_item";
  }
  
}   
