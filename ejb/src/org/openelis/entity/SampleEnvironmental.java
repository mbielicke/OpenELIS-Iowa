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
  * SampleEnvironmental Entity POJO for database 
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
  
}   
