
package org.openelis.entity;

/**
  * OrderContainer Entity POJO for database 
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
@Table(name="order_container")
@EntityListeners({AuditUtil.class})
public class OrderContainer implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="order_id")
  private Integer orderId;             

  @Column(name="container_id")
  private Integer containerId;             

  @Column(name="number_of_containers")
  private Integer numberOfContainers;             

  @Column(name="type_of_sample_id")
  private Integer typeOfSampleId;             


  @Transient
  private OrderContainer original;

  
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

  public Integer getContainerId() {
    return containerId;
  }
  public void setContainerId(Integer containerId) {
    if((containerId == null && this.containerId != null) || 
       (containerId != null && !containerId.equals(this.containerId)))
      this.containerId = containerId;
  }

  public Integer getNumberOfContainers() {
    return numberOfContainers;
  }
  public void setNumberOfContainers(Integer numberOfContainers) {
    if((numberOfContainers == null && this.numberOfContainers != null) || 
       (numberOfContainers != null && !numberOfContainers.equals(this.numberOfContainers)))
      this.numberOfContainers = numberOfContainers;
  }

  public Integer getTypeOfSampleId() {
    return typeOfSampleId;
  }
  public void setTypeOfSampleId(Integer typeOfSampleId) {
    if((typeOfSampleId == null && this.typeOfSampleId != null) || 
       (typeOfSampleId != null && !typeOfSampleId.equals(this.typeOfSampleId)))
      this.typeOfSampleId = typeOfSampleId;
  }

  
  public void setClone() {
    try {
      original = (OrderContainer)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(orderId,original.orderId,doc,"order_id");

      AuditUtil.getChangeXML(containerId,original.containerId,doc,"container_id");

      AuditUtil.getChangeXML(numberOfContainers,original.numberOfContainers,doc,"number_of_containers");

      AuditUtil.getChangeXML(typeOfSampleId,original.typeOfSampleId,doc,"type_of_sample_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "order_container";
  }
  
}   
