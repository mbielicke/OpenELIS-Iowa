/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

package org.openelis.entity;

/**
  * Order Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.OrderItem;
import org.openelis.entity.Organization;
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
    @NamedQuery(name = "Order.OrderInternal", query = "select new org.openelis.domain.OrderDO(o.id, o.statusId, o.orderedDate, o.neededInDays, o.requestedBy, o.costCenterId, o.organizationId, " +
                            " o.isExternal, o.externalOrderNumber, o.reportToId, o.billToId) from Order o where o.id = :id"),
    @NamedQuery(name = "Order.OrderExternalKit", query = "select new org.openelis.domain.OrderDO(o.id, o.statusId, o.orderedDate, o.neededInDays, o.requestedBy, o.costCenterId, o.organizationId, " +
                            " oo.name, oo.address.multipleUnit, oo.address.streetAddress, oo.address.city, oo.address.state, oo.address.zipCode, o.isExternal, o.externalOrderNumber, " +
                            " o.reportToId, o.billToId) from Order o left join o.organization oo where o.id = :id"),
    @NamedQuery(name = "Order.ReportToBillTo", query = "select new org.openelis.domain.BillToReportToDO(o.billToId, o.billTo.name, o.billTo.address.multipleUnit, o.billTo.address.streetAddress," +
                            " o.billTo.address.city, o.billTo.address.state, o.billTo.address.zipCode, o.reportToId, o.reportTo.name, o.reportTo.address.multipleUnit, o.reportTo.address.streetAddress, " +
                            " o.reportTo.address.city, o.reportTo.address.state, o.reportTo.address.zipCode) from Order o where o.id = :id"),
    @NamedQuery(name = "Order.ReceiptsForOrder", query = "select new org.openelis.domain.InventoryReceiptDO(r.id,r.inventoryItemId, o.inventoryItem.name,r.organizationId,r.receivedDate,r.quantityReceived, " +
                            " r.unitCost,r.qcReference,r.externalReference,r.upc) from InventoryTransaction i left join i.fromReceipt r left join i.toOrder o where o.orderId = :id")})
            
@Entity
@Table(name="order")
@EntityListeners({AuditUtil.class})
public class Order implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="status_id")
  private Integer statusId;             

  @Column(name="ordered_date")
  private Date orderedDate;             

  @Column(name="needed_in_days")
  private Integer neededInDays;             

  @Column(name="requested_by")
  private String requestedBy;             

  @Column(name="cost_center_id")
  private Integer costCenterId;             

  @Column(name="organization_id")
  private Integer organizationId;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="external_order_number")
  private String externalOrderNumber;             

  @Column(name="report_to_id")
  private Integer reportToId;             

  @Column(name="bill_to_id")
  private Integer billToId;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", insertable = false, updatable = false)
  private Organization organization;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_to_id", insertable = false, updatable = false)
  private Organization reportTo;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bill_to_id", insertable = false, updatable = false)
  private Organization billTo;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
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

  public Integer getStatusId() {
    return statusId;
  }
  public void setStatusId(Integer statusId) {
    if((statusId == null && this.statusId != null) || 
       (statusId != null && !statusId.equals(this.statusId)))
      this.statusId = statusId;
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

  public Integer getNeededInDays() {
    return neededInDays;
  }
  public void setNeededInDays(Integer neededInDays) {
    if((neededInDays == null && this.neededInDays != null) || 
       (neededInDays != null && !neededInDays.equals(this.neededInDays)))
      this.neededInDays = neededInDays;
  }

  public String getRequestedBy() {
    return requestedBy;
  }
  public void setRequestedBy(String requestedBy) {
    if((requestedBy == null && this.requestedBy != null) || 
       (requestedBy != null && !requestedBy.equals(this.requestedBy)))
      this.requestedBy = requestedBy;
  }

  public Integer getCostCenterId() {
    return costCenterId;
  }
  public void setCostCenterId(Integer costCenterId) {
    if((costCenterId == null && this.costCenterId != null) || 
       (costCenterId != null && !costCenterId.equals(this.costCenterId)))
      this.costCenterId = costCenterId;
  }

  public Integer getOrganizationId() {
    return organizationId;
  }
  public void setOrganizationId(Integer organizationId) {
    if((organizationId == null && this.organizationId != null) || 
       (organizationId != null && !organizationId.equals(this.organizationId)))
      this.organizationId = organizationId;
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

  public Integer getReportToId() {
    return reportToId;
  }
  public void setReportToId(Integer reportToId) {
    if((reportToId == null && this.reportToId != null) || 
       (reportToId != null && !reportToId.equals(this.reportToId)))
      this.reportToId = reportToId;
  }

  public Integer getBillToId() {
    return billToId;
  }
  public void setBillToId(Integer billToId) {
    if((billToId == null && this.billToId != null) || 
       (billToId != null && !billToId.equals(this.billToId)))
      this.billToId = billToId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(statusId,original.statusId,doc,"status_id");

      AuditUtil.getChangeXML(orderedDate,original.orderedDate,doc,"ordered_date");

      AuditUtil.getChangeXML(neededInDays,original.neededInDays,doc,"needed_in_days");

      AuditUtil.getChangeXML(requestedBy,original.requestedBy,doc,"requested_by");

      AuditUtil.getChangeXML(costCenterId,original.costCenterId,doc,"cost_center_id");

      AuditUtil.getChangeXML(organizationId,original.organizationId,doc,"organization_id");

      AuditUtil.getChangeXML(isExternal,original.isExternal,doc,"is_external");

      AuditUtil.getChangeXML(externalOrderNumber,original.externalOrderNumber,doc,"external_order_number");

      AuditUtil.getChangeXML(reportToId,original.reportToId,doc,"report_to_id");

      AuditUtil.getChangeXML(billToId,original.billToId,doc,"bill_to_id");

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
