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

public class AnalysisViewDO extends AnalysisDO {

    private static final long serialVersionUID = 1L;

    //
    // additional fields for read/display purposes
    //
    protected Integer         methodId;
    protected String          testName, methodName, sectionName, unitOfMeasure;

    public AnalysisViewDO() {
    }

    // analysis and test name,method name, and status
    public AnalysisViewDO(Integer id, Integer sampleItemId, Integer revision, Integer testId,
                          Integer sectionId, Integer preAnalysisId, Integer parentAnalysisId,
                          Integer parentResultId, String isReportable, Integer unitOfMeasureId,
                          Integer statusId, Date availableDate, Date startedDate,
                          Date completedDate, Date releasedDate, Date printedDate,
                          String sectionName, String testName, Integer methodId, String methodName, String unitOfMeasure) {

        super(id, sampleItemId, revision, testId, sectionId, preAnalysisId, parentAnalysisId,
              parentResultId, isReportable, unitOfMeasureId, statusId, availableDate, startedDate,
              completedDate, releasedDate, printedDate);

        setSectionName(sectionName);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setUnitOfMeasure(unitOfMeasure);
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
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

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = DataBaseUtil.trim(unitOfMeasure);
    }
}
