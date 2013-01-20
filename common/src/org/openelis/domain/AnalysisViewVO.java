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

import java.io.Serializable;
import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
/** 
 * This class's objects store the data for the individual records that populate
 * the todo lists for analyses
 */
public class AnalysisViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer        sampleId, accessionNumber, timeHolding, timeTaAverage,
                              analysisId, priority, testId, analysisStatusId, sectionId,
                              unitOfMeasureId, worksheetFormatId;                                                            
    protected String         domain, primaryOrganizationName, testName, methodName,
                              todoDescription, worksheetDescription, analysisResultOverride,
                              sectionName;
    protected Datetime       availableDate, startedDate, completedDate, releasedDate,
                              receivedDate, collectionDate, collectionTime, enteredDate;

    public AnalysisViewVO() {        
    }
    
    public AnalysisViewVO(Integer sampleId, String domain, Integer accessionNumber,
                          Date receivedDate, Date collectionDate, Date collectionTime,
                          Date enteredDate, String primaryOrganizationName, String todoDescription, 
                          String worksheetDescription, Integer priority, Integer testId,
                          String testName, String methodName, Integer timeTaAverage,
                          Integer timeHolding, Integer analysisId, Integer analysisStatusId,
                          Integer sectionId, String sectionName, Date availableDate,
                          Date startedDate, Date completedDate, Date releasedDate,
                          String analysisResultOverride, Integer unitOfMeasureId,
                          Integer worksheetFormatId) {
        
        setSampleId(sampleId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setCollectionTime(DataBaseUtil.toHM(collectionTime));
        setEnteredDate(DataBaseUtil.toYM(enteredDate));
        setPrimaryOrganizationName(primaryOrganizationName);
        setToDoDescription(todoDescription);
        setWorksheetDescription(worksheetDescription);
        setPriority(priority);
        setTestId(testId);
        setTestName(testName);
        setMethodName(methodName);
        setTimeTaAverage(timeTaAverage);
        setTimeHolding(timeHolding);
        setAnalysisId(analysisId);
        setAnalysisStatusId(analysisStatusId);
        setSectionId(sectionId);
        setSectionName(sectionName);
        setAvailableDate(DataBaseUtil.toYM(availableDate));
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setReleasedDate(DataBaseUtil.toYM(releasedDate));
        setAnalysisResultOverride(analysisResultOverride);
        setUnitOfMeasureId(unitOfMeasureId);
        setWorksheetFormatId(worksheetFormatId);
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
    
    public void setEnteredDate(Datetime enteredDate) {
        this.enteredDate = DataBaseUtil.toYM(enteredDate);
    }
    
    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public void setPrimaryOrganizationName(String primaryOrganizationName) {
        this.primaryOrganizationName = DataBaseUtil.trim(primaryOrganizationName);
    }
    
    public String getToDoDescription() {
        return todoDescription;
    }

    public void setToDoDescription(String todoDescription) {
        this.todoDescription = DataBaseUtil.trim(todoDescription);
    }
    
    public String getWorksheetDescription() {
        return worksheetDescription;
    }

    public void setWorksheetDescription(String worksheetDescription) {
        this.worksheetDescription = DataBaseUtil.trim(worksheetDescription);
    }
    
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
    
    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }

    public void setAnalysisStatusId(Integer analysisStatusId) {
        this.analysisStatusId = analysisStatusId;
    }
    
    public Integer getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = DataBaseUtil.trim(sectionName);
    }   
    
    public Datetime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Datetime availableDate) {
        this.availableDate = DataBaseUtil.toYM(availableDate);
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
    
    public Datetime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Datetime releasedDate) {
        this.releasedDate = DataBaseUtil.toYM(releasedDate);
    }
    
    public String getAnalysisResultOverride() {
        return analysisResultOverride;
    }
    
    public void setAnalysisResultOverride(String analysisResultOverride) {
        this.analysisResultOverride = DataBaseUtil.trim(analysisResultOverride);
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
}