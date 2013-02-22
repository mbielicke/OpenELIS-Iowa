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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SampleNeonatal.FetchBySampleId",
                query = "select new org.openelis.domain.SampleNeonatalDO(s.id, s.sampleId, s.patientId, s.nextOfKinId," +
                		"s.nextOfKinRelationId, s.isNicu, s.birthOrderId, s.gestationalAge, s.feedingId, s.weight, s.isTransfused," +
                		"s.transfusionDate, s.transfusionAge, s.isRepeat, s.collectionAge, s.isCollectionValid, s.providerId, s.barcodeNumber)"
                      + " from SampleNeonatal s where s.sampleId = :id"),
    @NamedQuery( name = "SampleNeonatal.FetchBySampleIds",
                query = "select new org.openelis.domain.SampleNeonatalDO(s.id, s.sampleId, s.patientId, s.nextOfKinId," +
                        "s.nextOfKinRelationId, s.isNicu, s.birthOrderId, s.gestationalAge, s.feedingId, s.weight, s.isTransfused," +
                        "s.transfusionDate, s.transfusionAge, s.isRepeat, s.collectionAge, s.isCollectionValid, s.providerId, s.barcodeNumber)"
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
    
    @Column(name = "next_of_kin_id")
    private Integer             nextOfKinId;
    
    @Column(name = "next_of_kin_relation_id")
    private Integer             nextOfKinRelationId;
    
    @Column(name = "is_nicu")
    private String              isNicu;
    
    @Column(name = "birth_order_id")
    private Integer             birthOrderId;
    
    @Column(name = "gestational_age")
    private Integer             gestationalAge;
    
    @Column(name = "feeding_id")
    private Integer             feedingId;
    
    @Column(name = "weight")
    private Integer             weight;
    
    @Column(name = "is_transfused")
    private String              isTransfused;
    
    @Column(name = "transfusion_date")
    private Date                transfusionDate;
    
    @Column(name = "transfusion_age")
    private Integer             transfusionAge;
    
    @Column(name = "is_repeat")
    private String              isRepeat;
    
    @Column(name = "collection_age")
    private Integer             collectionAge;
    
    @Column(name = "is_collection_valid")
    private String             isCollectionValid;
    
    @Column(name = "provider_id")
    private Integer             providerId;
    
    @Column(name = "barcode_number")
    private String              barcodeNumber;
    
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

    public String getIsNicu() {
        return isNicu;
    }

    public void setIsNicu(String isNicu) {
        if (DataBaseUtil.isDifferent(isNicu, this.isNicu))
            this.isNicu = isNicu;
    }

    public Integer getBirthOrderId() {
        return birthOrderId;
    }

    public void setBirthOrderId(Integer birthOrderId) {
        if (DataBaseUtil.isDifferent(birthOrderId, this.birthOrderId))
            this.birthOrderId = birthOrderId;
    }

    public Integer getGestationalAge() {
        return gestationalAge;
    }

    public void setGestationalAge(Integer gestationalAge) {
        if (DataBaseUtil.isDifferent(gestationalAge, this.gestationalAge))
            this.gestationalAge = gestationalAge;
    }

    public Integer getFeedingId() {
        return feedingId;
    }

    public void setFeedingId(Integer feedingId) {
        if (DataBaseUtil.isDifferent(feedingId, this.feedingId))
            this.feedingId = feedingId;
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
        return DataBaseUtil.toYM(transfusionDate);
    }

    public void setTransfusionDate(Datetime transfusionDate) {
        if (DataBaseUtil.isDifferentYM(transfusionDate, this.transfusionDate))
            this.transfusionDate = DataBaseUtil.toDate(transfusionDate);
    }

    public Integer getTransfusionAge() {
        return transfusionAge;
    }

    public void setTransfusionAge(Integer transfusionAge) {
        if (DataBaseUtil.isDifferent(transfusionAge, this.transfusionAge))
            this.transfusionAge = transfusionAge;
    }

    public String getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(String isRepeat) {
        if (DataBaseUtil.isDifferent(isRepeat, this.isRepeat))
            this.isRepeat = isRepeat;
    }

    public Integer getCollectionAge() {
        return collectionAge;
    }

    public void setCollectionAge(Integer collectionAge) {
        if (DataBaseUtil.isDifferent(collectionAge, this.collectionAge))
            this.collectionAge = collectionAge;
    }

    public String getIsCollectionValid() {
        return isCollectionValid;
    }

    public void setIsCollectionValid(String isCollectionValid) {
        if (DataBaseUtil.isDifferent(isCollectionValid, this.isCollectionValid))
            this.isCollectionValid = isCollectionValid;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        if (DataBaseUtil.isDifferent(providerId, this.providerId))
            this.providerId = providerId;
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        if (DataBaseUtil.isDifferent(barcodeNumber, this.barcodeNumber))
            this.barcodeNumber = barcodeNumber;
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
                 .setField("next_of_kin_id", nextOfKinId, original.nextOfKinId)
                 .setField("next_of_kin_relation_id", nextOfKinRelationId, original.nextOfKinRelationId)
                 .setField("is_nicu", isNicu, original.isNicu)
                 .setField("birth_order_id", birthOrderId, original.birthOrderId, Constants.table().DICTIONARY)
                 .setField("gestational_age", gestationalAge, original.gestationalAge)
                 .setField("feeding_id", feedingId, original.feedingId, Constants.table().DICTIONARY)
                 .setField("weight", weight, original.weight)
                 .setField("is_transfused", isTransfused, original.isTransfused)
                 .setField("transfusion_date", transfusionDate, original.transfusionDate)
                 .setField("transfusion_age", transfusionAge, original.transfusionAge)
                 .setField("is_repeat", isRepeat, original.isRepeat)
                 .setField("collection_age", collectionAge, original.collectionAge)
                 .setField("is_collection_valid", isCollectionValid, original.isCollectionValid)
                 .setField("provider_id", providerId, original.providerId)
                 .setField("barcode_number", barcodeNumber, original.barcodeNumber);

        return audit;
    }
}