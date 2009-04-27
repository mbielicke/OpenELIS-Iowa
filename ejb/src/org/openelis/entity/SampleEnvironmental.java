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
  * SampleEnvironmental Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries( {
    @NamedQuery(name = "SampleEnvironmental.SampleEnvironmentalBySampleId", query = "select new org.openelis.domain.SampleEnvironmentalDO(se.id, se.sampleId, se.isHazardous, " + 
                " se.description, se.collector, se.collectorPhone, se.samplingLocation, se.addressId) from SampleEnvironmental se where se.sampleId = :id")})
                
@Entity
@Table(name="sample_environmental")
@EntityListeners({AuditUtil.class})
public class SampleEnvironmental implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_id")
  private Integer sampleId;             

  @Column(name="is_hazardous")
  private String isHazardous;             

  @Column(name="description")
  private String description;             

  @Column(name="collector")
  private String collector;             

  @Column(name="collector_phone")
  private String collectorPhone;             

  @Column(name="sampling_location")
  private String samplingLocation;             

  @Column(name="address_id")
  private Integer addressId;     
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", insertable = false, updatable = false)
  private Address address;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_id", insertable = false, updatable = false)
  private Sample sample;
  
  @Transient
  private SampleEnvironmental original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSampleId() {
    return sampleId;
  }
  public void setSampleId(Integer sampleId) {
    if((sampleId == null && this.sampleId != null) || 
       (sampleId != null && !sampleId.equals(this.sampleId)))
      this.sampleId = sampleId;
  }

  public String getIsHazardous() {
    return isHazardous;
  }
  public void setIsHazardous(String isHazardous) {
    if((isHazardous == null && this.isHazardous != null) || 
       (isHazardous != null && !isHazardous.equals(this.isHazardous)))
      this.isHazardous = isHazardous;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getCollector() {
    return collector;
  }
  public void setCollector(String collector) {
    if((collector == null && this.collector != null) || 
       (collector != null && !collector.equals(this.collector)))
      this.collector = collector;
  }

  public String getCollectorPhone() {
    return collectorPhone;
  }
  public void setCollectorPhone(String collectorPhone) {
    if((collectorPhone == null && this.collectorPhone != null) || 
       (collectorPhone != null && !collectorPhone.equals(this.collectorPhone)))
      this.collectorPhone = collectorPhone;
  }

  public String getSamplingLocation() {
    return samplingLocation;
  }
  public void setSamplingLocation(String samplingLocation) {
    if((samplingLocation == null && this.samplingLocation != null) || 
       (samplingLocation != null && !samplingLocation.equals(this.samplingLocation)))
      this.samplingLocation = samplingLocation;
  }

  public Integer getAddressId() {
    return addressId;
  }
  public void setAddressId(Integer addressId) {
    if((addressId == null && this.addressId != null) || 
       (addressId != null && !addressId.equals(this.addressId)))
      this.addressId = addressId;
  }

  
  public void setClone() {
    try {
      original = (SampleEnvironmental)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleId,original.sampleId,doc,"sample_id");

      AuditUtil.getChangeXML(isHazardous,original.isHazardous,doc,"is_hazardous");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(collector,original.collector,doc,"collector");

      AuditUtil.getChangeXML(collectorPhone,original.collectorPhone,doc,"collector_phone");

      AuditUtil.getChangeXML(samplingLocation,original.samplingLocation,doc,"sampling_location");

      AuditUtil.getChangeXML(addressId,original.addressId,doc,"address_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "sample_environmental";
  }
public Address getAddress() {
    return address;
}
public void setAddress(Address address) {
    this.address = address;
}
public Sample getSample() {
    return sample;
}
public void setSample(Sample sample) {
    this.sample = sample;
}
  
}   
