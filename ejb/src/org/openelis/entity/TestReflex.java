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
 * TestReflex Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    
    @NamedQuery( name = "TestReflex.FetchByTestId",
                query = "select new org.openelis.domain.TestReflexViewDO(tr.id, tr.testId,tr.testAnalyteId,tr.testResultId," +
                        "tr.flagsId,tr.addTestId,t.name,m.name,a.name,trs.value,trs.typeId)"
                      + " from TestReflex tr left join tr.testResult trs left join tr.testAnalyte tra left join tra.analyte a left join tr.addTest t left join t.method m where tr.testId = :testId"),
    @NamedQuery( name = "TestReflex.FetchByAddTestId",
                query = "select new org.openelis.domain.TestReflexViewDO(tr.id, tr.testId,tr.testAnalyteId," +
                        "tr.testResultId,tr.flagsId,tr.addTestId,t.name,m.name,a.name,trs.value,trs.typeId)"
                      + " from TestReflex tr left join tr.testAnalyte tra left join tra.analyte a left join tr.testResult trs left join tr.test t left join t.method m where tr.addTestId = :testId and t.isActive = 'Y' ")
})

@Entity
@Table(name = "test_reflex")
@EntityListeners( {AuditUtil.class})
public class TestReflex implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer     id;

    @Column(name = "test_id")
    private Integer     testId;

    @Column(name = "test_analyte_id")
    private Integer     testAnalyteId;

    @Column(name = "test_result_id")
    private Integer     testResultId;

    @Column(name = "flags_id")
    private Integer     flagsId;

    @Column(name = "add_test_id")
    private Integer     addTestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flags_id", insertable = false, updatable = false)
    private Dictionary  dictionary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
    private TestAnalyte testAnalyte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_result_id", insertable = false, updatable = false)
    private TestResult  testResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_test_id", insertable = false, updatable = false)
    private Test        addTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test        test;

    @Transient
    private TestReflex  original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId,this.testId))
            this.testId = testId;
    }

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        if (DataBaseUtil.isDifferent(testAnalyteId,this.testAnalyteId))
            this.testAnalyteId = testAnalyteId;
    }

    public Integer getTestResultId() {
        return testResultId;
    }

    public void setTestResultId(Integer testResultId) {
        if (DataBaseUtil.isDifferent(testResultId,this.testResultId))
            this.testResultId = testResultId;
    }

    public Integer getFlagsId() {
        return flagsId;
    }

    public void setFlagsId(Integer flagsId) {
        if (DataBaseUtil.isDifferent(flagsId,this.flagsId))
            this.flagsId = flagsId;
    }

    public Integer getAddTestId() {
        return addTestId;
    }

    public void setAddTestId(Integer addTestId) {
        if (DataBaseUtil.isDifferent(addTestId,this.addTestId))
            this.addTestId = addTestId;
    }
    
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public Test getAddTest() {
        return addTest;
    }

    public void setAddTest(Test addTest) {
        this.addTest = addTest;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public TestAnalyte getTestAnalyte() {
        return testAnalyte;
    }

    public void setTestAnalyte(TestAnalyte testAnalyte) {
        this.testAnalyte = testAnalyte;
    }

    public void setClone() {
        try {
            original = (TestReflex)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.TEST_REFLEX);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("test_analyte_id", testAnalyteId, original.testAnalyteId, ReferenceTable.TEST_ANALYTE)
                 .setField("test_result_id", testResultId, original.testResultId, ReferenceTable.TEST_RESULT)
                 .setField("flags_id", flagsId, original.flagsId, ReferenceTable.DICTIONARY)
                 .setField("add_test_id", addTestId, original.addTestId, ReferenceTable.TEST);

        return audit;
    }
}
