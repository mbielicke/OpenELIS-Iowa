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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetAnalysisViewVO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer  analysisId, analysisStatusId, analysisTypeId, changeFlagsId,
                       dueDays, id, formatId, fromOtherId, priority, qcId, qcLotId,
                       testId, timeHolding, timeTaAverage, unitOfMeasureId, worksheetAnalysisId,
                       worksheetId, worksheetItemId;
    protected String   accessionNumber, description, methodName, sectionName, systemUsers,
                       testName, unitOfMeasure, worksheetDescription;
    protected Datetime collectionDate, collectionTime, expireDate, receivedDate,
                       startedDate, completedDate;
    
    public WorksheetAnalysisViewVO() {
    }

    public WorksheetAnalysisViewVO(Integer id, Integer worksheetItemId, Integer worksheetId,
                                   Integer formatId, String worksheetDescription,
                                   String accessionNumber, Integer analysisId, Integer qcLotId,
                                   Integer qcId, Integer worksheetAnalysisId, String systemUsers,
                                   Date startedDate, Date completedDate, Integer fromOtherId,
                                   Integer changeFlagsId, String description, Integer testId,
                                   String testName, String methodName, Integer timeTaAverage,
                                   Integer timeHolding, String sectionName, Integer unitOfMeasureId,
                                   String unitOfMeasure, Integer analysisStatusId,
                                   Integer analysisTypeId, Date collectionDate,
                                   Date collectionTime, Date receivedDate, Integer priority) {
        setId(id);
        setWorksheetItemId(worksheetItemId);
        setWorksheetId(worksheetId);
        setFormatId(formatId);
        setWorksheetDescription(worksheetDescription);
        setAccessionNumber(accessionNumber);
        setAnalysisId(analysisId);
        setQcLotId(qcLotId);
        setQcId(qcId);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setSystemUsers(systemUsers);
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setFromOtherId(fromOtherId);
        setChangeFlagsId(changeFlagsId);
        setDescription(description);
        setTestId(testId);
        setTestName(testName);
        setMethodName(methodName);
        setTimeTaAverage(timeTaAverage);
        setTimeHolding(timeHolding);
        setSectionName(sectionName);
        setUnitOfMeasureId(unitOfMeasureId);
        setUnitOfMeasure(unitOfMeasure);
        setAnalysisStatusId(analysisStatusId);
        setAnalysisTypeId(analysisTypeId);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setPriority(priority);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    public void setWorksheetItemId(Integer worksheetItemId) {
        this.worksheetItemId = worksheetItemId;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
    }

    public Integer getFormatId() {
        return formatId;
    }

    public void setFormatId(Integer formatId) {
        this.formatId = formatId;
    }

    public String getWorksheetDescription() {
        return worksheetDescription;
    }

    public void setWorksheetDescription(String worksheetDescription) {
        this.worksheetDescription = DataBaseUtil.trim(worksheetDescription);
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = DataBaseUtil.trim(accessionNumber);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getQcLotId() {
        return qcLotId;
    }

    public void setQcLotId(Integer qcLotId) {
        this.qcLotId = qcLotId;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        this.qcId = qcId;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
    }

    public String getSystemUsers() {
        return systemUsers;
    }

    public void setSystemUsers(String systemUsers) {
        this.systemUsers = DataBaseUtil.trim(systemUsers);
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

    public Integer getFromOtherId() {
        return fromOtherId;
    }

    public void setFromOtherId(Integer fromOtherId) {
        this.fromOtherId = fromOtherId;
    }

    public Integer getChangeFlagsId() {
        return changeFlagsId;
    }

    public void setChangeFlagsId(Integer changeFlagsId) {
        this.changeFlagsId = changeFlagsId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

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

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public void setTimeTaAverage(Integer timeTaAverage) {
        this.timeTaAverage = timeTaAverage;
    }

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public void setTimeHolding(Integer timeHolding) {
        this.timeHolding = timeHolding;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = DataBaseUtil.trim(sectionName);
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = DataBaseUtil.trim(unitOfMeasure);
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }

    public void setAnalysisStatusId(Integer analysisStatusId) {
        this.analysisStatusId = analysisStatusId;
    }

    public Integer getAnalysisTypeId() {
        return analysisTypeId;
    }

    public void setAnalysisTypeId(Integer analysisTypeId) {
        this.analysisTypeId = analysisTypeId;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}