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
import org.openelis.gwt.common.RPC;

/**
 * The class is used to carry fields for the MCL Violation Report. The fields are considered read/display
 * and do not get committed to the database.
 */

public class MCLViolationReportVO implements RPC {
    
    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, accessionNumber, stateLabId, analysisId,
                              sectionId, unitOfMeasureId;
    protected Datetime        collectionDate, collectionTime, anaStartedDate, anaReleasedDate;
    protected String          facilityId, sampleType, samplePointId, location, pwsId,
                              pwsName, fieldOffice, organizationName, testName, 
                              methodName, unitDescription;

    public MCLViolationReportVO() {
    }
    
    public MCLViolationReportVO(Integer sampleId, Integer accessionNumber, Date collectionDate,
                                Date collectionTime, Integer stateLabId, String facilityId,
                                String sampleType, String samplePointId, String location,
                                String pwsId, String pwsName, String fieldOffice,
                                String organizationName, Integer analysisId, Integer sectionId,
                                Integer unitOfMeasureId, Date anaStartedDate, Date anaReleasedDate,
                                String unitDescription, String testName, String methodName) {
        setSampleId(sampleId);
        setAccessionNumber(accessionNumber);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setStateLabId(stateLabId);
        setFacilityId(DataBaseUtil.trim(facilityId));
        setSampleType(DataBaseUtil.trim(sampleType));
        setSamplePointId(DataBaseUtil.trim(samplePointId));
        setLocation(DataBaseUtil.trim(location));
        setPwsId(DataBaseUtil.trim(pwsId));
        setPwsName(DataBaseUtil.trim(pwsName));
        setFieldOffice(DataBaseUtil.trim(fieldOffice));
        setOrganizationName(organizationName);
        setAnalysisId(analysisId);
        setSectionId(sectionId);
        setUnitOfMeasureId(unitOfMeasureId);
        setAnaStartedDate(DataBaseUtil.toYM(anaStartedDate));        
        setAnaReleasedDate(DataBaseUtil.toYM(anaReleasedDate));
        setUnitDescription(unitDescription);
        setTestName(testName);
        setMethodName(methodName);
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Datetime getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYD(collectionDate);
    }

    public Datetime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Datetime collectionTime) {
        this.collectionTime = DataBaseUtil.toHM(collectionTime);
    }

    public Integer getStateLabId() {
        return stateLabId;
    }

    public void setStateLabId(Integer stateLabId) {
        this.stateLabId = stateLabId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = DataBaseUtil.trim(facilityId);
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = DataBaseUtil.trim(sampleType);
    }

    public String getSamplePointId() {
        return samplePointId;
    }

    public void setSamplePointId(String samplePointId) {
        this.samplePointId = DataBaseUtil.trim(samplePointId);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public String getPwsId() {
        return pwsId;
    }

    public void setPwsId(String pwsId) {
        this.pwsId = DataBaseUtil.trim(pwsId);
    }

    public String getPwsName() {
        return pwsName;
    }

    public void setPwsName(String pwsName) {
        this.pwsName = DataBaseUtil.trim(pwsName);
    }

    public String getFieldOffice() {
        return fieldOffice;
    }

    public void setFieldOffice(String fieldOffice) {
        this.fieldOffice = DataBaseUtil.trim(fieldOffice);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public Datetime getAnaStartedDate() {
        return anaStartedDate;
    }

    public void setAnaStartedDate(Datetime anaStartedDate) {
        this.anaStartedDate = DataBaseUtil.toYM(anaStartedDate);
    }

    public Datetime getAnaReleasedDate() {
        return anaReleasedDate;
    }

    public void setAnaReleasedDate(Datetime anaReleasedDate) {
        this.anaReleasedDate = DataBaseUtil.toYM(anaReleasedDate);
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = DataBaseUtil.trim(unitDescription);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }
}
