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
 * The class extends IOrderTestDO and carries the additional test/panel information.
 * The additional field is for read/display only and does not get committed to
 * the database. Note: isChanged will not reflect any changes to read/display
 * fields.
 */

public class IOrderTestViewDO extends IOrderTestDO {

    private static final long serialVersionUID = 1L;

    protected String          testName, methodName, description, isActive;
    
    protected Integer         methodId;
    
    public IOrderTestViewDO() {        
    }
    
    public IOrderTestViewDO(Integer id, Integer iorderId, Integer itemSequence, Integer sortOrder,
                           Integer testId, String testName, Integer methodId, String methodName,
                           String description, String isActive) {
        super(id, iorderId, itemSequence, sortOrder, testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setDescription(description);
        setIsActive(isActive);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }
    
    private void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
    }
}