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
  * SampleHuman Entity POJO for database 
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

import org.openelis.domain.ReferenceTable;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "SampleHuman.SampleHumanBySampleId", query = "select new org.openelis.domain.SampleHumanDO(sh.id, sh.sampleId, sh.patientId, " + 
                " sh.providerId, sh.providerPhone) from SampleHuman sh where sh.sampleId = :id")})
                
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
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_id", insertable = false, updatable = false)
  private Sample sample;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", insertable = false, updatable = false)
  private Patient patient;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", insertable = false, updatable = false)
  private Provider provider;

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

  public Sample getSample() {
      return sample;
  }
  public void setSample(Sample sample) {
      this.sample = sample;
  }
  public Patient getPatient() {
      return patient;
  }
  public void setPatient(Patient patient) {
      this.patient = patient;
  }
  public Provider getProvider() {
      return provider;
  }
  public void setProvider(Provider provider) {
      this.provider = provider;
  }
  
  public void setClone() {
    try {
        original = (SampleHuman)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SAMPLE_HUMAN);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("patient_id", patientId, original.patientId)
                 .setField("provider_id", providerId, original.providerId)
                 .setField("provider_phone", providerPhone, original.providerPhone);

        return audit;
  }
}   
