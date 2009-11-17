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
 * Class represents the fields in database table panel_item.
 */

public class PanelItemDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, panelId, sortOrder;
    protected String          testName, methodName;

    public PanelItemDO() {
    }

    public PanelItemDO(Integer id, Integer panelId, Integer sortOrder, String testName,
                       String methodName) {
        setId(id);
        setPanelId(panelId);
        setSortOrder(sortOrder);
        setTestName(testName);
        setMethodName(methodName);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getPanelId() {
        return panelId;
    }

    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
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

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
        _changed = true;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
        _changed = true;
    }
}
