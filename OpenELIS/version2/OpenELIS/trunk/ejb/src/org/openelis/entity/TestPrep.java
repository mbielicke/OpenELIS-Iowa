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
 * TestPrep Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "TestPrep.FetchByTestId",
                query = "select distinct new org.openelis.domain.TestPrepViewDO(tp.id, tp.testId, tp.prepTestId,tp.isOptional,t.name,m.name) "
                      + "  from TestPrep tp left join tp.prepTest t left join t.method m where tp.testId = :id "),
    @NamedQuery( name = "TestPrep.FetchByPrepTestId",
                query = " select distinct new org.openelis.domain.TestPrepViewDO(tp.id, tp.testId, tp.prepTestId,tp.isOptional,t.name,m.name) "
                      + " from TestPrep tp left join tp.test t left join t.method m where tp.prepTestId = :testId and t.isActive = 'Y' ")})
@Entity
@Table(name = "test_prep")
@EntityListeners({AuditUtil.class})
public class TestPrep implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer  id;

    @Column(name = "test_id")
    private Integer  testId;

    @Column(name = "prep_test_id")
    private Integer  prepTestId;

    @Column(name = "is_optional")
    private String   isOptional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test     test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prep_test_id", insertable = false, updatable = false)
    private Test     prepTest;

    @Transient
    private TestPrep original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if ( (id == null && this.id != null) || (id != null && !id.equals(this.id)))
            this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer getPrepTestId() {
        return prepTestId;
    }

    public void setPrepTestId(Integer prepTestId) {
        if (DataBaseUtil.isDifferent(prepTestId, this.prepTestId))
            this.prepTestId = prepTestId;
    }

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        if (DataBaseUtil.isDifferent(isOptional, this.isOptional))
            this.isOptional = isOptional;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Test getPrepTest() {
        return prepTest;
    }

    public void setPrepTest(Test prepTest) {
        this.prepTest = prepTest;
    }

    public void setClone() {
        try {
            original = (TestPrep)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.TEST_PREP);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("prep_test_id", prepTestId, original.prepTestId, ReferenceTable.TEST)
                 .setField("is_optional", isOptional, original.isOptional);

        return audit;
    }
}
