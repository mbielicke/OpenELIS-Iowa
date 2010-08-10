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

public class WorksheetResultViewDO extends WorksheetResultDO {

    private static final long serialVersionUID = 1L;

    protected Integer resultGroup;
    protected String  analyteName;

    public WorksheetResultViewDO() {
    }

    public WorksheetResultViewDO(Integer id, Integer worksheetAnalysisId, Integer testAnalyteId,
                                 Integer testResultId, String isColumn, Integer sortOrder,
                                 Integer analyteId, Integer typeId, String value,
                                 String analyteName, Integer resultGroup) {
        super(id, worksheetAnalysisId, testAnalyteId, testResultId, isColumn, sortOrder,
              analyteId, typeId, value);
        setAnalyteName(analyteName);
        setResultGroup(resultGroup);
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = analyteName;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }
}