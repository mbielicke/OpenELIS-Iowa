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
  * SampleAnimal Entity POJO for database 
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
@Table(name="sample_animal")
@EntityListeners({AuditUtil.class})
public class SampleAnimal implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_id")
  private Integer sampleId;             

  @Column(name="animal_common_name_id")
  private Integer animalCommonNameId;             

  @Column(name="animal_scientific_name_id")
  private Integer animalScientificNameId;             

  @Column(name="collector")
  private String collector;             

  @Column(name="collector_phone")
  private String collectorPhone;             

  @Column(name="sampling_location")
  private String samplingLocation;             

  @Column(name="address_id")
  private Integer addressId;             


  @Transient
  private SampleAnimal original;

  
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

  public Integer getAnimalCommonNameId() {
    return animalCommonNameId;
  }
  public void setAnimalCommonNameId(Integer animalCommonNameId) {
    if((animalCommonNameId == null && this.animalCommonNameId != null) || 
       (animalCommonNameId != null && !animalCommonNameId.equals(this.animalCommonNameId)))
      this.animalCommonNameId = animalCommonNameId;
  }

  public Integer getAnimalScientificNameId() {
    return animalScientificNameId;
  }
  public void setAnimalScientificNameId(Integer animalScientificNameId) {
    if((animalScientificNameId == null && this.animalScientificNameId != null) || 
       (animalScientificNameId != null && !animalScientificNameId.equals(this.animalScientificNameId)))
      this.animalScientificNameId = animalScientificNameId;
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
      original = (SampleAnimal)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleId,original.sampleId,doc,"sample_id");

      AuditUtil.getChangeXML(animalCommonNameId,original.animalCommonNameId,doc,"animal_common_name_id");

      AuditUtil.getChangeXML(animalScientificNameId,original.animalScientificNameId,doc,"animal_scientific_name_id");

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
    return "sample_animal";
  }
  
}   
