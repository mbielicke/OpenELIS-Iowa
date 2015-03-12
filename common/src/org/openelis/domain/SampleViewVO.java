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
import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * This class's objects store the data for the individual records that populate
 * the web lists for samples
 */
public class SampleViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, accessionNumber, sampleRevision, sampleStatusId,
                    reportToId, projectId, analysisId, analysisRevision, analysisStatusId;
    protected String          domain, clientReference, reportToName, collector, location,
                    locationCity, projectName, pwsNumber0, pwsName, sdwisFacilityId,
                    patientLastName, patientFirstName, providerName, analysisIsReportable,
                    testReportingDescription, methodReportingDescription;
    protected Datetime        receivedDate, collectionDate, collectionTime, sampleReleasedDate,
                    patientBirthDate;

    public SampleViewVO() {
    }

    public SampleViewVO(Integer sampleId, String domain, Integer accessionNumber,
                        Integer sampleRevision, Date receivedDate, Date collectionDate,
                        Date collectionTime, Integer sampleStatusId, String clientReference,
                        Date sampleReleasedDate, Integer reportToId, String reportToName,
                        String collector, String location, String locationCity, Integer projectId,
                        String projectName, String pwsNumber0, String pwsName,
                        String sdwisFacilityId, String patientLastName, String patientFirstName,
                        Date patientBirthDate, String providerName, Integer analysisId,
                        Integer analysisRevision, String analysisIsReportable,
                        Integer analysisStatusId, String testReportingDescription,
                        String methodReportingDescription) {
        setSampleId(sampleId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setSampleRevision(sampleRevision);
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setSampleStatusId(sampleStatusId);
        setClientReference(clientReference);
        setSampleReleasedDate(DataBaseUtil.toYM(sampleReleasedDate));
        setReportToId(reportToId);
        setReportToName(reportToName);
        setCollector(collector);
        setLocation(location);
        setLocationCity(locationCity);
        setProjectId(projectId);
        setProjectName(projectName);
        setPwsNumber0(pwsNumber0);
        setPwsName(pwsName);
        setSdwisFacilityId(sdwisFacilityId);
        setPatientLastName(patientLastName);
        setPatientFirstName(patientFirstName);
        setPatientBirthDate(DataBaseUtil.toYD(patientBirthDate));
        setProviderName(providerName);
        setAnalysisId(analysisId);
        setAnalysisRevision(analysisRevision);
        setAnalysisIsReportable(analysisIsReportable);
        setAnalysisStatusId(analysisStatusId);
        setTestReportingDescription(testReportingDescription);
        setMethodReportingDescription(methodReportingDescription);
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

    public Integer getSampleRevision() {
        return sampleRevision;
    }

    public void setSampleRevision(Integer sampleRevision) {
        this.sampleRevision = sampleRevision;
    }

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
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

    public Integer getSampleStatusId() {
        return sampleStatusId;
    }

    public void setSampleStatusId(Integer sampleStatusId) {
        this.sampleStatusId = sampleStatusId;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
    }

    public Datetime getSampleReleasedDate() {
        return sampleReleasedDate;
    }

    public void setSampleReleasedDate(Datetime sampleReleasedDate) {
        this.sampleReleasedDate = DataBaseUtil.toYM(sampleReleasedDate);
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public void setReportToId(Integer reportToId) {
        this.reportToId = reportToId;
    }

    public String getReportToName() {
        return reportToName;
    }

    public void setReportToName(String reportToName) {
        this.reportToName = DataBaseUtil.trim(reportToName);
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = DataBaseUtil.trim(locationCity);
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = DataBaseUtil.trim(projectName);
    }

    public String getPwsNumber0() {
        return pwsNumber0;
    }

    public void setPwsNumber0(String pwsNumber0) {
        this.pwsNumber0 = DataBaseUtil.trim(pwsNumber0);
    }

    public String getPwsName() {
        return pwsName;
    }

    public void setPwsName(String pwsName) {
        this.pwsName = DataBaseUtil.trim(pwsName);
    }

    public String getSdwisFacilityId() {
        return sdwisFacilityId;
    }

    public void setSdwisFacilityId(String sdwisFacilityId) {
        this.sdwisFacilityId = DataBaseUtil.trim(sdwisFacilityId);
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = DataBaseUtil.trim(patientLastName);
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = DataBaseUtil.trim(patientFirstName);
    }

    public Datetime getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientBirthDate(Datetime patientBirthDate) {
        this.patientBirthDate = DataBaseUtil.toYD(patientBirthDate);
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String provider) {
        this.providerName = DataBaseUtil.trim(provider);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getAnalysisRevision() {
        return analysisRevision;
    }

    public void setAnalysisRevision(Integer analysisRevision) {
        this.analysisRevision = analysisRevision;
    }

    public String getAnalysisIsReportable() {
        return analysisIsReportable;
    }

    public void setAnalysisIsReportable(String analysisIsReportable) {
        this.analysisIsReportable = DataBaseUtil.trim(analysisIsReportable);
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }

    public void setAnalysisStatusId(Integer analysisStatusId) {
        this.analysisStatusId = analysisStatusId;
    }

    public String getTestReportingDescription() {
        return testReportingDescription;
    }

    public void setTestReportingDescription(String testReportingDescription) {
        this.testReportingDescription = DataBaseUtil.trim(testReportingDescription);
    }

    public String getMethodReportingDescription() {
        return methodReportingDescription;
    }

    public void setMethodReportingDescription(String methodReportingDescription) {
        this.methodReportingDescription = DataBaseUtil.trim(methodReportingDescription);
    }
}