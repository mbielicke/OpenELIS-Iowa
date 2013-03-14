/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
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

    protected Integer         id, sampleId, birthOrder, gestationalAge, nextOfKinRelationId,
                               feedingId, weight, collectionAge, providerId;

    protected String          isRepeat, isNicu, weightSign, isTransfused, isCollectionValid,
                               formNumber;

    protected Datetime        transfusionDate;

    protected PatientDO       patient, nextOfKin;

    public SampleNeonatalDO() {
        patient = new PatientDO();
        nextOfKin = new PatientDO();
    }

    public SampleNeonatalDO(Integer id, Integer sampleId, Integer patientId, Integer birthOrder,
                            Integer gestationalAge, Integer nextOfKinId, Integer nextOfKinRelationId,
                            String isRepeat, String isNicu, Integer feedingId, String weightSign,
                            Integer weight, String isTransfused, Date transfusionDate,
                            String isCollectionValid, Integer collectionAge, Integer providerId,
                            String formNumber, String patientLastName, String patientFirstName,
                            String patientMiddleName, Integer patientAddressId, 
                            Date patientBirthDate, Date patientBirthTime, Integer patientGenderId, 
                            Integer patientRaceId, Integer patientEthnicityId, String patientMultipleUnit,
                            String patientStreetAddress, String patientCity, String patientState, 
                            String patientZipCode, String patientWorkPhone, String patientHomePhone,
                            String patientCellPhone, String patientFaxPhone, String patientEmail, 
                            String patientCountry, String nextOfKinLastName, String nextOfKinFirstName,
                            String nextOfKinMiddleName, Integer nextOfKinAddressId, 
                            Date nextOfKinBirthDate, Date nextOfKinBirthTime, Integer nextOfKinGenderId, 
                            Integer nextOfKinRaceId, Integer nextOfKinEthnicityId,
                            String nextOfKinMultipleUnit, String nextOfKinStreetAddress,
                            String nextOfKinCity, String nextOfKinState, String nextOfKinZipCode,
                            String nextOfKinWorkPhone, String nextOfKinHomePhone,
                            String nextOfKinCellPhone, String nextOfKinFaxPhone,
                            String nextOfKinEmail, String nextOfKinCountry) {
        setId(id);
        setSampleId(sampleId);
        setBirthOrder(birthOrder);
        setGestationalAge(gestationalAge);
        setNextOfKinRelationId(nextOfKinRelationId);
        setIsRepeat(isRepeat);
        setIsNicu(isNicu);
        setFeedingId(feedingId);
        setWeightSign(weightSign);
        setWeight(weight);
        setIsTransfused(isTransfused);
        setTransfusionDate(DataBaseUtil.toYD(transfusionDate));
        setIsCollectionValid(isCollectionValid);
        setCollectionAge(collectionAge);
        setProviderId(providerId);
        setFormNumber(formNumber);
        
        patient = new PatientDO(patientId, patientLastName, patientFirstName, patientMiddleName,
                                patientAddressId, patientBirthDate, patientBirthTime, 
                                patientGenderId, patientRaceId, patientEthnicityId, 
                                patientMultipleUnit, patientStreetAddress, patientCity,
                                patientState, patientZipCode, patientWorkPhone, 
                                patientHomePhone, patientCellPhone, patientFaxPhone, 
                                patientEmail, patientCountry);
        
        nextOfKin = new PatientDO(nextOfKinId, nextOfKinLastName, nextOfKinFirstName, 
                                  nextOfKinMiddleName, nextOfKinAddressId, nextOfKinBirthDate,
                                  nextOfKinBirthTime, nextOfKinGenderId, nextOfKinRaceId,
                                  nextOfKinEthnicityId, nextOfKinMultipleUnit, nextOfKinStreetAddress,
                                  nextOfKinCity, nextOfKinState, nextOfKinZipCode,
                                  nextOfKinWorkPhone, nextOfKinHomePhone, nextOfKinCellPhone,
                                  nextOfKinFaxPhone, nextOfKinEmail, nextOfKinCountry);
        
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

    public Integer getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(Integer birthOrderId) {
        this.birthOrder = birthOrderId;
        _changed = true;
    }

    public Integer getGestationalAge() {
        return gestationalAge;
    }

    public void setGestationalAge(Integer gestationalAge) {
        this.gestationalAge = gestationalAge;
        _changed = true;
    }

    public Integer getNextOfKinRelationId() {
        return nextOfKinRelationId;
    }

    public void setNextOfKinRelationId(Integer nextOfKinRelationId) {
        this.nextOfKinRelationId = nextOfKinRelationId;
        _changed = true;
    }

    public String getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(String isRepeat) {
        this.isRepeat = DataBaseUtil.trim(isRepeat);
        _changed = true;
    }

    public String getIsNicu() {
        return isNicu;
    }

    public void setIsNicu(String isNicu) {
        this.isNicu = DataBaseUtil.trim(isNicu);
        _changed = true;
    }

    public Integer getFeedingId() {
        return feedingId;
    }

    public void setFeedingId(Integer feedingId) {
        this.feedingId = feedingId;
        _changed = true;
    }

    public String getWeightSign() {
        return weightSign;
    }

    public void setWeightSign(String weightSign) {
        this.weightSign = weightSign;
        _changed = true;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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

    public String getIsCollectionValid() {
        return isCollectionValid;
    }

    public void setIsCollectionValid(String isCollectionValid) {
        this.isCollectionValid = DataBaseUtil.trim(isCollectionValid);
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

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = DataBaseUtil.trim(formNumber);
        _changed = true;
    }

    public PatientDO getPatient() {
        return patient;
    }

    public PatientDO getNextOfKin() {
        return nextOfKin;
    }
}