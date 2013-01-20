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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * The class is used to carry fields for the analysis records on SDWIS Unload report.
 * The fields are considered read/display and do not get committed to the database.
 */

public class SDWISUnloadReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         analysisId, sampleItemSequence, sectionId, sectionOrgId,
                              statusId, unitOfMeasureId;
    protected String          methodName, isReportable, testName;
    protected Datetime        completedDate, startedDate;

    public SDWISUnloadReportVO() {
    }

    public SDWISUnloadReportVO(Integer analysisId, Integer sampleItemSequence, String testName,
                               String methodName, Integer sectionId, Integer sectionOrgId,
                               String isReportable, Integer unitOfMeasureId, Integer statusId,
                               Date startedDate, Date completedDate) {
        setAnalysisId(analysisId);
        setSampleItemSequence(sampleItemSequence);
        setTestName(testName);
        setMethodName(methodName);
        setSectionId(sectionId);
        setSectionOrganizationId(sectionOrgId);
        setIsReportable(isReportable);
        setUnitOfMeasureId(unitOfMeasureId);
        setStatusId(statusId);
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getSampleItemSequence() {
        return sampleItemSequence;
    }

    public void setSampleItemSequence(Integer sampleItemSequence) {
        this.sampleItemSequence = sampleItemSequence;
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

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getSectionOrganizationId() {
        return sectionOrgId;
    }

    public void setSectionOrganizationId(Integer sectionOrgId) {
        this.sectionOrgId = sectionOrgId;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Datetime getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Datetime startedDate) {
        this.startedDate = DataBaseUtil.toYM(startedDate);
    }

    public Datetime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Datetime completedDate) {
        this.completedDate = DataBaseUtil.toYM(completedDate);
    }
}