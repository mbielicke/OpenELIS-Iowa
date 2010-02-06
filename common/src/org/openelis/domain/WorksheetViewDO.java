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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class extends note DO and carries several commonly used field user name
 * The additional field is for read/display only and does not get committed to
 * the database. Note: isChanged will not reflect any changes to read/display
 * fields.
 */

public class WorksheetViewDO extends WorksheetDO {

    private static final long serialVersionUID = 1L;

    protected String          systemUser, testName, methodName;

    public WorksheetViewDO() {

    }

    public WorksheetViewDO(Integer id, Date createdDate, Integer systemUserId,
                           Integer statusId, Integer formatId, Integer relatedWorksheetId) {
        super(id, createdDate, systemUserId, statusId, formatId, relatedWorksheetId);
    }

    public WorksheetViewDO(Integer id, Date createdDate, Integer systemUserId,
                           Integer statusId, Integer formatId, Integer relatedWorksheetId,
                           String testName, String methodName) {
        super(id, createdDate, systemUserId, statusId, formatId, relatedWorksheetId);
        setTestName(testName);
        setMethodName(methodName);
    }

    public String getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(String systemUser) {
        this.systemUser = DataBaseUtil.trim(systemUser);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
