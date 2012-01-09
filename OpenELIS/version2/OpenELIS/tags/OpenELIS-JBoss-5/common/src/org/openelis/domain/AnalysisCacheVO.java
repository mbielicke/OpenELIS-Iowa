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
/** 
 * This class's objects store the data for the individual records that populate
 * the caches used for the various todo lists  
 */
public class AnalysisCacheVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, statusId, sectionId, sampleId, sampleAccessionNumber,
                              testTimeHolding,testTimeTaAverage, sampleEnvironmentalPriority;                              
    protected String          sampleDomain, sampleReportToName, testName, testMethodName,
                              sectionName, analysisQaeventResultOverride, sampleQaeventResultOverride, 
                              sampleProjectName, samplePrivateWellOwner, sampleSDWISPWSName;
    protected Datetime        availableDate, startedDate, completedDate, releasedDate,
                              sampleReceivedDate, sampleCollectionDate,
                              sampleCollectionTime;
    
    public AnalysisCacheVO() {        
    }
    
    public AnalysisCacheVO(Integer id, Integer statusId, Integer sectionId,Date availableDate,
                           Date startedDate, Date completedDate, Date releasedDate, Integer sampleId,
                           String sampleDomain, Integer sampleAccessionNumber, Date sampleReceivedDate,
                           Date sampleCollectionDate, Date sampleCollectionTime, String testName,
                           Integer testTimeHolding, Integer testTimeTaAverage, String testMethodName) {
        setId(id);
        setStatusId(statusId);
        setSectionId(sectionId);
        setAvailableDate(DataBaseUtil.toYM(availableDate));
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setReleasedDate(DataBaseUtil.toYM(releasedDate));
        setSampleId(sampleId);
        setSampleDomain(sampleDomain);
        setSampleAccessionNumber(sampleAccessionNumber);
        setSampleReceivedDate(DataBaseUtil.toYM(sampleReceivedDate));
        setSampleCollectionDate(DataBaseUtil.toYD(sampleCollectionDate));
        setSampleCollectionTime(DataBaseUtil.toHM(sampleCollectionTime));
        setTestName(testName);
        setTestTimeHolding(testTimeHolding);
        setTestTimeTaAverage(testTimeTaAverage);
        setTestMethodName(testMethodName);
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Datetime getStartedDate() {
        return startedDate;
    }
    
    public Datetime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Datetime availableDate) {
        this.availableDate = DataBaseUtil.toYM(availableDate);
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
        
    public String getSampleDomain() {
        return sampleDomain;
    }
    
    public void setSampleDomain(String sampleDomain) {
        this.sampleDomain = DataBaseUtil.trim(sampleDomain);
    }
    
    public Integer getSampleAccessionNumber() {
        return sampleAccessionNumber;
    }
    
    public void setSampleAccessionNumber(Integer sampleAccessionNumber) {
        this.sampleAccessionNumber = sampleAccessionNumber;
    }
    
    public Datetime getSampleReceivedDate() {
        return sampleReceivedDate;
    }
    
    public void setSampleReceivedDate(Datetime sampleReceivedDate) {
        this.sampleReceivedDate = DataBaseUtil.toYM(sampleReceivedDate);
    }
    
    public Datetime getSampleCollectionDate() {
        return sampleCollectionDate;
    }
    
    public void setSampleCollectionDate(Datetime sampleCollectionDate) {
        this.sampleCollectionDate = DataBaseUtil.toYD(sampleCollectionDate);
    }
    
    public Datetime getSampleCollectionTime() {
        return sampleCollectionTime;
    }
    
    public void setSampleCollectionTime(Datetime sampleCollectionTime) {
        this.sampleCollectionTime = DataBaseUtil.toHM(sampleCollectionTime);
    }
        
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }
    
    public Integer getTestTimeHolding() {
        return testTimeHolding;
    }

    public void setTestTimeHolding(Integer testTimeHolding) {
        this.testTimeHolding = testTimeHolding;
    }

    public Integer getTestTimeTaAverage() {
        return testTimeTaAverage;
    }

    public void setTestTimeTaAverage(Integer testTimeTaAverage) {
        this.testTimeTaAverage = testTimeTaAverage;
    }
    
    public String getTestMethodName() {
        return testMethodName;
    }
    
    public void setTestMethodName(String testMethodName) {
        this.testMethodName = DataBaseUtil.trim(testMethodName);
    }
    
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = DataBaseUtil.trim(sectionName);
    }

    public String getAnalysisQaeventResultOverride() {
        return analysisQaeventResultOverride;
    }
    
    public void setAnalysisQaeventResultOverride(String analysisQaeventResultOverride) {
        this.analysisQaeventResultOverride = DataBaseUtil.trim(analysisQaeventResultOverride);
    }
    
    public String getSampleQaeventResultOverride() {
        return sampleQaeventResultOverride;
    }

    public void setSampleQaeventResultOverride(String sampleQaeventResultOverride) {
        this.sampleQaeventResultOverride = DataBaseUtil.trim(sampleQaeventResultOverride);
    }
    
    public String getSampleReportToName() {
        return sampleReportToName;
    }
    
    public void setSampleReportToName(String sampleReportToName) {
        this.sampleReportToName = DataBaseUtil.trim(sampleReportToName);
    }

    public Integer getSampleEnvironmentalPriority() {
        return sampleEnvironmentalPriority;
    }

    public void setSampleEnvironmentalPriority(Integer sampleEnvironmentalPriority) {
        this.sampleEnvironmentalPriority = sampleEnvironmentalPriority;
    }

    public String getSampleProjectName() {
        return sampleProjectName;
    }

    public void setSampleProjectName(String sampleProjectName) {
        this.sampleProjectName = DataBaseUtil.trim(sampleProjectName);
    }
    
    public String getSamplePrivateWellOwner() {
        return samplePrivateWellOwner;
    }

    public void setSamplePrivateWellOwner(String samplePrivateWellOwner) {
        this.samplePrivateWellOwner = DataBaseUtil.trim(samplePrivateWellOwner);
    }

    public String getSampleSDWISPWSName() {
        return sampleSDWISPWSName;
    }

    public void setSampleSDWISPWSName(String sampleSDWISPWSName) {
        this.sampleSDWISPWSName = DataBaseUtil.trim(sampleSDWISPWSName);
    }
}
