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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;

/**
 * Class contains several fields from test and method. It is used for
 * display/view only.
 */

public class TestMethodVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         testId, methodId;
    protected String          testName, testDescription, methodName, methodDescription, isActive;
    protected Datetime        activeBegin, activeEnd;

    public TestMethodVO() {
    }

    public TestMethodVO(Integer testId, String testName, String testDescription,
                            Integer methodId, String methodName, String methodDescription,
                            String isActive, Date activeBegin, Date activeEnd) {
        setTestId(testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setTestDescription(testDescription);
        setMethodDescription(methodDescription);
        setIsActive(isActive);
        setActiveBegin(DataBaseUtil.toYD(activeBegin));
        setActiveEnd(DataBaseUtil.toYD(activeEnd));
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = DataBaseUtil.trim(testDescription);
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
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = DataBaseUtil.trim(methodDescription);
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Datetime activeBegin) {
        this.activeBegin = DataBaseUtil.toYD(activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Datetime activeEnd) {
        this.activeEnd = DataBaseUtil.toYD(activeEnd);
    }
}