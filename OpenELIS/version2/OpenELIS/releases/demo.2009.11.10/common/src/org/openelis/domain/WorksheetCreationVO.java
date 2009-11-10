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

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class WorksheetCreationVO implements RPC {
    private static final long serialVersionUID = 1L;

    protected Integer  analysisId, accessionNumber, priority, testId, timeHolding,
                       timeTaAverage, preAnalysisId, statusId;
    protected Long     dueDays;
    protected String   domain, envDescription, /*projectName,*/ testName, methodName,
                       sectionName;
    protected Datetime collectionDate, receivedDate, expireDate;

    public WorksheetCreationVO() {

    }

    // analysis, accession number, test, method, section, status and received
    public WorksheetCreationVO(Integer analysisId, String domain, Integer accessionNumber,
                               Date collectionDate, Date receivedDate, String description,
                               Integer priority, /*String projectName,*/ Integer testId, String testName,
                               String methodName, Integer timeHolding, Integer timeTaAverage,
                               String sectionName, Integer preAnalysisId, Integer statusId) {
        setAnalysisId(analysisId);
        setDomain(domain);
        setAccessionNumber(accessionNumber);
        setCollectionDate(DataBaseUtil.toYD(collectionDate));
        setReceivedDate(DataBaseUtil.toYM(receivedDate));
        setEnvDescription(description);
        setPriority(priority);
//        setProjectName(projectName);
        setTestId(testId);
        setTestName(testName);
        setMethodName(methodName);
        setTimeHolding(timeHolding);
        setTimeTaAverage(timeTaAverage);
        setSectionName(sectionName);
        setPreAnalysisId(preAnalysisId);
        setStatusId(statusId);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYM(receivedDate);
    }
    
    public String getEnvDescription() {
        return envDescription;
    }

    public void setEnvDescription(String description) {
        this.envDescription = description;
    }

    public String getDescription() {
        String description;
        
        description = "";
        if ("E".equals(getDomain()))
            description = getEnvDescription();
//        else if ("C".equals(getDomain()))
//            description = getLastName()+", "+getFirstName();
        
        return description;
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
        this.testName = testName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
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

    public Long getDueDays() {
        return dueDays;
    }

    public void setDueDays(Long dueDays) {
        this.dueDays = dueDays;
    }

    public Datetime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Datetime expireDate) {
        this.expireDate = expireDate;
    }
}
