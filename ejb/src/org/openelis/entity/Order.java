
package org.openelis.entity;

/**
  * Order Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Order.OrderInternal", query = "select new org.openelis.domain.OrderDO(o.id, o.status, o.orderedDate, o.neededInDays, o.requestedBy, o.costCenter, o.organizationId, " +
                            " o.isExternal, o.externalOrderNumber, o.reportToId, o.billToId) from Order o where o.id = :id"),
    @NamedQuery(name = "Order.OrderExternalKit", query = "select new org.openelis.domain.OrderDO(o.id, o.status, o.orderedDate, o.neededInDays, o.requestedBy, o.costCenter, o.organizationId, " +
                            " oo.name, oo.address.multipleUnit, oo.address.streetAddress, oo.address.city, oo.address.state, oo.address.zipCode, o.isExternal, o.externalOrderNumber, " +
                            " o.reportToId, o.billToId) from Order o left join o.organization oo where o.id = :id"),
    @NamedQuery(name = "Order.ReportToBillTo", query = "select new org.openelis.domain.BillToReportToDO(o.billToId, o.billTo.name, o.billTo.address.multipleUnit, o.billTo.address.streetAddress," +
                            " o.billTo.address.city, o.billTo.address.state, o.billTo.address.zipCode, o.reportToId, o.reportTo.name, o.reportTo.address.multipleUnit, o.reportTo.address.streetAddress, " +
                            " o.reportTo.address.city, o.reportTo.address.state, o.reportTo.address.zipCode) from Order o where o.id = :id")})
            
@Entity
@Table(name="order")
@EntityListeners({AuditUtil.class})
public class Order implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="status")
  private Integer status;
  
  @Column(name="ordered_date")
  private Date orderedDate;             

  @Column(name="needed_in_days")
  private Integer neededInDays;             

  @Column(name="requested_by")
  private String requestedBy;             

  @Column(name="cost_center")
  private Integer costCenter;             

  @Column(name="organization")
  private Integer organizationId;
  
  @Column(name="is_external")
  private String isExternal;             

  @Column(name="external_order_number")
  private String externalOrderNumber;             

  @Column(name="report_to")
  private Integer reportToId;
  
  @Column(name="bill_to")
  private Integer billToId;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization", insertable = false, updatable = false)
  private Organization organization;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_to", insertable = false, updatable = false)
  private Organization reportTo;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bill_to", insertable = false, updatable = false)
  private Organization billTo;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "order")
  private Collection<OrderItem> orderItem;

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

  public Integer getOrganizationId() {
    return organizationId;
  }
  public void setOrganizationId(Integer organization) {
    if((organization == null && this.organizationId != null) || 
       (organization != null && !organization.equals(this.organizationId)))
      this.organizationId = organization;
  }

  public Datetime getOrderedDate() {
    if(orderedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.DAY,orderedDate);
  }
  public void setOrderedDate (Datetime orderedDate){
    if((orderedDate == null && this.orderedDate != null) || (orderedDate != null && this.orderedDate == null) || 
       (orderedDate != null && !orderedDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.orderedDate))))
        this.orderedDate = orderedDate.getDate();
  }

  public String getRequestedBy() {
    return requestedBy;
  }
  public void setRequestedBy(String requestedBy) {
    if((requestedBy == null && this.requestedBy != null) || 
       (requestedBy != null && !requestedBy.equals(this.requestedBy)))
      this.requestedBy = requestedBy;
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
      
      if((status == null && original.status != null) || 
         (status != null && !status.equals(original.status))){
        Element elem = doc.createElement("status");
        elem.appendChild(doc.createTextNode(original.status.toString().trim()));
        root.appendChild(elem);
      }          

      if((orderedDate == null && original.orderedDate != null) || 
         (orderedDate != null && !orderedDate.equals(original.orderedDate))){
        Element elem = doc.createElement("ordered_date");
        elem.appendChild(doc.createTextNode(original.orderedDate.toString().trim()));
        root.appendChild(elem);
      }
      
      if((neededInDays == null && original.neededInDays != null) || 
         (neededInDays != null && !neededInDays.equals(original.neededInDays))){
        Element elem = doc.createElement("needed_in_days");
        elem.appendChild(doc.createTextNode(original.neededInDays.toString().trim()));
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
      
      if((organizationId == null && original.organizationId != null) || 
         (organizationId != null && !organizationId.equals(original.organizationId))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organizationId.toString().trim()));
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
      
      if((reportToId == null && original.reportToId != null) || 
         (reportToId != null && !reportToId.equals(original.reportToId))){
        Element elem = doc.createElement("report_to");
        elem.appendChild(doc.createTextNode(original.reportToId.toString().trim()));
        root.appendChild(elem);
      } 
      
      if((billToId == null && original.billToId != null) || 
         (billToId != null && !billToId.equals(original.billToId))){
        Element elem = doc.createElement("bill_to");
        elem.appendChild(doc.createTextNode(original.billToId.toString().trim()));
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
public Integer getBillToId() {
    return billToId;
}
public void setBillToId(Integer billTo) {
    if((billTo == null && this.billToId != null) || 
       (billTo != null && !billTo.equals(this.billToId)))
    this.billToId = billTo;
}
public Integer getNeededInDays() {
    return neededInDays;
}
public void setNeededInDays(Integer neededInDays) {
    if((neededInDays == null && this.neededInDays != null) || 
         (neededInDays != null && !neededInDays.equals(this.neededInDays)))
    this.neededInDays = neededInDays;
}
public Integer getReportToId() {
    return reportToId;
}
public void setReportToId(Integer reportTo) {
    if((reportTo == null && this.reportToId != null) || 
         (reportTo != null && !reportTo.equals(this.reportToId)))
    this.reportToId = reportTo;
}
public Integer getStatus() {
    return status;
}
public void setStatus(Integer status) {
    if((status == null && this.status != null) || 
            (status != null && !status.equals(this.status)))
    this.status = status;
}
public void setCostCenter(Integer costCenter) {
    if((costCenter == null && this.costCenter != null) || 
          (costCenter != null && !costCenter.equals(this.costCenter)))
    this.costCenter = costCenter;
}
public Integer getCostCenter() {
    return costCenter;
}
public Organization getBillTo() {
    return billTo;
}

public Organization getOrganization() {
    return organization;
}

public Organization getReportTo() {
    return reportTo;
}
public Collection<OrderItem> getOrderItem() {
    return orderItem;
}
public void setOrderItem(Collection<OrderItem> orderItem) {
    this.orderItem = orderItem;
} 
}   
