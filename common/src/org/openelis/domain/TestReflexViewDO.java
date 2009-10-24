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
 * The class extends test reflex DO and carries several commonly used fields such
 * as test & method names. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class TestReflexViewDO extends TestReflexDO {

    private static final long serialVersionUID = 1L;

    protected String          addTestName, addMethodName, testAnalyteName, testResultValue;    
    protected Integer         testResultTypeId;

    public TestReflexViewDO() {
    }

    public TestReflexViewDO(Integer id, Integer testId, Integer testAnalyteId,
                                  Integer testResultId, Integer flagsId, Integer addTestId,
                                  String addTestName, String addMethodName,
                                  String testAnalyteName,String testResultValue,
                                  Integer testResultTypeId) {
        super(id, testId, testAnalyteId, testResultId, flagsId, addTestId);
        setAddTestName(addTestName);
        setAddMethodName(addMethodName);
        setTestAnalyteName(testAnalyteName);
        setTestResultValue(testResultValue);
        setTestResultTypeId(testResultTypeId);
    }

    public String getAddTestName() {
        return addTestName;
    }

    public void setAddTestName(String addTestName) {
        this.addTestName = DataBaseUtil.trim(addTestName);
    }

    public String getAddMethodName() {
        return addMethodName;
    }

    public void setAddMethodName(String addMethodName) {
        this.addMethodName = DataBaseUtil.trim(addMethodName);
    }

    public String getTestAnalyteName() {
        return testAnalyteName;
    }

    public void setTestAnalyteName(String testAnalyteName) {
        this.testAnalyteName = testAnalyteName;
    }

    public String getTestResultValue() {
        return testResultValue;
    }

    public void setTestResultValue(String testResultValue) {
        this.testResultValue = testResultValue;
    }

    public Integer getTestResultTypeId() {
        return testResultTypeId;
    }

    public void setTestResultTypeId(Integer testResultTypeId) {
        this.testResultTypeId = testResultTypeId;
    }
}
