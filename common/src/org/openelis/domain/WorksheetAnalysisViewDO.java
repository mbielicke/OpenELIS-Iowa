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

public class WorksheetAnalysisViewDO extends WorksheetAnalysisDO {

    private static final long serialVersionUID = 1L;

    protected Integer  dueDays, statusId, testId, unitOfMeasureId, worksheetId;
    protected String   description, methodName, testName, unitOfMeasure;
    protected Datetime collectionDate, expireDate, receivedDate;
    
    public WorksheetAnalysisViewDO() {
    }

    public WorksheetAnalysisViewDO(Integer id, Integer worksheetItemId, Integer worksheetId,
                                   String accessionNumber, Integer analysisId, Integer qcLotId,
                                   Integer worksheetAnalysisId, Integer qcSystemUserId,
                                   Date qcStartedDate, String isFromOther, String description,
                                   Integer testId, String testName, String methodName,
                                   Integer unitOfMeasureId, String unitOfMeasure,
                                   Integer statusId, Date collectionDate, Date receivedDate,
                                   Integer dueDays, Date expireDate) {
        super(id, worksheetItemId, accessionNumber, analysisId, qcLotId, worksheetAnalysisId,
              qcSystemUserId, qcStartedDate, isFromOther);
        setWorksheetId(worksheetId);
        setDescription(description);
        setTestId(testId);
        setTestName(testName);
        setMethodName(methodName);
        setUnitOfMeasureId(unitOfMeasureId);
        setUnitOfMeasure(unitOfMeasure);
        setStatusId(statusId);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setDueDays(dueDays);
        setExpireDate(DataBaseUtil.toYM(expireDate));
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Datetime getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Datetime collectionDate) {
        this.collectionDate = DataBaseUtil.toYD(collectionDate);
    }

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
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
}