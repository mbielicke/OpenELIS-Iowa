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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table sample_neonatal.
 */

public class SampleNeonatalDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    private Integer             id, sampleId, patientId, nextOfKinId, nextOfKinRelationId,
                                 birthOrderId, gestationalAge, feedingId, weight,
                                 transfusionAge, collectionAge, providerId;
    
    private String              isNicu, isTransfused, isRepeat, isCollectionValid, 
                                 barcodeNumber;
    
    private Datetime            transfusionDate;
    
    public SampleNeonatalDO() {
    }
    
    public SampleNeonatalDO(Integer id, Integer sampleId, Integer patientId, Integer nextOfKinId,
                             Integer nextOfKinRelationId, String isNicu, Integer birthOrderId,
                             Integer gestationalAge, Integer feedingId, Integer weight, 
                             String isTransfused, Date transfusionDate, Integer transfusionAge,
                             String isRepeat, Integer collectionAge, String isCollectionValid,
                             Integer providerId, String barcodeNumber) {
       setId(id);
       setSampleId(sampleId);
       setPatientId(patientId);
       setNextOfKinId(nextOfKinId);
       setNextOfKinRelationId(nextOfKinRelationId);
       setIsNicu(isNicu);
       setBirthOrderId(birthOrderId);
       setGestationalAge(gestationalAge);
       setFeedingId(feedingId);
       setWeight(weight);
       setIsTransfused(isTransfused);
       setTransfusionDate(DataBaseUtil.toYD(transfusionDate));
       setTransfusionAge(transfusionAge);
       setIsRepeat(isRepeat);
       setCollectionAge(collectionAge);
       setIsCollectionValid(isCollectionValid);
       setProviderId(providerId);
       setBarcodeNumber(barcodeNumber);
       _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
        _changed = true;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
        _changed = true;
    }

    public Integer getNextOfKinId() {
        return nextOfKinId;
    }

    public void setNextOfKinId(Integer nextOfKinId) {
        this.nextOfKinId = nextOfKinId;
        _changed = true;
    }

    public Integer getNextOfKinRelationId() {
        return nextOfKinRelationId;
    }

    public void setNextOfKinRelationId(Integer nextOfKinRelationId) {
        this.nextOfKinRelationId = nextOfKinRelationId;
        _changed = true;
    }

    public Integer getBirthOrderId() {
        return birthOrderId;
    }

    public void setBirthOrderId(Integer birthOrderId) {
        this.birthOrderId = birthOrderId;
        _changed = true;
    }

    public Integer getGestationalAge() {
        return gestationalAge;
    }

    public void setGestationalAge(Integer gestationalAge) {
        this.gestationalAge = gestationalAge;
        _changed = true;
    }

    public Integer getFeedingId() {
        return feedingId;
    }

    public void setFeedingId(Integer feedingId) {
        this.feedingId = feedingId;
        _changed = true;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
        _changed = true;
    }

    public Integer getTransfusionAge() {
        return transfusionAge;
    }

    public void setTransfusionAge(Integer transfusionAge) {
        this.transfusionAge = transfusionAge;
        _changed = true;
    }

    public Integer getCollectionAge() {
        return collectionAge;
    }

    public void setCollectionAge(Integer collectionAge) {
        this.collectionAge = collectionAge;
        _changed = true;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
        _changed = true;
    }

    public String getIsNicu() {
        return isNicu;
    }

    public void setIsNicu(String isNicu) {
        this.isNicu = DataBaseUtil.trim(isNicu);
        _changed = true;
    }

    public String getIsTransfused() {
        return isTransfused;
    }

    public void setIsTransfused(String isTransfused) {
        this.isTransfused = DataBaseUtil.trim(isTransfused);
        _changed = true;
    }
    
    public Datetime getTransfusionDate() {
        return transfusionDate;
    }

    public void setTransfusionDate(Datetime transfusionDate) {
        this.transfusionDate = DataBaseUtil.toYD(transfusionDate);
        _changed = true;
    }

    public String getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(String isRepeat) {
        this.isRepeat = DataBaseUtil.trim(isRepeat);
        _changed = true;
    }

    public String getIsCollectionValid() {
        return isCollectionValid;
    }

    public void setIsCollectionValid(String isCollectionValid) {
        this.isCollectionValid = DataBaseUtil.trim(isCollectionValid);
        _changed = true;
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = DataBaseUtil.trim(barcodeNumber);
        _changed = true;
    }
}