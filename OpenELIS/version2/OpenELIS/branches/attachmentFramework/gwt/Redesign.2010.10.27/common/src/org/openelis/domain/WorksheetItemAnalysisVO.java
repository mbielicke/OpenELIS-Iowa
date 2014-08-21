/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import org.openelis.gwt.common.RPC;

public class WorksheetItemAnalysisVO implements RPC {
    private static final long serialVersionUID = 1L;

    protected Integer worksheetItemId, position, worksheetAnalysisId, analysisId,
                      qcId;
    protected String  description, accessionNumber, testName, methodName;

    public WorksheetItemAnalysisVO() {

    }

    public WorksheetItemAnalysisVO(Integer worksheetItemId, Integer position,
                                   Integer worksheetAnalysisId, Integer analysisId,
                                   Integer qcId, String description, String accessionNumber,
                                   String testName, String methodName) {
        setWorksheetItemId(worksheetItemId);
        setPosition(position);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setAnalysisId(analysisId);
        setQcId(qcId);
        setDescription(description);
        setAccessionNumber(accessionNumber);
        setTestName(testName);
        setMethodName(methodName);
    }

    /**
     * @return the worksheetItemId
     */
    public Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    /**
     * @param worksheetItemId the worksheetItemId to set
     */
    public void setWorksheetItemId(Integer worksheetItemId) {
        this.worksheetItemId = worksheetItemId;
    }

    /**
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @return the worksheetAnalysisId
     */
    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    /**
     * @param worksheetAnalysisId the worksheetAnalysisId to set
     */
    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
    }

    /**
     * @return the analysisId
     */
    public Integer getAnalysisId() {
        return analysisId;
    }

    /**
     * @param analysisId the analysisId to set
     */
    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    /**
     * @return the qcId
     */
    public Integer getQcId() {
        return qcId;
    }

    /**
     * @param qcId the qcId to set
     */
    public void setQcId(Integer qcId) {
        this.qcId = qcId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the accessionNumber
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * @param accessionNumber the accessionNumber to set
     */
    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @param testName the testName to set
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
