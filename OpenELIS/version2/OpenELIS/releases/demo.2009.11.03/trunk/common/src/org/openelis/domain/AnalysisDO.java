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

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table analysis.  
 */

public class AnalysisDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sampleItemId, revision, testId, sectionId, preAnalysisId,
                              parentAnalysisId, parentResultId, unitOfMeasureId, statusId;
    protected String          isReportable;
    protected Datetime        availableDate, startedDate, completedDate, releasedDate, printedDate;

    public AnalysisDO() {
    }

    public AnalysisDO(Integer id, Integer sampleItemId, Integer revision, Integer testId,
                      Integer sectionId, Integer preAnalysisId, Integer parentAnalysisId,
                      Integer parentResultId, String isReportable, Integer unitOfMeasureId,
                      Integer statusId, Date availableDate, Date startedDate, Date completedDate,
                      Date releasedDate, Date printedDate) {
        setId(id);
        setSampleItemId(sampleItemId);
        setRevision(revision);
        setTestId(testId);
        setSectionId(sectionId);
        setPreAnalysisId(preAnalysisId);
        setParentAnalysisId(parentAnalysisId);
        setParentResultId(parentResultId);
        setIsReportable(isReportable);
        setUnitOfMeasureId(unitOfMeasureId);
        setStatusId(statusId);
        setAvailableDate(DataBaseUtil.toYM(availableDate));
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setReleasedDate(DataBaseUtil.toYM(releasedDate));
        setPrintedDate(DataBaseUtil.toYM(printedDate));
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
        _changed = true;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
        _changed = true;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
        _changed = true;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
        _changed = true;
    }

    public Integer getPreAnalysisId() {
        return preAnalysisId;
    }

    public void setPreAnalysisId(Integer preAnalysisId) {
        this.preAnalysisId = preAnalysisId;
        _changed = true;
    }

    public Integer getParentAnalysisId() {
        return parentAnalysisId;
    }

    public void setParentAnalysisId(Integer parentAnalysisId) {
        this.parentAnalysisId = parentAnalysisId;
        _changed = true;
    }

    public Integer getParentResultId() {
        return parentResultId;
    }

    public void setParentResultId(Integer parentResultId) {
        this.parentResultId = parentResultId;
        _changed = true;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
        _changed = true;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
        _changed = true;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }

    public Datetime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Datetime availableDate) {
        this.availableDate = DataBaseUtil.toYM(availableDate);
        _changed = true;
    }

    public Datetime getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Datetime startedDate) {
        this.startedDate = DataBaseUtil.toYM(startedDate);
        _changed = true;
    }

    public Datetime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Datetime completedDate) {
        this.completedDate = DataBaseUtil.toYM(completedDate);
        _changed = true;
    }

    public Datetime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Datetime releasedDate) {
        this.releasedDate = DataBaseUtil.toYM(releasedDate);
        _changed = true;
    }

    public Datetime getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(Datetime printedDate) {
        this.printedDate = DataBaseUtil.toYM(printedDate);
        _changed = true;
    }
}