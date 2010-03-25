
package org.openelis.entity;

/**
  * OrderTest Entity POJO for database 
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
@Table(name="order_test")
@EntityListeners({AuditUtil.class})
public class OrderTest implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="order_id")
  private Integer orderId;             

  @Column(name="sequence")
  private Integer sequence;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             


  @Transient
  private OrderTest original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getOrderId() {
    return orderId;
  }
  public void setOrderId(Integer orderId) {
    if((orderId == null && this.orderId != null) || 
       (orderId != null && !orderId.equals(this.orderId)))
      this.orderId = orderId;
  }

  public Integer getSequence() {
    return sequence;
  }
  public void setSequence(Integer sequence) {
    if((sequence == null && this.sequence != null) || 
       (sequence != null && !sequence.equals(this.sequence)))
      this.sequence = sequence;
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

  
  public void setClone() {
    try {
      original = (OrderTest)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(orderId,original.orderId,doc,"order_id");

      AuditUtil.getChangeXML(sequence,original.sequence,doc,"sequence");

      AuditUtil.getChangeXML(referenceId,original.referenceId,doc,"reference_id");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "order_test";
  }
  
}   
