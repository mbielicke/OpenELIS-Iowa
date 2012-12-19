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

/**
 * The class extends analysis DO and carries several commonly used fields such
 * as test & method names. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class AnalysisViewDO extends AnalysisDO {

    private static final long serialVersionUID = 1L;

    //
    // additional fields for read/display purposes
    //
    protected Integer         methodId;
    protected String          testName, testReportingDescription, methodName,
                              methodReportingDescription, preAnalysisTest, preAnalysisMethod,
                              sectionName;

    public AnalysisViewDO() {
    }

    // analysis and test name,method name, and status
    public AnalysisViewDO(Integer id, Integer sampleItemId, Integer revision, Integer testId,
                          Integer sectionId, Integer preAnalysisId, Integer parentAnalysisId,
                          Integer parentResultId, String isReportable, Integer unitOfMeasureId,
                          Integer statusId, Date availableDate, Date startedDate,
                          Date completedDate, Date releasedDate, Date printedDate,
                          String testName, String testReportingDescription, Integer methodId,
                          String methodName, String methodReportingDescription,
                          String preAnalysisTest, String preAnalysisMethod, String sectionName) {

        super(id, sampleItemId, revision, testId, sectionId, preAnalysisId, parentAnalysisId,
              parentResultId, isReportable, unitOfMeasureId, statusId, availableDate, startedDate,
              completedDate, releasedDate, printedDate);

        setTestName(testName);
        setTestReportingDescription(testReportingDescription);
        setMethodId(methodId);
        setMethodName(methodName);
        setMethodReportingDescription(methodReportingDescription);
        setPreAnalysisTest(preAnalysisTest);
        setPreAnalysisMethod(preAnalysisMethod);
        setSectionName(sectionName);        
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }

    public String getTestReportingDescription() {
        return testReportingDescription;
    }

    public void setTestReportingDescription(String testReportingDescription) {
        this.testReportingDescription = DataBaseUtil.trim(testReportingDescription);
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
    
    public String getMethodReportingDescription() {
        return methodReportingDescription;
    }

    public void setMethodReportingDescription(String methodReportingDescription) {
        this.methodReportingDescription = DataBaseUtil.trim(methodReportingDescription);
    }

    public String getPreAnalysisTest() {
        return preAnalysisTest;
    }

    public void setPreAnalysisTest(String preAnalysisTest) {
        this.preAnalysisTest = DataBaseUtil.trim(preAnalysisTest);
    }

    public String getPreAnalysisMethod() {
        return preAnalysisMethod;
    }

    public void setPreAnalysisMethod(String preAnalysisMethod) {
        this.preAnalysisMethod = DataBaseUtil.trim(preAnalysisMethod);
    }

    public void setSectionName(String sectionName) {
        this.sectionName = DataBaseUtil.trim(sectionName);
    }

    public String getSectionName() {
        return sectionName;
    }
}