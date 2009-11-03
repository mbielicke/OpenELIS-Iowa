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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table test_analyte.
 */

public class TestAnalyteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testId, sortOrder, rowGroup, analyteId, typeId, resultGroup,
                    scriptletId;
    protected String          isColumn, isReportable;

    public TestAnalyteDO() {
    }

    public TestAnalyteDO(Integer id, Integer testId, Integer sortOrder, Integer rowGroup,
                         String isColumn, Integer analyteId, Integer typeId, String isReportable,
                         Integer resultGroup, Integer scriptletId) {
        setId(id);
        setTestId(testId);
        setSortOrder(sortOrder);
        setRowGroup(rowGroup);
        setIsColumn(isColumn);
        setAnalyteId(analyteId);
        setTypeId(typeId);
        setIsReportable(isReportable);
        setResultGroup(resultGroup);
        setScriptletId(scriptletId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
        _changed = true;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if(this.sortOrder == null || !(this.sortOrder.equals(sortOrder))) {
            this.sortOrder = sortOrder;
            _changed = true;
        }
    }

    public Integer getRowGroup() {
        return rowGroup;
    }

    public void setRowGroup(Integer rowGroup) {
        this.rowGroup = rowGroup;
        _changed = true;
    }

    public String getIsColumn() {
        return isColumn;
    }

    public void setIsColumn(String isColumn) {
        this.isColumn = DataBaseUtil.trim(isColumn);
        _changed = true;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
        _changed = true;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
        _changed = true;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
        _changed = true;
    }
}
