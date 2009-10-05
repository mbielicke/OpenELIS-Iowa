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
 * The class extends analysis DO and carries several commonly used fields such
 * as test & method names. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class WorksheetViewDO extends WorksheetDO {

    private static final long serialVersionUID = 1L;

    //
    // additional fields for read/display purposes
    //
    protected Integer methodId;
    protected String  testName, methodName;

    public WorksheetViewDO() {
    }

    // test name, method id, and method name
    public WorksheetViewDO(Integer id, Date createdDate, Integer systemUser,
                           Integer statusId, Integer numberFormatId, Integer testId,
                           String testName, Integer methodId, String methodName) {
        super(id, createdDate, systemUser, statusId, numberFormatId, testId);

        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
