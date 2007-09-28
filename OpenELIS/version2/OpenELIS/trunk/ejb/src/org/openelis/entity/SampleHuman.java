
package org.openelis.entity;

/**
  * SampleHuman Entity POJO for database 
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
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="sample_human")
@EntityListeners({AuditUtil.class})
public class SampleHuman implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample")
  private Integer sample;             

  @Column(name="patient")
  private Integer patient;             

  @Column(name="provider")
  private Integer provider;             

  @Column(name="provider_phone")
  private String providerPhone;             


  @Transient
  private SampleHuman original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getSample() {
    return sample;
  }
  public void setSample(Integer sample) {
    this.sample = sample;
  }

  public Integer getPatient() {
    return patient;
  }
  public void setPatient(Integer patient) {
    this.patient = patient;
  }

  public Integer getProvider() {
    return provider;
  }
  public void setProvider(Integer provider) {
    this.provider = provider;
  }

  public String getProviderPhone() {
    return providerPhone;
  }
  public void setProviderPhone(String providerPhone) {
    this.providerPhone = providerPhone;
  }

  
  public void setClone() {
    try {
      original = (SampleHuman)this.clone();
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

      if((patient == null && original.patient != null) || 
         (patient != null && !patient.equals(original.patient))){
        Element elem = doc.createElement("patient");
        elem.appendChild(doc.createTextNode(original.patient.toString()));
        root.appendChild(elem);
      }      

      if((provider == null && original.provider != null) || 
         (provider != null && !provider.equals(original.provider))){
        Element elem = doc.createElement("provider");
        elem.appendChild(doc.createTextNode(original.provider.toString()));
        root.appendChild(elem);
      }      

      if((providerPhone == null && original.providerPhone != null) || 
         (providerPhone != null && !providerPhone.equals(original.providerPhone))){
        Element elem = doc.createElement("provider_phone");
        elem.appendChild(doc.createTextNode(original.providerPhone.toString()));
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
    return "sample_human";
  }
  
}   
