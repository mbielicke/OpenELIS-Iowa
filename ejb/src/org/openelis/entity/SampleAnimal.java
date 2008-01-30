
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

  @Column(name="sample")
  private Integer sample;             

  @Column(name="animal_common_name")
  private Integer animalCommonName;             

  @Column(name="animal_scientific_name")
  private Integer animalScientificName;             

  @Column(name="collector")
  private String collector;             

  @Column(name="collector_phone")
  private String collectorPhone;             

  @Column(name="sampling_location")
  private String samplingLocation;             

  @Column(name="address")
  private Integer address;             


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

  public Integer getSample() {
    return sample;
  }
  public void setSample(Integer sample) {
    if((sample == null && this.sample != null) || 
       (sample != null && !sample.equals(this.sample)))
      this.sample = sample;
  }

  public Integer getAnimalCommonName() {
    return animalCommonName;
  }
  public void setAnimalCommonName(Integer animalCommonName) {
    if((animalCommonName == null && this.animalCommonName != null) || 
       (animalCommonName != null && !animalCommonName.equals(this.animalCommonName)))
      this.animalCommonName = animalCommonName;
  }

  public Integer getAnimalScientificName() {
    return animalScientificName;
  }
  public void setAnimalScientificName(Integer animalScientificName) {
    if((animalScientificName == null && this.animalScientificName != null) || 
       (animalScientificName != null && !animalScientificName.equals(this.animalScientificName)))
      this.animalScientificName = animalScientificName;
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

  public Integer getAddress() {
    return address;
  }
  public void setAddress(Integer address) {
    if((address == null && this.address != null) || 
       (address != null && !address.equals(this.address)))
      this.address = address;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((sample == null && original.sample != null) || 
         (sample != null && !sample.equals(original.sample))){
        Element elem = doc.createElement("sample");
        elem.appendChild(doc.createTextNode(original.sample.toString().trim()));
        root.appendChild(elem);
      }      

      if((animalCommonName == null && original.animalCommonName != null) || 
         (animalCommonName != null && !animalCommonName.equals(original.animalCommonName))){
        Element elem = doc.createElement("animal_common_name");
        elem.appendChild(doc.createTextNode(original.animalCommonName.toString().trim()));
        root.appendChild(elem);
      }      

      if((animalScientificName == null && original.animalScientificName != null) || 
         (animalScientificName != null && !animalScientificName.equals(original.animalScientificName))){
        Element elem = doc.createElement("animal_scientific_name");
        elem.appendChild(doc.createTextNode(original.animalScientificName.toString().trim()));
        root.appendChild(elem);
      }      

      if((collector == null && original.collector != null) || 
         (collector != null && !collector.equals(original.collector))){
        Element elem = doc.createElement("collector");
        elem.appendChild(doc.createTextNode(original.collector.toString().trim()));
        root.appendChild(elem);
      }      

      if((collectorPhone == null && original.collectorPhone != null) || 
         (collectorPhone != null && !collectorPhone.equals(original.collectorPhone))){
        Element elem = doc.createElement("collector_phone");
        elem.appendChild(doc.createTextNode(original.collectorPhone.toString().trim()));
        root.appendChild(elem);
      }      

      if((samplingLocation == null && original.samplingLocation != null) || 
         (samplingLocation != null && !samplingLocation.equals(original.samplingLocation))){
        Element elem = doc.createElement("sampling_location");
        elem.appendChild(doc.createTextNode(original.samplingLocation.toString().trim()));
        root.appendChild(elem);
      }      

      if((address == null && original.address != null) || 
         (address != null && !address.equals(original.address))){
        Element elem = doc.createElement("address");
        elem.appendChild(doc.createTextNode(original.address.toString().trim()));
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
    return "sample_animal";
  }
  
}   
