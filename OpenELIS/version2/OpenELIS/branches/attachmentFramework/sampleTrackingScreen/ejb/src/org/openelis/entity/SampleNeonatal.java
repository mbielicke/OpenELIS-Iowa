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
 * SampleNeonatal Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SampleNeonatal.FetchBySampleIds",
                query = "select distinct new org.openelis.domain.SampleNeonatalDO(s.id, s.sampleId, s.patientId," +
                        "s.birthOrder, s.gestationalAge, s.nextOfKinId, s.nextOfKinRelationId," +
                        "s.isRepeat, s.isNicu, s.feedingId, s.weightSign, s.weight, s.isTransfused," +
                        "s.transfusionDate, s.isCollectionValid, s.collectionAge, s.providerId, s.formNumber)"
                      + " from SampleNeonatal s where s.sampleId in (:ids)")})
@Entity
@Table(name = "sample_neonatal")
@EntityListeners({AuditUtil.class})
public class SampleNeonatal implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer             id;

    @Column(name = "sample_id")
    private Integer             sampleId;
    
    @Column(name = "patient_id")
    private Integer             patientId;
    
    @Column(name = "birth_order")
    private Integer             birthOrder;
    
    @Column(name = "gestational_age")
    private Integer             gestationalAge;
    
    @Column(name = "next_of_kin_id")
    private Integer             nextOfKinId;
    
    @Column(name = "next_of_kin_relation_id")
    private Integer             nextOfKinRelationId;
    
    @Column(name = "is_repeat")
    private String              isRepeat;
    
    @Column(name = "is_nicu")
    private String              isNicu;
    
    @Column(name = "feeding_id")
    private Integer             feedingId;
    
    @Column(name = "weight_sign")
    private String              weightSign;
    
    @Column(name = "weight")
    private Integer             weight;
    
    @Column(name = "is_transfused")
    private String              isTransfused;
    
    @Column(name = "transfusion_date")
    private Date                transfusionDate;
    
    @Column(name = "is_collection_valid")
    private String             isCollectionValid;
    
    @Column(name = "collection_age")
    private Integer             collectionAge;
    
    @Column(name = "provider_id")
    private Integer             providerId;
    
    @Column(name = "form_number")
    private String              formNumber;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient              patient;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_of_kin_id", insertable = false, updatable = false)
    private Patient              nextOfKin;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider              provider;
    
    @Transient
    private SampleNeonatal      original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        if (DataBaseUtil.isDifferent(patientId, this.patientId))
            this.patientId = patientId;
    }
    
    public Integer getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(Integer birthOrder) {
        if (DataBaseUtil.isDifferent(birthOrder, this.birthOrder))
            this.birthOrder = birthOrder;
    }

    public Integer getGestationalAge() {
        return gestationalAge;
    }

    public void setGestationalAge(Integer gestationalAge) {
        if (DataBaseUtil.isDifferent(gestationalAge, this.gestationalAge))
            this.gestationalAge = gestationalAge;
    }

    public Integer getNextOfKinId() {
        return nextOfKinId;
    }

    public void setNextOfKinId(Integer nextOfKinId) {
        if (DataBaseUtil.isDifferent(nextOfKinId, this.nextOfKinId))
            this.nextOfKinId = nextOfKinId;
    }

    public Integer getNextOfKinRelationId() {
        return nextOfKinRelationId;
    }

    public void setNextOfKinRelationId(Integer nextOfKinRelationId) {
        if (DataBaseUtil.isDifferent(nextOfKinRelationId, this.nextOfKinRelationId))
            this.nextOfKinRelationId = nextOfKinRelationId;
    }

    public String getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(String isRepeat) {
        if (DataBaseUtil.isDifferent(isRepeat, this.isRepeat))
            this.isRepeat = isRepeat;
    }

    public String getIsNicu() {
        return isNicu;
    }

    public void setIsNicu(String isNicu) {
        if (DataBaseUtil.isDifferent(isNicu, this.isNicu))
            this.isNicu = isNicu;
    }

    public Integer getFeedingId() {
        return feedingId;
    }

    public void setFeedingId(Integer feedingId) {
        if (DataBaseUtil.isDifferent(feedingId, this.feedingId))
            this.feedingId = feedingId;
    }
    
    public String getWeightSign() {
        return weightSign;
    }

    public void setWeightSign(String weightSign) {
        if (DataBaseUtil.isDifferent(weightSign, this.weightSign))
            this.weightSign = weightSign;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        if (DataBaseUtil.isDifferent(weight, this.weight))
            this.weight = weight;
    }

    public String getIsTransfused() {
        return isTransfused;
    }

    public void setIsTransfused(String isTransfused) {
        if (DataBaseUtil.isDifferent(isTransfused, this.isTransfused))
            this.isTransfused = isTransfused;
    }

    public Datetime getTransfusionDate() {
        return DataBaseUtil.toYD(transfusionDate);
    }

    public void setTransfusionDate(Datetime transfusionDate) {
        if (DataBaseUtil.isDifferentYD(transfusionDate, this.transfusionDate))
            this.transfusionDate = DataBaseUtil.toDate(transfusionDate);
    }
    
    public Integer getCollectionAge() {
        return collectionAge;
    }
    
    public String getIsCollectionValid() {
        return isCollectionValid;
    }

    public void setIsCollectionValid(String isCollectionValid) {
        if (DataBaseUtil.isDifferent(isCollectionValid, this.isCollectionValid))
            this.isCollectionValid = isCollectionValid;
    }

    public void setCollectionAge(Integer collectionAge) {
        if (DataBaseUtil.isDifferent(collectionAge, this.collectionAge))
            this.collectionAge = collectionAge;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        if (DataBaseUtil.isDifferent(providerId, this.providerId))
            this.providerId = providerId;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        if (DataBaseUtil.isDifferent(formNumber, this.formNumber))
            this.formNumber = formNumber;
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

    public Patient getNextOfKin() {
        return nextOfKin;
    }

    public void setNextOfKin(Patient nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setClone() {
        try {
            original = (SampleNeonatal)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SAMPLE_NEONATAL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId, Constants.table().SAMPLE)
                 .setField("patient_id", patientId, original.patientId)
                 .setField("birth_order", birthOrder, original.birthOrder)
                 .setField("gestational_age", gestationalAge, original.gestationalAge)
                 .setField("next_of_kin_id", nextOfKinId, original.nextOfKinId)
                 .setField("next_of_kin_relation_id", nextOfKinRelationId, original.nextOfKinRelationId)
                 .setField("is_repeat", isRepeat, original.isRepeat)  
                 .setField("is_nicu", isNicu, original.isNicu)
                 .setField("feeding_id", feedingId, original.feedingId, Constants.table().DICTIONARY)
                 .setField("weight_sign", weightSign, original.weightSign)
                 .setField("weight", weight, original.weight)
                 .setField("is_transfused", isTransfused, original.isTransfused)
                 .setField("transfusion_date", transfusionDate, original.transfusionDate)
                 .setField("is_collection_valid", isCollectionValid, original.isCollectionValid)
                 .setField("collection_age", collectionAge, original.collectionAge)
                 .setField("provider_id", providerId, original.providerId)
                 .setField("form_number", formNumber, original.formNumber);

        return audit;
    }
}