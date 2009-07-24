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

import org.openelis.gwt.common.Datetime;

public class AnalysisDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Integer id;             

    protected Integer sampleItemId;             

    protected Integer revision;             

    protected Integer testId;             

    protected Integer sectionId;             

    protected Integer preAnalysisId;             

    protected Integer parentAnalysisId;             

    protected Integer parentResultId;             

    protected String isReportable;             

    protected Integer unitOfMeasureId;             

    protected Integer statusId;             

    protected Datetime availableDate;             

    protected Datetime startedDate;             

    protected Datetime completedDate;             

    protected Datetime releasedDate;             

    protected Datetime printedDate;  
    
    private Boolean delete = false;
    
    public AnalysisDO() {
        
    }

    public AnalysisDO(Integer id,Integer sampleItemId,Integer revision,
                      Integer testId,Integer sectionId,Integer preAnalysisId,
                      Integer parentAnalysisId,Integer parentResultId,
                      String isReportable,Integer unitOfMeasureId,
                      Integer statusId,Date availableDate,
                      Date startedDate,Date completedDate,
                      Date releasedDate,Date printedDate) {
                                                                                
        this.id = id;
        this.sampleItemId = sampleItemId;
        this.revision = revision;
        this.testId = testId;
        this.sectionId = sectionId;
        this.preAnalysisId = preAnalysisId;
        this.parentAnalysisId = parentAnalysisId;
        this.parentResultId = parentResultId;
        this.isReportable = isReportable;
        this.unitOfMeasureId = unitOfMeasureId;
        this.statusId = statusId;
        setAvailableDate(availableDate);
        setStartedDate(startedDate);
        setCompletedDate(completedDate);
        setReleasedDate(releasedDate);
        setPrintedDate(printedDate);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getPreAnalysisId() {
        return preAnalysisId;
    }

    public void setPreAnalysisId(Integer preAnalysisId) {
        this.preAnalysisId = preAnalysisId;
    }

    public Integer getParentAnalysisId() {
        return parentAnalysisId;
    }

    public void setParentAnalysisId(Integer parentAnalysisId) {
        this.parentAnalysisId = parentAnalysisId;
    }

    public Integer getParentResultId() {
        return parentResultId;
    }

    public void setParentResultId(Integer parentResultId) {
        this.parentResultId = parentResultId;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
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

    public Datetime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Date availableDate) {
        this.availableDate = new Datetime(Datetime.YEAR,Datetime.DAY,availableDate);
    }

    public Datetime getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = new Datetime(Datetime.YEAR,Datetime.DAY,startedDate);
    }
    
    public void setStartedDate(Datetime startedDate) {
    	setStartedDate(startedDate.getDate());
    }

    public Datetime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = new Datetime(Datetime.YEAR,Datetime.DAY,completedDate);
    }

    public Datetime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = new Datetime(Datetime.YEAR,Datetime.DAY,releasedDate);
    }

    public Datetime getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(Date printedDate) {
        this.printedDate = new Datetime(Datetime.YEAR,Datetime.DAY,printedDate);
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }
    
    
}
