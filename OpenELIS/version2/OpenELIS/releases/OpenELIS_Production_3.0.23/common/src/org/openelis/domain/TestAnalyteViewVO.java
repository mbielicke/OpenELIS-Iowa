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

import java.io.Serializable;
import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * Class represents the fields in database view test_analyte_view. The view is
 * used mainly to allow querying for row and column test analytes by test,
 * method or analyte name without having to execute multiple queries.
 */
public class TestAnalyteViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testId, methodId, rowTestAnalyteId, rowAnalyteId, colAnalyteId;

    protected String          testName, methodName, testIsActive, rowAnalyteName, colAnalyteName;

    protected Datetime        testActiveBegin, testActiveEnd;

    public TestAnalyteViewVO() {
    }

    public TestAnalyteViewVO(Integer id, Integer testId, String testName, Integer methodId,
                             String methodName, String testIsActive, Date testActiveBegin,
                             Date testActiveEnd, Integer rowTestAnalyteId,
                             Integer rowAnalyteId, String rowAnalyteName, Integer colAnalyteId,
                             String colAnalyteName) {
        setId(id);
        setTestId(testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setTestIsActive(testIsActive);
        setTestActiveBegin(DataBaseUtil.toYD(testActiveBegin));
        setTestActiveEnd(DataBaseUtil.toYD(testActiveEnd));
        setRowTestAnalyteId(rowTestAnalyteId);
        setRowAnalyteId(rowAnalyteId);
        setRowAnalyteName(rowAnalyteName);
        setColAnalyteId(colAnalyteId);
        setColAnalyteName(colAnalyteName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTestIsActive() {
        return testIsActive;
    }

    public void setTestIsActive(String testIsActive) {
        this.testIsActive = DataBaseUtil.trim(testIsActive);
    }

    public Datetime getTestActiveBegin() {
        return testActiveBegin;
    }

    public void setTestActiveBegin(Datetime testActiveBegin) {
        this.testActiveBegin = DataBaseUtil.toYD(testActiveBegin);
    }

    public Datetime getTestActiveEnd() {
        return testActiveEnd;
    }

    public void setTestActiveEnd(Datetime testActiveEnd) {
        this.testActiveEnd = DataBaseUtil.toYD(testActiveEnd);
    }

    public Integer getRowTestAnalyteId() {
        return rowTestAnalyteId;
    }

    public void setRowTestAnalyteId(Integer rowTestAnalyteId) {
        this.rowTestAnalyteId = rowTestAnalyteId;
    }

    public Integer getRowAnalyteId() {
        return rowAnalyteId;
    }

    public void setRowAnalyteId(Integer rowAnalyteId) {
        this.rowAnalyteId = rowAnalyteId;
    }

    public String getRowAnalyteName() {
        return rowAnalyteName;
    }

    public void setRowAnalyteName(String rowAnalyteName) {
        this.rowAnalyteName = DataBaseUtil.trim(rowAnalyteName);
    }

    public Integer getColAnalyteId() {
        return colAnalyteId;
    }

    public void setColAnalyteId(Integer colAnalyteId) {
        this.colAnalyteId = colAnalyteId;
    }

    public String getColAnalyteName() {
        return colAnalyteName;
    }

    public void setColAnalyteName(String colAnalyteName) {
        this.colAnalyteName = DataBaseUtil.trim(colAnalyteName);
    }
}