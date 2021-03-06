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

import org.openelis.ui.common.DataBaseUtil;

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetResultDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, worksheetAnalysisId, testAnalyteId, resultRow, analyteId,
                              changeFlagsId;
    protected String          values[];

    public WorksheetResultDO() {
        values = new String[30];
    }

    public WorksheetResultDO(Integer id, Integer worksheetAnalysisId, Integer testAnalyteId,
                             Integer resultRow, Integer analyteId, String v1, String v2, String v3,
                             String v4, String v5, String v6, String v7, String v8, String v9,
                             String v10, String v11, String v12, String v13, String v14,
                             String v15, String v16, String v17, String v18, String v19,
                             String v20, String v21, String v22, String v23, String v24,
                             String v25, String v26, String v27, String v28, String v29,
                             String v30, Integer changeFlagsId) {
        values = new String[30];

        setId(id);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setTestAnalyteId(testAnalyteId);
        setResultRow(resultRow);
        setAnalyteId(analyteId);
        setValueAt(0, v1);
        setValueAt(1, v2);
        setValueAt(2, v3);
        setValueAt(3, v4);
        setValueAt(4, v5);
        setValueAt(5, v6);
        setValueAt(6, v7);
        setValueAt(7, v8);
        setValueAt(8, v9);
        setValueAt(9, v10);
        setValueAt(10, v11);
        setValueAt(11, v12);
        setValueAt(12, v13);
        setValueAt(13, v14);
        setValueAt(14, v15);
        setValueAt(15, v16);
        setValueAt(16, v17);
        setValueAt(17, v18);
        setValueAt(18, v19);
        setValueAt(19, v20);
        setValueAt(20, v21);
        setValueAt(21, v22);
        setValueAt(22, v23);
        setValueAt(23, v24);
        setValueAt(24, v25);
        setValueAt(25, v26);
        setValueAt(26, v27);
        setValueAt(27, v28);
        setValueAt(28, v29);
        setValueAt(29, v30);
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

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
        _changed = true;
    }

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = testAnalyteId;
        _changed = true;
    }

    public Integer getResultRow() {
        return resultRow;
    }

    public void setResultRow(Integer resultRow) {
        this.resultRow = resultRow;
        _changed = true;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
        _changed = true;
    }

    public String getValueAt(int index) {
        return values[index];
    }

    public void setValueAt(int index, String value) {
        this.values[index] =  DataBaseUtil.trim(value);
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