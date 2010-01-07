/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * Shipping Entity POJO for database 
  */

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

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Shipping.Shipping", query = "select new org.openelis.domain.ShippingDO(s.id, s.statusId, s.shippedFromId, s.shippedToId, shipTo.name, " +
            " s.processedById, s.processedDate, s.shippedMethodId, s.shippedDate, s.numberOfPackages, s.cost, shipTo.address.multipleUnit, shipTo.address.streetAddress, " +
            " shipTo.address.city, shipTo.address.state, shipTo.address.zipCode) from Shipping s LEFT JOIN s.shipTo shipTo where s.id = :id")})
              
@Entity
@Table(name="shipping")
@EntityListeners({AuditUtil.class})
public class Shipping implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="status_id")
  private Integer statusId;             

  @Column(name="shipped_from_id")
  private Integer shippedFromId;     
  
  @Column(name="shipped_to_id")
  private Integer shippedToId;  

  @Column(name="processed_by_id")
  private Integer processedById;             

  @Column(name="processed_date")
  private Date processedDate;             

  @Column(name="shipped_method_id")
  private Integer shippedMethodId;             

  @Column(name="shipped_date")
  private Date shippedDate;             

  @Column(name="number_of_packages")
  private Integer numberOfPackages;  
  
  @Column(name="cost")
  private Double cost;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipped_to_id", insertable = false, updatable = false)
  private Organization shipTo;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipping_id", insertable = false, updatable = false)
  private Collection<ShippingTracking> shippingTracking;

  @Transient
  private Shipping original;

  
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

  public Integer getShippedFromId() {
    return shippedFromId;
  }
  public void setShippedFromId(Integer shippedFromId) {
    if((shippedFromId == null && this.shippedFromId != null) || 
       (shippedFromId != null && !shippedFromId.equals(this.shippedFromId)))
      this.shippedFromId = shippedFromId;
  }
  
  public Integer getShippedToId() {
      return shippedToId;
    }
    public void setShippedToId(Integer shippedToId) {
      if((shippedToId == null && this.shippedToId != null) || 
         (shippedToId != null && !shippedToId.equals(this.shippedToId)))
        this.shippedToId = shippedToId;
    }

  public Integer getProcessedById() {
    return processedById;
  }
  public void setProcessedById(Integer processedById) {
    if((processedById == null && this.processedById != null) || 
       (processedById != null && !processedById.equals(this.processedById)))
      this.processedById = processedById;
  }

  public Datetime getProcessedDate() {
    if(processedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.DAY,processedDate);
  }
  public void setProcessedDate (Datetime processedDate){
    if((processedDate == null && this.processedDate != null) || (processedDate != null && this.processedDate == null) || 
       (processedDate != null && !processedDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.processedDate))))
      this.processedDate = processedDate.getDate();
  }

  public Integer getShippedMethodId() {
    return shippedMethodId;
  }
  public void setShippedMethodId(Integer shippedMethodId) {
    if((shippedMethodId == null && this.shippedMethodId != null) || 
       (shippedMethodId != null && !shippedMethodId.equals(this.shippedMethodId)))
      this.shippedMethodId = shippedMethodId;
  }

  public Datetime getShippedDate() {
    if(shippedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.DAY,shippedDate);
  }
  public void setShippedDate (Datetime shippedDate){
    if((shippedDate == null && this.shippedDate != null) || (shippedDate != null && this.shippedDate == null) || 
       (shippedDate != null && !shippedDate.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.shippedDate))))
      this.shippedDate = shippedDate.getDate();
  }

  public Integer getNumberOfPackages() {
    return numberOfPackages;
  }
  public void setNumberOfPackages(Integer numberOfPackages) {
    if((numberOfPackages == null && this.numberOfPackages != null) || 
       (numberOfPackages != null && !numberOfPackages.equals(this.numberOfPackages)))
      this.numberOfPackages = numberOfPackages;
  }
  
  public Double getCost() {
      return cost;
    }
  
  public void setCost(Double cost) {
      if((cost == null && this.cost != null) || 
         (cost != null && !cost.equals(this.cost)))
        this.cost = cost;
  }
  
  public Collection<ShippingTracking> getShippingTracking() {
      return shippingTracking;
  }
  
  public void setShippingTracking(Collection<ShippingTracking> shippingTracking) {
      this.shippingTracking = shippingTracking;
  }
  
  public Organization getShipTo() {
      return shipTo;
  }
  
  public void setShipTo(Organization shipTo) {
      this.shipTo = shipTo;
  }
  
  public void setClone() {
    try {
      original = (Shipping)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SHIPPING);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("status_id", statusId, original.statusId)
                 .setField("shipped_from_id", shippedFromId, original.shippedFromId)
                 .setField("shipped_to_id", shippedToId, original.shippedToId)
                 .setField("processed_by_id", processedById, original.processedById)
                 .setField("processed_date", processedDate, original.processedDate)
                 .setField("shipped_method_id", shippedMethodId, original.shippedMethodId)
                 .setField("shipped_date", shippedDate, original.shippedDate)
                 .setField("number_of_packages", numberOfPackages, original.numberOfPackages)
                 .setField("cost", cost, original.cost);

        return audit;
  }  
}   
