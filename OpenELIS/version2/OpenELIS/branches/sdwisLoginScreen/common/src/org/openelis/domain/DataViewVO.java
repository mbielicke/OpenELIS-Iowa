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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;

/**
 * 
 * This class is used to carry the data entered on Data dump screen to the
 * back-end and also between different screens
 */
public class DataViewVO implements Serializable {

    private static final long              serialVersionUID = 1L;

    protected String                       analysisId, analysisTestName, analysisTestMethodName,
                                           excludeResultOverride,
                                           clientReference, accessionNumber, revision,
                                           collectionDate, receivedDate, enteredDate,
                                           releasedDate, statusId, clientReferenceHeader,
                                           projectName, reportToOrganizationName,
                                           excludeResults, excludeAuxData, organizationId,
                                           organizationName, organizationAttention,
                                           organizationAddressMultipleUnit,
                                           organizationAddressAddress, organizationAddressCity,
                                           organizationAddressState, organizationAddressZipCode,
                                           sampleItemTypeofSampleId, sampleItemSourceOfSampleId, 
                                           sampleItemSourceOther, sampleItemContainerId,
                                           sampleItemContainerReference, sampleItemItemSequence,
                                           analysisTestNameHeader, analysisTestMethodNameHeader,
                                           analysisStatusIdHeader, analysisRevision,
                                           analysisIsReportable, analysisIsReportableHeader,
                                           analysisQaName, analysisCompletedDate,
                                           analysisCompletedBy, analysisReleasedDate,
                                           analysisReleasedBy, analysisStartedDate,
                                           analysisPrintedDate, analysisSectionName,
                                           analysisUnitOfMeasureId, sampleEnvironmentalIsHazardous,
                                           sampleEnvironmentalPriority, sampleEnvironmentalCollector,
                                           sampleEnvironmentalCollectorPhone, sampleEnvironmentalLocation,
                                           sampleEnvironmentalLocationAddressCity, 
                                           sampleEnvironmentalLocationAddressCityHeader,
                                           sampleEnvironmentalDescription, samplePrivateWellOwner, 
                                           samplePrivateWellCollector, samplePrivateWellWellNumber,
                                           samplePrivateWellReportToAddressWorkPhone,
                                           samplePrivateWellReportToAddressFaxPhone,
                                           samplePrivateWellLocation, 
                                           samplePrivateWellLocationAddressMultipleUnit,
                                           samplePrivateWellLocationAddressStreetAddress,
                                           samplePrivateWellLocationAddressCity,
                                           samplePrivateWellLocationAddressState,
                                           samplePrivateWellLocationAddressZipCode,
                                           sampleSDWISPwsId, sampleSDWISPwsName,
                                           sampleSDWISStateLabId, sampleSDWISFacilityId,
                                           sampleSDWISSampleTypeId, sampleSDWISSampleCategoryId,
                                           sampleSDWISSamplePointId, sampleSDWISLocation, sampleSDWISPriority,
                                           sampleSDWISCollector, sampleEnvironmentalCollectorHeader,
                                           sampleEnvironmentalLocationHeader;
    protected Integer                      analysisStatusId, accessionNumberFrom,
                                           accessionNumberTo, projectId;
    protected Date                         analysisCompletedDateFrom, analysisCompletedDateTo,
                                           analysisReleasedDateFrom, analysisReleasedDateTo, 
                                           collectionDateFrom, collectionDateTo, 
                                           receivedDateFrom, receivedDateTo, enteredDateFrom,
                                           enteredDateTo, releasedDateFrom, releasedDateTo;
    
    protected ArrayList<QueryData>             queryFields;
    protected ArrayList<TestAnalyteDataViewVO> testAnalytes;
    protected ArrayList<AuxFieldDataViewVO>    auxFields;
    
    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = DataBaseUtil.trim(analysisId);
    }
    
    public String getAnalysisTestName() {
        return analysisTestName;
    }

    public void setAnalysisTestName(String analysisTestName) {
        this.analysisTestName = DataBaseUtil.trim(analysisTestName);
    }

    public String getAnalysisTestMethodName() {
        return analysisTestMethodName;
    }

    public void setAnalysisTestMethodName(String analysisTestMethodName) {
        this.analysisTestMethodName = DataBaseUtil.trim(analysisTestMethodName);
    }

    public String getExcludeResults() {
        return excludeResults;
    }

    public void setExcludeResults(String excludeResults) {
        this.excludeResults = DataBaseUtil.trim(excludeResults);
    }
    
    public String getExcludeAuxData() {
        return excludeAuxData;
    }

    public void setExcludeAuxData(String excludeAuxData) {
        this.excludeAuxData = DataBaseUtil.trim(excludeAuxData);
    }
    
    public String getExcludeResultOverride() {
        return excludeResultOverride;
    }

    public void setExcludeResultOverride(String excludeResultOverride) {
        this.excludeResultOverride = DataBaseUtil.trim(excludeResultOverride);
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = DataBaseUtil.trim(accessionNumber);
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = DataBaseUtil.trim(revision);
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectedDate) {
        this.collectionDate = DataBaseUtil.trim(collectedDate);
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = DataBaseUtil.trim(receivedDate);
    }

    public String getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = DataBaseUtil.trim(enteredDate);
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = DataBaseUtil.trim(releasedDate);
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = DataBaseUtil.trim(statusId);
    }
    
    public String getClientReferenceHeader() {
        return clientReferenceHeader;
    }

    public void setClientReferenceHeader(String clientReferenceHeader) {
        this.clientReferenceHeader = DataBaseUtil.trim(clientReferenceHeader);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = DataBaseUtil.trim(projectName);
    }
    
    public String getReportToOrganizationName() {
        return reportToOrganizationName;
    }

    public void setReportToOrganizationName(String reportToOrganizationName) {
        this.reportToOrganizationName = DataBaseUtil.trim(reportToOrganizationName);
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = DataBaseUtil.trim(organizationId);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }

    public String getOrganizationAttention() {
        return organizationAttention;
    }

    public void setOrganizationAttention(String organizationAttention) {
        this.organizationAttention = DataBaseUtil.trim(organizationAttention);
    }

    public String getOrganizationAddressMultipleUnit() {
        return organizationAddressMultipleUnit;
    }

    public void setOrganizationAddressMultipleUnit(String organizationAddressMultipleUnit) {
        this.organizationAddressMultipleUnit = DataBaseUtil.trim(organizationAddressMultipleUnit);
    }

    public String getOrganizationAddressAddress() {
        return organizationAddressAddress;
    }

    public void setOrganizationAddressAddress(String organizationAddressAddress) {
        this.organizationAddressAddress = DataBaseUtil.trim(organizationAddressAddress);
    }

    public String getOrganizationAddressCity() {
        return organizationAddressCity;
    }

    public void setOrganizationAddressCity(String organizationAddressCity) {
        this.organizationAddressCity = DataBaseUtil.trim(organizationAddressCity);
    }

    public String getOrganizationAddressState() {
        return organizationAddressState;
    }

    public void setOrganizationAddressState(String organizationAddressState) {
        this.organizationAddressState = DataBaseUtil.trim(organizationAddressState);
    }

    public String getOrganizationAddressZipCode() {
        return organizationAddressZipCode;
    }

    public void setOrganizationAddressZipCode(String organizationAddressZipCode) {
        this.organizationAddressZipCode = DataBaseUtil.trim(organizationAddressZipCode);
    }

    public String getSampleItemTypeofSampleId() {
        return sampleItemTypeofSampleId;
    }

    public void setSampleItemTypeofSampleId(String sampleItemTypeofSampleId) {
        this.sampleItemTypeofSampleId = DataBaseUtil.trim(sampleItemTypeofSampleId);
    }

    public String getSampleItemSourceOfSampleId() {
        return sampleItemSourceOfSampleId;
    }

    public void setSampleItemSourceOfSampleId(String sampleItemSourceOfSampleId) {
        this.sampleItemSourceOfSampleId = DataBaseUtil.trim(sampleItemSourceOfSampleId);
    }

    public String getSampleItemSourceOther() {
        return sampleItemSourceOther;
    }

    public void setSampleItemSourceOther(String sampleItemSourceOther) {
        this.sampleItemSourceOther = DataBaseUtil.trim(sampleItemSourceOther);
    }

    public String getSampleItemContainerId() {
        return sampleItemContainerId;
    }

    public void setSampleItemContainerId(String sampleItemContainerId) {
        this.sampleItemContainerId = DataBaseUtil.trim(sampleItemContainerId);
    }

    public String getSampleItemContainerReference() {
        return sampleItemContainerReference;
    }

    public void setSampleItemContainerReference(String sampleItemContainerReference) {
        this.sampleItemContainerReference = DataBaseUtil.trim(sampleItemContainerReference);
    }

    public String getSampleItemItemSequence() {
        return sampleItemItemSequence;
    }

    public void setSampleItemItemSequence(String sampleItemItemSequence) {
        this.sampleItemItemSequence = DataBaseUtil.trim(sampleItemItemSequence);
    }

    public String getAnalysisTestNameHeader() {
        return analysisTestNameHeader;
    }

    public void setAnalysisTestNameHeader(String analysisTestNameHeader) {
        this.analysisTestNameHeader = DataBaseUtil.trim(analysisTestNameHeader);
    }

    public String getAnalysisTestMethodNameHeader() {
        return analysisTestMethodNameHeader;
    }

    public void setAnalysisTestMethodNameHeader(String analysisTestMethodNameHeader) {
        this.analysisTestMethodNameHeader = DataBaseUtil.trim(analysisTestMethodNameHeader);
    }

    public String getAnalysisStatusIdHeader() {
        return analysisStatusIdHeader;
    }

    public void setAnalysisStatusIdHeader(String analysisStatusIdHeader) {
        this.analysisStatusIdHeader = DataBaseUtil.trim(analysisStatusIdHeader);
    }

    public String getAnalysisRevision() {
        return analysisRevision;
    }

    public void setAnalysisRevision(String analysisRevision) {
        this.analysisRevision = DataBaseUtil.trim(analysisRevision);
    }
    
    public String getAnalysisIsReportable() {
        return analysisIsReportable;
    }

    public void setAnalysisIsReportable(String analysisIsReportable) {
        this.analysisIsReportable = DataBaseUtil.trim(analysisIsReportable);
    }
    
    public String getAnalysisIsReportableHeader() {
        return analysisIsReportableHeader;
    }

    public void setAnalysisIsReportableHeader(String analysisIsReportableHeader) {
        this.analysisIsReportableHeader = DataBaseUtil.trim(analysisIsReportableHeader);
    }

    public String getAnalysisQaName() {
        return analysisQaName;
    }

    public void setAnalysisQaName(String analysisQaName) {
        this.analysisQaName = DataBaseUtil.trim(analysisQaName);
    }

    public String getAnalysisCompletedDate() {
        return analysisCompletedDate;
    }

    public void setAnalysisCompletedDate(String analysisCompletedDate) {
        this.analysisCompletedDate = DataBaseUtil.trim(analysisCompletedDate);
    }

    public String getAnalysisCompletedBy() {
        return analysisCompletedBy;
    }

    public void setAnalysisCompletedBy(String analysisCompletedBy) {
        this.analysisCompletedBy = DataBaseUtil.trim(analysisCompletedBy);
    }

    public String getAnalysisReleasedDate() {
        return analysisReleasedDate;
    }

    public void setAnalysisReleasedDate(String analysisReleasedDate) {
        this.analysisReleasedDate = DataBaseUtil.trim(analysisReleasedDate);
    }

    public String getAnalysisReleasedBy() {
        return analysisReleasedBy;
    }

    public void setAnalysisReleasedBy(String analysisReleasedBy) {
        this.analysisReleasedBy = DataBaseUtil.trim(analysisReleasedBy);
    }

    public String getAnalysisStartedDate() {
        return analysisStartedDate;
    }

    public void setAnalysisStartedDate(String analysisStartedDate) {
        this.analysisStartedDate = DataBaseUtil.trim(analysisStartedDate);
    }

    public String getAnalysisPrintedDate() {
        return analysisPrintedDate;
    }

    public void setAnalysisPrintedDate(String analysisPrintedDate) {
        this.analysisPrintedDate = DataBaseUtil.trim(analysisPrintedDate);
    }
    
    public String getAnalysisUnitOfMeasureId() {
        return analysisUnitOfMeasureId;
    }

    public void setAnalysisUnitOfMeasureId(String analysisUnitOfMeasureId) {
        this.analysisUnitOfMeasureId = DataBaseUtil.trim(analysisUnitOfMeasureId);
    }
    
    public String getAnalysisSectionName() {
        return analysisSectionName;
    }

    public void setAnalysisSectionName(String analysisSectionName) {
        this.analysisSectionName = DataBaseUtil.trim(analysisSectionName);
    }
    
    public String getSampleEnvironmentalIsHazardous() {
        return sampleEnvironmentalIsHazardous;
    }

    public void setSampleEnvironmentalIsHazardous(String sampleEnvironmentalIsHazardous) {
        this.sampleEnvironmentalIsHazardous = DataBaseUtil.trim(sampleEnvironmentalIsHazardous);
    }

    public String getSampleEnvironmentalPriority() {
        return sampleEnvironmentalPriority;
    }

    public void setSampleEnvironmentalPriority(String sampleEnvironmentalPriority) {
        this.sampleEnvironmentalPriority = DataBaseUtil.trim(sampleEnvironmentalPriority);
    }

    public String getSampleEnvironmentalCollector() {
        return sampleEnvironmentalCollector;
    }

    public void setSampleEnvironmentalCollector(String sampleEnvironmentalCollector) {
        this.sampleEnvironmentalCollector = DataBaseUtil.trim(sampleEnvironmentalCollector);
    }

    public String getSampleEnvironmentalCollectorPhone() {
        return sampleEnvironmentalCollectorPhone;
    }

    public void setSampleEnvironmentalCollectorPhone(String sampleEnvironmentalCollectorPhone) {
        this.sampleEnvironmentalCollectorPhone = DataBaseUtil.trim(sampleEnvironmentalCollectorPhone);
    }

    public String getSampleEnvironmentalLocation() {
        return sampleEnvironmentalLocation;
    }

    public void setSampleEnvironmentalLocation(String sampleEnvironmentalLocation) {
        this.sampleEnvironmentalLocation = DataBaseUtil.trim(sampleEnvironmentalLocation);
    }

    public String getSampleEnvironmentalLocationAddressCity() {
        return sampleEnvironmentalLocationAddressCity;
    }

    public void setSampleEnvironmentalLocationAddressCity(String sampleEnvironmentalLocationAddressCity) {
        this.sampleEnvironmentalLocationAddressCity = DataBaseUtil.trim(sampleEnvironmentalLocationAddressCity);
    }
    
    public String getSampleEnvironmentalLocationAddressCityHeader() {
        return sampleEnvironmentalLocationAddressCityHeader;
    }

    public void setSampleEnvironmentalLocationAddressCityHeader(String sampleEnvironmentalLocationAddressCityHeader) {
        this.sampleEnvironmentalLocationAddressCityHeader = DataBaseUtil.trim(sampleEnvironmentalLocationAddressCityHeader);
    }

    public String getSampleEnvironmentalDescription() {
        return sampleEnvironmentalDescription;
    }

    public void setSampleEnvironmentalDescription(String sampleEnvironmentalDescription) {
        this.sampleEnvironmentalDescription = DataBaseUtil.trim(sampleEnvironmentalDescription);
    }

    public String getSamplePrivateWellOwner() {
        return samplePrivateWellOwner;
    }

    public void setSamplePrivateWellOwner(String samplePrivateWellOwner) {
        this.samplePrivateWellOwner = DataBaseUtil.trim(samplePrivateWellOwner);
    }

    public String getSamplePrivateWellCollector() {
        return samplePrivateWellCollector;
    }

    public void setSamplePrivateWellCollector(String samplePrivateWellCollector) {
        this.samplePrivateWellCollector = DataBaseUtil.trim(samplePrivateWellCollector);
    }

    public String getSamplePrivateWellWellNumber() {
        return samplePrivateWellWellNumber;
    }

    public void setSamplePrivateWellWellNumber(String samplePrivateWellWellNumber) {
        this.samplePrivateWellWellNumber = DataBaseUtil.trim(samplePrivateWellWellNumber);
    }

    public String getSamplePrivateWellReportToAddressWorkPhone() {
        return samplePrivateWellReportToAddressWorkPhone;
    }

    public void setSamplePrivateWellReportToAddressWorkPhone(String samplePrivateWellReportToAddressWorkPhone) {
        this.samplePrivateWellReportToAddressWorkPhone = DataBaseUtil.trim(samplePrivateWellReportToAddressWorkPhone);
    }

    public String getSamplePrivateWellReportToAddressFaxPhone() {
        return samplePrivateWellReportToAddressFaxPhone;
    }

    public void setSamplePrivateWellReportToAddressFaxPhone(String samplePrivateWellReportToAddressFaxPhone) {
        this.samplePrivateWellReportToAddressFaxPhone = DataBaseUtil.trim(samplePrivateWellReportToAddressFaxPhone);
    }

    public String getSamplePrivateWellLocation() {
        return samplePrivateWellLocation;
    }

    public void setSamplePrivateWellLocation(String samplePrivateWellLocation) {
        this.samplePrivateWellLocation = DataBaseUtil.trim(samplePrivateWellLocation);
    }

    public String getSamplePrivateWellLocationAddressMultipleUnit() {
        return samplePrivateWellLocationAddressMultipleUnit;
    }

    public void setSamplePrivateWellLocationAddressMultipleUnit(String samplePrivateWellLocationAddressMultipleUnit) {
        this.samplePrivateWellLocationAddressMultipleUnit = DataBaseUtil.trim(samplePrivateWellLocationAddressMultipleUnit);
    }

    public String getSamplePrivateWellLocationAddressStreetAddress() {
        return samplePrivateWellLocationAddressStreetAddress;
    }

    public void setSamplePrivateWellLocationAddressStreetAddress(String samplePrivateWellLocationAddressStreetAddress) {
        this.samplePrivateWellLocationAddressStreetAddress = DataBaseUtil.trim(samplePrivateWellLocationAddressStreetAddress);
    }
    
    public String getSamplePrivateWellLocationAddressCity() {
        return samplePrivateWellLocationAddressCity;
    }

    public void setSamplePrivateWellLocationAddressCity(String samplePrivateWellLocationAddressCity) {
        this.samplePrivateWellLocationAddressCity = DataBaseUtil.trim(samplePrivateWellLocationAddressCity);
    }

    public String getSamplePrivateWellLocationAddressState() {
        return samplePrivateWellLocationAddressState;
    }

    public void setSamplePrivateWellLocationAddressState(String samplePrivateWellLocationAddressState) {
        this.samplePrivateWellLocationAddressState = DataBaseUtil.trim(samplePrivateWellLocationAddressState);
    }

    public String getSamplePrivateWellLocationAddressZipCode() {
        return samplePrivateWellLocationAddressZipCode;
    }

    public void setSamplePrivateWellLocationAddressZipCode(String samplePrivateWellLocationAddressZipCode) {
        this.samplePrivateWellLocationAddressZipCode = DataBaseUtil.trim(samplePrivateWellLocationAddressZipCode);
    }

    public String getSampleSDWISPwsId() {
        return sampleSDWISPwsId;
    }

    public void setSampleSDWISPwsId(String sampleSDWISPwsId) {
        this.sampleSDWISPwsId = DataBaseUtil.trim(sampleSDWISPwsId);
    }

    public String getSampleSDWISPwsName() {
        return sampleSDWISPwsName;
    }

    public void setSampleSDWISPwsName(String sampleSDWISPwsName) {
        this.sampleSDWISPwsName = DataBaseUtil.trim(sampleSDWISPwsName);
    }

    public String getSampleSDWISStateLabId() {
        return sampleSDWISStateLabId;
    }

    public void setSampleSDWISStateLabId(String sampleSDWISStateLabId) {
        this.sampleSDWISStateLabId = DataBaseUtil.trim(sampleSDWISStateLabId);
    }

    public String getSampleSDWISFacilityId() {
        return sampleSDWISFacilityId;
    }

    public void setSampleSDWISFacilityId(String sampleSDWISFacilityId) {
        this.sampleSDWISFacilityId = DataBaseUtil.trim(sampleSDWISFacilityId);
    }

    public String getSampleSDWISSampleTypeId() {
        return sampleSDWISSampleTypeId;
    }

    public void setSampleSDWISSampleTypeId(String sampleSDWISSampleTypeId) {
        this.sampleSDWISSampleTypeId = DataBaseUtil.trim(sampleSDWISSampleTypeId);
    }

    public String getSampleSDWISSampleCategoryId() {
        return sampleSDWISSampleCategoryId;
    }

    public void setSampleSDWISSampleCategoryId(String sampleSDWISSampleCategoryId) {
        this.sampleSDWISSampleCategoryId = DataBaseUtil.trim(sampleSDWISSampleCategoryId);
    }

    public String getSampleSDWISSamplePointId() {
        return sampleSDWISSamplePointId;
    }

    public void setSampleSDWISSamplePointId(String sampleSDWISSamplePointId) {
        this.sampleSDWISSamplePointId = DataBaseUtil.trim(sampleSDWISSamplePointId);
    }
    
    public String getSampleSDWISLocation() {
        return sampleSDWISLocation;
    }

    public void setSampleSDWISLocation(String sampleSDWISLocation) {
        this.sampleSDWISLocation = sampleSDWISLocation;
    }

    public String getSampleSDWISPriority() {
        return sampleSDWISPriority;
    }

    public void setSampleSDWISPriority(String sampleSDWISPriority) {
        this.sampleSDWISPriority = DataBaseUtil.trim(sampleSDWISPriority);
    }

    public String getSampleSDWISCollector() {
        return sampleSDWISCollector;
    }

    public void setSampleSDWISCollector(String sampleSDWISCollector) {
        this.sampleSDWISCollector = DataBaseUtil.trim(sampleSDWISCollector);
    }

    public String getSampleEnvironmentalCollectorHeader() {
        return sampleEnvironmentalCollectorHeader;
    }

    public void setSampleEnvironmentalCollectorHeader(String collectorHeader) {
        this.sampleEnvironmentalCollectorHeader = DataBaseUtil.trim(collectorHeader);
    }
    
    public String getSampleEnvironmentalLocationHeader() {
        return sampleEnvironmentalLocationHeader;
    }

    public void setSampleEnvironmentalLocationHeader(String locationHeader) {
        this.sampleEnvironmentalLocationHeader = DataBaseUtil.trim(locationHeader);
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }

    public void setAnalysisStatusId(Integer analysisStatusId) {
        this.analysisStatusId = analysisStatusId;
    }

    public Integer getAccessionNumberFrom() {
        return accessionNumberFrom;
    }

    public void setAccessionNumberFrom(Integer accessionNumberFrom) {
        this.accessionNumberFrom = accessionNumberFrom;
    }

    public Integer getAccessionNumberTo() {
        return accessionNumberTo;
    }

    public void setAccessionNumberTo(Integer accessionNumberTo) {
        this.accessionNumberTo = accessionNumberTo;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Date getAnalysisCompletedDateFrom() {
        return analysisCompletedDateFrom;
    }

    public void setAnalysisCompletedDateFrom(Date analysisCompletedDateFrom) {
        this.analysisCompletedDateFrom = analysisCompletedDateFrom;
    }

    public Date getAnalysisCompletedDateTo() {
        return analysisCompletedDateTo;
    }

    public void setAnalysisCompletedDateTo(Date analysisCompletedDateTo) {
        this.analysisCompletedDateTo = analysisCompletedDateTo;
    }

    public Date getAnalysisReleasedDateFrom() {
        return analysisReleasedDateFrom;
    }

    public void setAnalysisReleasedDateFrom(Date analysisReleasedDateFrom) {
        this.analysisReleasedDateFrom = analysisReleasedDateFrom;
    }

    public Date getAnalysisReleasedDateTo() {
        return analysisReleasedDateTo;
    }

    public void setAnalysisReleasedDateTo(Date analysisReleasedDateTo) {
        this.analysisReleasedDateTo = analysisReleasedDateTo;
    }

    public Date getCollectionDateFrom() {
        return collectionDateFrom;
    }

    public void setCollectionDateFrom(Date collectionDateFrom) {
        this.collectionDateFrom = collectionDateFrom;
    }

    public Date getCollectionDateTo() {
        return collectionDateTo;
    }

    public void setCollectionDateTo(Date collectionDateTo) {
        this.collectionDateTo = collectionDateTo;
    }

    public Date getReceivedDateFrom() {
        return receivedDateFrom;
    }

    public void setReceivedDateFrom(Date receivedDateFrom) {
        this.receivedDateFrom = receivedDateFrom;
    }

    public Date getReceivedDateTo() {
        return receivedDateTo;
    }

    public void setReceivedDateTo(Date receivedDateTo) {
        this.receivedDateTo = receivedDateTo;
    }

    public Date getEnteredDateFrom() {
        return enteredDateFrom;
    }

    public void setEnteredDateFrom(Date enteredDateFrom) {
        this.enteredDateFrom = enteredDateFrom;
    }

    public Date getEnteredDateTo() {
        return enteredDateTo;
    }

    public void setEnteredDateTo(Date enteredDateTo) {
        this.enteredDateTo = enteredDateTo;
    }    
       
    public Date getReleasedDateFrom() {
        return releasedDateFrom;
    }

    public void setReleasedDateFrom(Date releasedDateFrom) {
        this.releasedDateFrom = releasedDateFrom;
    }
    
    public Date getReleasedDateTo() {
        return releasedDateTo;
    }

    public void setReleasedDateTo(Date releasedDateTo) {
        this.releasedDateTo = releasedDateTo;
    }
    
    public ArrayList<QueryData> getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(ArrayList<QueryData> queryFields) {
        this.queryFields = queryFields;
    }

    public ArrayList<TestAnalyteDataViewVO> getTestAnalytes() {
        return testAnalytes;
    }

    public void setAnalytes(ArrayList<TestAnalyteDataViewVO> testAnalytes) {
        this.testAnalytes = testAnalytes;
    }

    public ArrayList<AuxFieldDataViewVO> getAuxFields() {
        return auxFields;
    }

    public void setAuxFields(ArrayList<AuxFieldDataViewVO> auxFields) {
        this.auxFields = auxFields;
    }   
}