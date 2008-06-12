
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
import org.openelis.utils.Auditable;

@Entity
@Table(name="sample_human")
@EntityListeners({AuditUtil.class})
public class SampleHuman implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_id")
  private Integer sampleId;             

  @Column(name="patient_id")
  private Integer patientId;             

  @Column(name="provider_id")
  private Integer providerId;             

  @Column(name="provider_phone")
  private String providerPhone;             


  @Transient
  private SampleHuman original;

  
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

  public Integer getPatientId() {
    return patientId;
  }
  public void setPatientId(Integer patientId) {
    if((patientId == null && this.patientId != null) || 
       (patientId != null && !patientId.equals(this.patientId)))
      this.patientId = patientId;
  }

  public Integer getProviderId() {
    return providerId;
  }
  public void setProviderId(Integer providerId) {
    if((providerId == null && this.providerId != null) || 
       (providerId != null && !providerId.equals(this.providerId)))
      this.providerId = providerId;
  }

  public String getProviderPhone() {
    return providerPhone;
  }
  public void setProviderPhone(String providerPhone) {
    if((providerPhone == null && this.providerPhone != null) || 
       (providerPhone != null && !providerPhone.equals(this.providerPhone)))
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleId,original.sampleId,doc,"sample_id");

      AuditUtil.getChangeXML(patientId,original.patientId,doc,"patient_id");

      AuditUtil.getChangeXML(providerId,original.providerId,doc,"provider_id");

      AuditUtil.getChangeXML(providerPhone,original.providerPhone,doc,"provider_phone");

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
