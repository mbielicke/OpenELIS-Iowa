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
 * Class represents the fields in database table test_reflex.
 */

public class TestReflexDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testId, testAnalyteId, testResultId, flagsId, addTestId;

    public TestReflexDO() {
    }

    public TestReflexDO(Integer id, Integer testId, Integer testAnalyteId, Integer testResultId,
                        Integer flagsId, Integer addTestId) {
        setId(id);
        setTestId(testId);
        setTestAnalyteId(testAnalyteId);
        setTestResultId(testResultId);
        setFlagsId(flagsId);
        setAddTestId(addTestId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
        _changed = true;
    }

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = testAnalyteId;
        _changed = true;
    }

    public Integer getTestResultId() {
        return testResultId;
    }

    public void setTestResultId(Integer testResultId) {
        this.testResultId = testResultId;
        _changed = true;
    }

    public Integer getAddTestId() {
        return addTestId;
    }

    public void setAddTestId(Integer addTestId) {
        this.addTestId = addTestId;
        _changed = true;
    }

    public Integer getFlagsId() {
        return flagsId;
    }

    public void setFlagsId(Integer flagsId) {
        this.flagsId = flagsId;
        _changed = true;
    }
}
