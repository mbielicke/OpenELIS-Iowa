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

import org.openelis.gwt.common.DataBaseUtil;

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetResultViewDO extends WorksheetResultDO {

    private static final long serialVersionUID = 1L;

    protected Integer resultGroup;
    protected String  analyteName, analyteExternalId;

    public WorksheetResultViewDO() {
    }

    public WorksheetResultViewDO(Integer id, Integer worksheetAnalysisId, Integer testAnalyteId,
                                 Integer testResultId, Integer resultRow, Integer analyteId,
                                 Integer typeId, String v1, String v2, String v3,
                                 String v4, String v5, String v6, String v7, String v8,
                                 String v9, String v10, String v11, String v12,
                                 String v13, String v14, String v15, String v16,
                                 String v17, String v18, String v19, String v20,
                                 String v21, String v22, String v23, String v24,
                                 String v25, String v26, String v27, String v28,
                                 String v29, String v30, String analyteName, String externalId,
                                 Integer resultGroup) {
        super(id, worksheetAnalysisId, testAnalyteId, testResultId, resultRow, analyteId,
              typeId, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14,
              v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28,
              v29, v30);
        setAnalyteName(analyteName);
        setAnalyteExternalId(externalId);
        setResultGroup(resultGroup);
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getAnalyteExternalId() {
        return analyteExternalId;
    }

    public void setAnalyteExternalId(String analyteExternalId) {
        this.analyteExternalId = DataBaseUtil.trim(analyteExternalId);
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }
}