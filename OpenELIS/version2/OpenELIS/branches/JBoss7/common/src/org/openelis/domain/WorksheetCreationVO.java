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

public class WorksheetCreationVO implements RPC {
    private static final long serialVersionUID = 1L;

    protected Boolean  hasQaOverride;
    protected Integer  analysisId, accessionNumber, dueDays, preAnalysisId, priority,
                       privateWellOrgId, sampleId, sectionId, statusId, testId, 
                       timeHolding, timeTaAverage, typeOfSampleId, unitOfMeasureId,
                       worksheetFormatId;
    protected String   description, domain, envLocation, methodName, privateWellLocation, 
                       privateWellReportToName, sdwisLocation, testName;
    protected Datetime collectionDate, collectionTime, expireDate, receivedDate;

    public WorksheetCreationVO() {

    }

    public WorksheetCreationVO(Integer analysisId, Integer sampleId, String domain,
                               Integer accessionNumber, Date collectionDate, Date collectionTime,
                               Date receivedDate, String envLocation, Integer priority,
                               String sdwisLocation, String privateWellLocation,
                               Integer privateWellOrgId, String privateWellReportToName,
                               Integer typeOfSampleId, Integer testId, String testName,
                               String methodName, Integer timeHolding, Integer timeTaAverage,
                               Integer sectionId, Integer preAnalysisId, Integer statusId,
                               Integer unitOfMeasureId, Integer worksheetFormatId) {
        setAnalysisId(analysisId);
        setSampleId(sampleId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setEnvLocation(envLocation);
        setPriority(priority);
        setSDWISLocation(sdwisLocation);
        setPrivateWellLocation(privateWellLocation);
        setPrivateWellOrgId(privateWellOrgId);
        setPrivateWellReportToName(privateWellReportToName);
        setTestId(testId);
        setTestName(testName);
        setMethodName(methodName);
        setTimeHolding(timeHolding);
        setTimeTaAverage(timeTaAverage);
        setSectionId(sectionId);
        setPreAnalysisId(preAnalysisId);
        setStatusId(statusId);
        setTypeOfSampleId(typeOfSampleId);
        setUnitOfMeasureId(unitOfMeasureId);
        setWorksheetFormatId(worksheetFormatId);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = DataBaseUtil.trim(domain);
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

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
    }
    
    public String getEnvLocation() {
        return envLocation;
    }

    public void setEnvLocation(String envLocation) {
        this.envLocation = DataBaseUtil.trim(envLocation);
    }

    public String getSDWISLocation() {
        return sdwisLocation;
    }

    public void setSDWISLocation(String sdwisLocation) {
        this.sdwisLocation = DataBaseUtil.trim(sdwisLocation);
    }

    public String getPrivateWellLocation() {
        return privateWellLocation;
    }

    public void setPrivateWellLocation(String privateWellLocation) {
        this.privateWellLocation = DataBaseUtil.trim(privateWellLocation);
    }

    public Integer getPrivateWellOrgId() {
        return privateWellOrgId;
    }

    public void setPrivateWellOrgId(Integer privateWellOrgId) {
        this.privateWellOrgId = privateWellOrgId;
    }

    public String getPrivateWellReportToName() {
        return privateWellReportToName;
    }

    public void setPrivateWellReportToName(String privateWellReportToName) {
        this.privateWellReportToName = DataBaseUtil.trim(privateWellReportToName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
/*
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
*/
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
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

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public void setTimeHolding(Integer timeHolding) {
        this.timeHolding = timeHolding;
    }

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public void setTimeTaAverage(Integer timeTaAverage) {
        this.timeTaAverage = timeTaAverage;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getPreAnalysisId() {
        return preAnalysisId;
    }

    public void setPreAnalysisId(Integer preAnalysisId) {
        this.preAnalysisId = preAnalysisId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        this.typeOfSampleId = typeOfSampleId;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public Integer getWorksheetFormatId() {
        return worksheetFormatId;
    }

    public void setWorksheetFormatId(Integer worksheetFormatId) {
        this.worksheetFormatId = worksheetFormatId;
    }

    public Integer getDueDays() {
        return dueDays;
    }

    public void setDueDays(Integer dueDays) {
        this.dueDays = dueDays;
    }

    public Datetime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Datetime expireDate) {
        this.expireDate = DataBaseUtil.toYM(expireDate);
    }

    public Boolean getHasQaOverride() {
        return hasQaOverride;
    }

    public void setHasQaOverride(Boolean hasQaOverride) {
        this.hasQaOverride = hasQaOverride;
    }
}
