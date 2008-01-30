
package org.openelis.entity;

/**
  * Order Entity POJO for database 
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
@Table(name="order")
@EntityListeners({AuditUtil.class})
public class Order implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="organization")
  private Integer organization;             

  @Column(name="ordered_date")
  private Date orderedDate;             

  @Column(name="neededby_date")
  private Date neededbyDate;             

  @Column(name="requested_by")
  private String requestedBy;             

  @Column(name="cost_center")
  private String costCenter;             

  @Column(name="processed_by")
  private Integer processedBy;             

  @Column(name="shipping_type")
  private Integer shippingType;             

  @Column(name="shipping_carrier")
  private Integer shippingCarrier;             

  @Column(name="shipping_cost")
  private Double shippingCost;             

  @Column(name="delivered_date")
  private Date deliveredDate;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="external_order_number")
  private String externalOrderNumber;             

  @Column(name="is_filled")
  private String isFilled;             


  @Transient
  private Order original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getOrganization() {
    return organization;
  }
  public void setOrganization(Integer organization) {
    if((organization == null && this.organization != null) || 
       (organization != null && !organization.equals(this.organization)))
      this.organization = organization;
  }

  public Datetime getOrderedDate() {
    if(orderedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,orderedDate);
  }
  public void setOrderedDate (Datetime ordered_date){
    if((orderedDate == null && this.orderedDate != null) || 
       (orderedDate != null && !orderedDate.equals(this.orderedDate)))
      this.orderedDate = ordered_date.getDate();
  }

  public Datetime getNeededbyDate() {
    if(neededbyDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,neededbyDate);
  }
  public void setNeededbyDate (Datetime neededby_date){
    if((neededbyDate == null && this.neededbyDate != null) || 
       (neededbyDate != null && !neededbyDate.equals(this.neededbyDate)))
      this.neededbyDate = neededby_date.getDate();
  }

  public String getRequestedBy() {
    return requestedBy;
  }
  public void setRequestedBy(String requestedBy) {
    if((requestedBy == null && this.requestedBy != null) || 
       (requestedBy != null && !requestedBy.equals(this.requestedBy)))
      this.requestedBy = requestedBy;
  }

  public String getCostCenter() {
    return costCenter;
  }
  public void setCostCenter(String costCenter) {
    if((costCenter == null && this.costCenter != null) || 
       (costCenter != null && !costCenter.equals(this.costCenter)))
      this.costCenter = costCenter;
  }

  public Integer getProcessedBy() {
    return processedBy;
  }
  public void setProcessedBy(Integer processedBy) {
    if((processedBy == null && this.processedBy != null) || 
       (processedBy != null && !processedBy.equals(this.processedBy)))
      this.processedBy = processedBy;
  }

  public Integer getShippingType() {
    return shippingType;
  }
  public void setShippingType(Integer shippingType) {
    if((shippingType == null && this.shippingType != null) || 
       (shippingType != null && !shippingType.equals(this.shippingType)))
      this.shippingType = shippingType;
  }

  public Integer getShippingCarrier() {
    return shippingCarrier;
  }
  public void setShippingCarrier(Integer shippingCarrier) {
    if((shippingCarrier == null && this.shippingCarrier != null) || 
       (shippingCarrier != null && !shippingCarrier.equals(this.shippingCarrier)))
      this.shippingCarrier = shippingCarrier;
  }

  public Double getShippingCost() {
    return shippingCost;
  }
  public void setShippingCost(Double shippingCost) {
    if((shippingCost == null && this.shippingCost != null) || 
       (shippingCost != null && !shippingCost.equals(this.shippingCost)))
      this.shippingCost = shippingCost;
  }

  public Datetime getDeliveredDate() {
    if(deliveredDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,deliveredDate);
  }
  public void setDeliveredDate (Datetime delivered_date){
    if((deliveredDate == null && this.deliveredDate != null) || 
       (deliveredDate != null && !deliveredDate.equals(this.deliveredDate)))
      this.deliveredDate = delivered_date.getDate();
  }

  public String getIsExternal() {
    return isExternal;
  }
  public void setIsExternal(String isExternal) {
    if((isExternal == null && this.isExternal != null) || 
       (isExternal != null && !isExternal.equals(this.isExternal)))
      this.isExternal = isExternal;
  }

  public String getExternalOrderNumber() {
    return externalOrderNumber;
  }
  public void setExternalOrderNumber(String externalOrderNumber) {
    if((externalOrderNumber == null && this.externalOrderNumber != null) || 
       (externalOrderNumber != null && !externalOrderNumber.equals(this.externalOrderNumber)))
      this.externalOrderNumber = externalOrderNumber;
  }

  public String getIsFilled() {
    return isFilled;
  }
  public void setIsFilled(String isFilled) {
    if((isFilled == null && this.isFilled != null) || 
       (isFilled != null && !isFilled.equals(this.isFilled)))
      this.isFilled = isFilled;
  }

  
  public void setClone() {
    try {
      original = (Order)this.clone();
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

      if((organization == null && original.organization != null) || 
         (organization != null && !organization.equals(original.organization))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organization.toString().trim()));
        root.appendChild(elem);
      }      

      if((orderedDate == null && original.orderedDate != null) || 
         (orderedDate != null && !orderedDate.equals(original.orderedDate))){
        Element elem = doc.createElement("ordered_date");
        elem.appendChild(doc.createTextNode(original.orderedDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((neededbyDate == null && original.neededbyDate != null) || 
         (neededbyDate != null && !neededbyDate.equals(original.neededbyDate))){
        Element elem = doc.createElement("neededby_date");
        elem.appendChild(doc.createTextNode(original.neededbyDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((requestedBy == null && original.requestedBy != null) || 
         (requestedBy != null && !requestedBy.equals(original.requestedBy))){
        Element elem = doc.createElement("requested_by");
        elem.appendChild(doc.createTextNode(original.requestedBy.toString().trim()));
        root.appendChild(elem);
      }      

      if((costCenter == null && original.costCenter != null) || 
         (costCenter != null && !costCenter.equals(original.costCenter))){
        Element elem = doc.createElement("cost_center");
        elem.appendChild(doc.createTextNode(original.costCenter.toString().trim()));
        root.appendChild(elem);
      }      

      if((processedBy == null && original.processedBy != null) || 
         (processedBy != null && !processedBy.equals(original.processedBy))){
        Element elem = doc.createElement("processed_by");
        elem.appendChild(doc.createTextNode(original.processedBy.toString().trim()));
        root.appendChild(elem);
      }      

      if((shippingType == null && original.shippingType != null) || 
         (shippingType != null && !shippingType.equals(original.shippingType))){
        Element elem = doc.createElement("shipping_type");
        elem.appendChild(doc.createTextNode(original.shippingType.toString().trim()));
        root.appendChild(elem);
      }      

      if((shippingCarrier == null && original.shippingCarrier != null) || 
         (shippingCarrier != null && !shippingCarrier.equals(original.shippingCarrier))){
        Element elem = doc.createElement("shipping_carrier");
        elem.appendChild(doc.createTextNode(original.shippingCarrier.toString().trim()));
        root.appendChild(elem);
      }      

      if((shippingCost == null && original.shippingCost != null) || 
         (shippingCost != null && !shippingCost.equals(original.shippingCost))){
        Element elem = doc.createElement("shipping_cost");
        elem.appendChild(doc.createTextNode(original.shippingCost.toString().trim()));
        root.appendChild(elem);
      }      

      if((deliveredDate == null && original.deliveredDate != null) || 
         (deliveredDate != null && !deliveredDate.equals(original.deliveredDate))){
        Element elem = doc.createElement("delivered_date");
        elem.appendChild(doc.createTextNode(original.deliveredDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((isExternal == null && original.isExternal != null) || 
         (isExternal != null && !isExternal.equals(original.isExternal))){
        Element elem = doc.createElement("is_external");
        elem.appendChild(doc.createTextNode(original.isExternal.toString().trim()));
        root.appendChild(elem);
      }      

      if((externalOrderNumber == null && original.externalOrderNumber != null) || 
         (externalOrderNumber != null && !externalOrderNumber.equals(original.externalOrderNumber))){
        Element elem = doc.createElement("external_order_number");
        elem.appendChild(doc.createTextNode(original.externalOrderNumber.toString().trim()));
        root.appendChild(elem);
      }      

      if((isFilled == null && original.isFilled != null) || 
         (isFilled != null && !isFilled.equals(original.isFilled))){
        Element elem = doc.createElement("is_filled");
        elem.appendChild(doc.createTextNode(original.isFilled.toString().trim()));
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
    return "order";
  }
  
}   
