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
package org.openelis.entity;

/**
 * TestResult Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "TestResult.FetchByTestIdResultGroup",
                query = "select distinct new org.openelis.domain.TestResultViewDO(tr.id,tr.testId,tr.resultGroup," +
                        "tr.sortOrder,tr.unitOfMeasureId,tr.typeId,tr.value,tr.significantDigits,tr.roundingMethodId,tr.flagsId,'')"
                      + " from TestResult tr where tr.testId = :testId and tr.resultGroup = :resultGroup order by tr.sortOrder "),      
    @NamedQuery( name = "TestResult.FetchByTestId",
                query = "select distinct new org.openelis.domain.TestResultDO(tr.id,tr.testId,tr.resultGroup," +
                        "tr.sortOrder,tr.unitOfMeasureId,tr.typeId,tr.value,tr.significantDigits,tr.roundingMethodId,tr.flagsId)"
                      + " from TestResult tr where tr.testId = :testId order by tr.resultGroup, tr.sortOrder "),
    @NamedQuery( name = "TestResult.FetchByAnalysisId",
                query = "select distinct new org.openelis.domain.TestResultDO(tr.id,tr.testId,tr.resultGroup," +
                        "tr.sortOrder,tr.unitOfMeasureId,tr.typeId,tr.value,tr.significantDigits,tr.roundingMethodId,tr.flagsId)"
                      + " from Analysis a, TestResult tr where a.testId = tr.testId and a.id = :analysisId order by tr.resultGroup, tr.sortOrder "),
   @NamedQuery( name =  "TestResult.FetchByValue",
               query =  "select distinct new org.openelis.domain.TestResultDO(tr.id,tr.testId,tr.resultGroup," +
                        "tr.sortOrder,tr.unitOfMeasureId,tr.typeId,tr.value,tr.significantDigits,tr.roundingMethodId,tr.flagsId)"
                      + " from TestResult tr where tr.value = :value" )})
   
@Entity
@Table(name = "test_result")
@EntityListeners({AuditUtil.class})
public class TestResult implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer    id;

    @Column(name = "test_id")
    private Integer    testId;

    @Column(name = "result_group")
    private Integer    resultGroup;

    @Column(name = "sort_order")
    private Integer    sortOrder;

    @Column(name = "flags_id")
    private Integer    flagsId;

    @Column(name = "type_id")
    private Integer    typeId;

    @Column(name = "value")
    private String     value;

    @Column(name = "significant_digits")
    private Integer    significantDigits;

    @Column(name = "rounding_method_id")
    private Integer    roundingMethodId;

    @Column(name = "unit_of_measure_id")
    private Integer    unitOfMeasureId;

    @Transient
    private TestResult original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer sortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        if (DataBaseUtil.isDifferent(resultGroup, this.resultGroup))
            this.resultGroup = resultGroup;
    }

    public Integer getFlagsId() {
        return flagsId;
    }

    public void setFlagsId(Integer flagsId) {
        if (DataBaseUtil.isDifferent(flagsId, this.flagsId))
            this.flagsId = flagsId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }

    public Integer getRoundingMethodId() {
        return roundingMethodId;
    }

    public void setRoundingMethodId(Integer roundingMethodId) {
        if (DataBaseUtil.isDifferent(roundingMethodId, this.roundingMethodId))
            this.roundingMethodId = roundingMethodId;
    }

    public Integer getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(Integer significantDigits) {
        if (DataBaseUtil.isDifferent(significantDigits, this.significantDigits))
            this.significantDigits = significantDigits;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        if (DataBaseUtil.isDifferent(unitOfMeasureId, this.unitOfMeasureId))
            this.unitOfMeasureId = unitOfMeasureId;
    }

    public void setClone() {
        try {
            original = (TestResult)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().TEST_RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("result_group", resultGroup, original.resultGroup)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("unit_of_measure_id", unitOfMeasureId, original.unitOfMeasureId, Constants.table().DICTIONARY)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("value", value, original.value)
                 .setField("significant_digits", significantDigits, original.significantDigits)
                 .setField("rounding_method_id", roundingMethodId, original.roundingMethodId, Constants.table().DICTIONARY)
                 .setField("flags_id", flagsId, original.flagsId, Constants.table().DICTIONARY);

        return audit;

    }
}
