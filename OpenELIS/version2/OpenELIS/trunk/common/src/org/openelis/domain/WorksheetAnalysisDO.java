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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetAnalysisDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer  id, worksheetItemId, worksheetAnalysisId, analysisId, qcId,
                       qcSystemUserId;
    protected String   accessionNumber;
    protected Datetime qcStartedDate;
    
    public WorksheetAnalysisDO() {
    }

    public WorksheetAnalysisDO(Integer id, Integer worksheetItemId, String accessionNumber,
                               Integer analysisId, Integer qcId, Integer worksheetAnalysisId,
                               Integer qcSystemUserId, Date qcStartedDate) {
        setId(id);
        setWorksheetItemId(worksheetItemId);
        setAccessionNumber(accessionNumber);
        setAnalysisId(analysisId);
        setQcId(qcId);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setQcSystemUserId(qcSystemUserId);
        setQcStartedDate(DataBaseUtil.toYM(qcStartedDate));
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    public void setWorksheetItemId(Integer worksheetItemId) {
        this.worksheetItemId = worksheetItemId;
        _changed = true;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = DataBaseUtil.trim(accessionNumber);
        _changed = true;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
        _changed = true;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        this.qcId = qcId;
        _changed = true;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
        _changed = true;
    }

    public Integer getQcSystemUserId() {
        return qcSystemUserId;
    }

    public void setQcSystemUserId(Integer qcSystemUserId) {
        this.qcSystemUserId = qcSystemUserId;
        _changed = true;
    }

    public Datetime getQcStartedDate() {
        return qcStartedDate;
    }

    public void setQcStartedDate(Datetime qcStartedDate) {
        this.qcStartedDate = DataBaseUtil.toYM(qcStartedDate);
        _changed = true;
    }
}