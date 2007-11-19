
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

  @Column(name="sample")
  private Integer sample;             

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

  public Integer getSample() {
    return sample;
  }
  public void setSample(Integer sample) {
    if((sample == null && this.sample != null) || 
       (sample != null && !sample.equals(this.sample)))
      this.sample = sample;
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

  
  public void setClone() {
    try {
      original = (SampleEnvironmental)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((sample == null && original.sample != null) || 
         (sample != null && !sample.equals(original.sample))){
        Element elem = doc.createElement("sample");
        elem.appendChild(doc.createTextNode(original.sample.toString()));
        root.appendChild(elem);
      }      

      if((isHazardous == null && original.isHazardous != null) || 
         (isHazardous != null && !isHazardous.equals(original.isHazardous))){
        Element elem = doc.createElement("is_hazardous");
        elem.appendChild(doc.createTextNode(original.isHazardous.toString()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((collector == null && original.collector != null) || 
         (collector != null && !collector.equals(original.collector))){
        Element elem = doc.createElement("collector");
        elem.appendChild(doc.createTextNode(original.collector.toString()));
        root.appendChild(elem);
      }      

      if((collectorPhone == null && original.collectorPhone != null) || 
         (collectorPhone != null && !collectorPhone.equals(original.collectorPhone))){
        Element elem = doc.createElement("collector_phone");
        elem.appendChild(doc.createTextNode(original.collectorPhone.toString()));
        root.appendChild(elem);
      }      

      if((samplingLocation == null && original.samplingLocation != null) || 
         (samplingLocation != null && !samplingLocation.equals(original.samplingLocation))){
        Element elem = doc.createElement("sampling_location");
        elem.appendChild(doc.createTextNode(original.samplingLocation.toString()));
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
    return "sample_environmental";
  }
  
}   
