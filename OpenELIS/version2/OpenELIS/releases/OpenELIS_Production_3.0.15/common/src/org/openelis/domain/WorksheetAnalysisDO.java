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

public class WorksheetAnalysisDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer  id, worksheetItemId, worksheetAnalysisId, analysisId, qcLotId,
                       fromOtherId, changeFlagsId;
    protected String   accessionNumber, systemUsers;
    protected Datetime startedDate, completedDate;
    
    public WorksheetAnalysisDO() {
    }

    public WorksheetAnalysisDO(Integer id, Integer worksheetItemId, String accessionNumber,
                               Integer analysisId, Integer qcLotId, Integer worksheetAnalysisId,
                               String systemUsers, Date startedDate, Date completedDate,
                               Integer fromOtherId, Integer changeFlagsId) {
        setId(id);
        setWorksheetItemId(worksheetItemId);
        setAccessionNumber(accessionNumber);
        setAnalysisId(analysisId);
        setQcLotId(qcLotId);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setSystemUsers(systemUsers);
        setStartedDate(DataBaseUtil.toYM(startedDate));
        setCompletedDate(DataBaseUtil.toYM(completedDate));
        setFromOtherId(fromOtherId);
        setChangeFlagsId(changeFlagsId);
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

    public Integer getQcLotId() {
        return qcLotId;
    }

    public void setQcLotId(Integer qcLotId) {
        this.qcLotId = qcLotId;
        _changed = true;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
        _changed = true;
    }

    public String getSystemUsers() {
        return systemUsers;
    }

    public void setSystemUsers(String systemUsers) {
        this.systemUsers = DataBaseUtil.trim(systemUsers);
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

    public Integer getFromOtherId() {
        return fromOtherId;
    }

    public void setFromOtherId(Integer fromOtherId) {
        this.fromOtherId = fromOtherId;
        _changed = true;
    }

    public Integer getChangeFlagsId() {
        return changeFlagsId;
    }

    public void setChangeFlagsId(Integer changeFlagsId) {
        this.changeFlagsId = changeFlagsId;
        _changed = true;
    }
}