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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * The class is used to carry fields for the client notification received
 * report. The fields are considered read/display and do not get committed to
 * the database.
 */

public class ClientNotificationVO implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Integer         accessionNumber, analysisId, analysisQaeventId, sampleQaeventId;
    protected Datetime        collectionDate, collectionTime, receivedDate;
    protected String          email, projectName, providerLastName, referenceField1,
                              referenceField2, referenceField3;

    public ClientNotificationVO() {
    }

    public ClientNotificationVO(Integer accessionNumber, Date collectionDate, Date collectionTime,
                                Date receivedDate, String email, Integer sampleQaeventId,
                                Integer analysisQaeventId, String referenceField1,
                                String referenceField2, String referenceField3, String projectName,
                                String providerLastName, Integer analysisId) {
        if (collectionTime instanceof Time)
            collectionTime = new Date(collectionTime.getTime());
        setAccessionNumber(accessionNumber);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setEmail(email);
        setSampleQaeventId(sampleQaeventId);
        setAnalysisQaeventId(analysisQaeventId);
        setReferenceField1(referenceField1);
        setReferenceField2(referenceField2);
        setReferenceField3(referenceField3);
        setProjectName(projectName);
        setProviderLastName(providerLastName);
        setAnalysisId(analysisId);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = DataBaseUtil.trim(email);
    }

    public Integer getSampleQaeventId() {
        return sampleQaeventId;
    }

    public void setSampleQaeventId(Integer sampleQaeventId) {
        this.sampleQaeventId = sampleQaeventId;
    }

    public Integer getAnalysisQaeventId() {
        return analysisQaeventId;
    }

    public void setAnalysisQaeventId(Integer analysisQaeventId) {
        this.analysisQaeventId = analysisQaeventId;
    }

    public String getReferenceField1() {
        return referenceField1;
    }

    public void setReferenceField1(String referenceField1) {
        this.referenceField1 = DataBaseUtil.trim(referenceField1);
    }

    public String getReferenceField2() {
        return referenceField2;
    }

    public void setReferenceField2(String referenceField2) {
        this.referenceField2 = DataBaseUtil.trim(referenceField2);
    }

    public String getReferenceField3() {
        return referenceField3;
    }

    public void setReferenceField3(String referenceField3) {
        this.referenceField3 = DataBaseUtil.trim(referenceField3);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = DataBaseUtil.trim(projectName);
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = DataBaseUtil.trim(providerLastName);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }
}