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
 * Class represents the fields in database table test_result.  
 */

public class TestResultDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testId, resultGroup, sortOrder, unitOfMeasureId, typeId,
                              significantDigits, roundingMethodId, flagsId;
    protected String          value;

    public TestResultDO() {
    }

    public TestResultDO(Integer id, Integer testId, Integer resultGroup, Integer sortOrder,
                        Integer unitOfMeasureId, Integer typeId, String value,
                        Integer significantDigits, Integer roundingMethodId, Integer flagsId) {
        setId(id);
        setTestId(testId);
        setResultGroup(resultGroup);
        setSortOrder(sortOrder);
        setUnitOfMeasureId(unitOfMeasureId);
        setTypeId(typeId);
        setValue(value);
        setSignificantDigits(significantDigits);
        setRoundingMethodId(roundingMethodId);
        setFlagsId(flagsId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        if(this.resultGroup == null || !(this.resultGroup.equals(resultGroup))) {
            this.resultGroup = resultGroup;
            _changed = true;
        }
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if(this.sortOrder == null || !(this.sortOrder.equals(sortOrder))) {
            this.sortOrder = sortOrder;
            _changed = true;
        }
        
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
        _changed = true;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = DataBaseUtil.trim(value);
        _changed = true;
    }

    public Integer getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(Integer significantDigits) {
        this.significantDigits = significantDigits;
        _changed = true;
    }

    public Integer getRoundingMethodId() {
        return roundingMethodId;
    }

    public void setRoundingMethodId(Integer roundingMethodId) {
        this.roundingMethodId = roundingMethodId;
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
