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
import java.sql.Time;
import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * The class is used to carry fields for the web based Final Report for SDWIS
 * domain. The fields are considered read/display and do not get committed to
 * the database.
 */

public class SampleStatusWebReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         accessionNumber, statusId, sampleId, analysisId;
    protected Datetime        collectionDate, collectionTime, receivedDate;
    protected String          collector, clientReference, testReportingDescription,
                              methodReportingDescription;
    protected QAEventType     sampleQA, analysisQA;

    public enum QAEventType {
        WARNING, OVERRIDE
    };

    public SampleStatusWebReportVO() {
    }

    public SampleStatusWebReportVO(Integer accessionNumber, Date receivedDate, Date collectionDate,
                                   Date collectionTime, Integer statusId, String clientReference,
                                   String collector, String testReportingDescription,
                                   String methodReportingDescription, Integer sampleId,
                                   Integer analysisId) {
        if (collectionTime instanceof Time)
            collectionTime = new Date(collectionTime.getTime());
        setAccessionNumber(accessionNumber);
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setStatusId(statusId);
        setClientReference(clientReference);
        setCollector(collector);
        setTestReportingDescription(testReportingDescription);
        setMethodReportingDescription(methodReportingDescription);
        setSampleId(sampleId);
        setAnalysisId(analysisId);
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public QAEventType getSampleQA() {
        return sampleQA;
    }

    public void setSampleQA(QAEventType sampleQA) {
        this.sampleQA = sampleQA;
    }

    public QAEventType getAnalysisQA() {
        return analysisQA;
    }

    public void setAnalysisQA(QAEventType analysisQA) {
        this.analysisQA = analysisQA;
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

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
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
