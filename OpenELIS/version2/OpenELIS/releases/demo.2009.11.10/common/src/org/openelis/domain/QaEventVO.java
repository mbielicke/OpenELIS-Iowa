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
import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class is used to carry id and name and other fields for query returns, for left 
 * display, and some auto complete fields. The fields are considered read/display
 * and do not get committed to the database.
 */

public class QaEventVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testId, typeId, reportingSeuence;
    protected String          name, description, isBillable, testName, methodName;

    public QaEventVO() {
    }

    public QaEventVO(Integer id, String name, String description, Integer testId,
                     Integer typeId, String isBillable, Integer reportingSequence,
                     String testName, String methodName) {
        setId(id);
        setName(name);
        setDescription(description);
        setTestId(testId);
        setTypeId(typeId);
        setIsBillable(isBillable);
        setReportingSeuence(reportingSequence);
        setTestName(testName);
        setMethodName(methodName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getReportingSeuence() {
        return reportingSeuence;
    }

    public void setReportingSeuence(Integer reportingSeuence) {
        this.reportingSeuence = reportingSeuence;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = DataBaseUtil.trim(isBillable);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String name) {
        this.testName = DataBaseUtil.trim(name);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String name) {
        this.methodName = DataBaseUtil.trim(name);
    }
}
