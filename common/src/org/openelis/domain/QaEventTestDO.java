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

/**
 * The class extends qaevent DO and carries a commonly used field testName. The
 * additional field is read/display only and does not get committed to the
 * database. Note: isChanged will reflect any changes to read/display fields.
 */

public class QaEventTestDO extends QaEventDO {

    private static final long serialVersionUID = 1L;

    //
    // additional field for read/display purposes
    //
    protected String          testName;

    public QaEventTestDO() {
    }

    public QaEventTestDO(Integer id, String name, String description, Integer testId,
                         String testName, Integer typeId, String isBillable,
                         Integer reportingSequence, String reportingText) {
        super(id, name, description, testId, typeId, isBillable, reportingSequence,
              reportingText);
        setTestName(testName);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
