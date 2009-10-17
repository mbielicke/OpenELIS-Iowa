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

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetAnalyteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer id, worksheetAnalysisId, sortOrder, analyteId, resultId;
    protected String  value;

    public WorksheetAnalyteDO() {
    }

    public WorksheetAnalyteDO(Integer id, Integer worksheetAnalysisId,
                              Integer sortOrder, Integer analyteId,
                              String value, Integer resultId) {
        setId(id);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setSortOrder(sortOrder);
        setAnalyteId(analyteId);
        setValue(value);
        setResultId(resultId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
        _changed = true;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        _changed = true;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
        _changed = true;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
        _changed = true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        _changed = true;
    }
}