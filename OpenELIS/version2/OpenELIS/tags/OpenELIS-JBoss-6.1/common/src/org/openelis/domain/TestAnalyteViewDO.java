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
 * The class extends test_analysis DO and carries several commonly used fields
 * such as analyte and scriptlet names. The additional fields are for
 * read/display only and do not get committed to the database. Note: isChanged
 * will reflect any changes to read/display fields.
 */

public class TestAnalyteViewDO extends TestAnalyteDO {

    private static final long serialVersionUID = 1L;

    protected String          scriptletName, analyteName, isAlias;

    public TestAnalyteViewDO() {
    }

    public TestAnalyteViewDO(Integer id, Integer testId, Integer sortOrder, Integer rowGroup,
                                String isColumn, Integer analyteId, Integer typeId,
                                String isReportable, Integer resultGroup, Integer scriptletId,
                                String analyteName, String scriptletName) {
        super(id, testId, sortOrder, rowGroup, isColumn, analyteId, typeId, isReportable,
              resultGroup, scriptletId);
        setScriptletName(scriptletName);
        setAnalyteName(analyteName);
        setIsAlias("N");
        
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }

    public String getIsColumn() {
        return isColumn;
    }

    public void setIsColumn(String isColumn) {
        this.isColumn = DataBaseUtil.trim(isColumn);
    }

    public String getIsAlias() {
        return isAlias;
    }

    public void setIsAlias(String isAlias) {
        this.isAlias = DataBaseUtil.trim(isAlias);
    }

}
