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
 * TestWorksheetAnalyte Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "TestWorksheetAnalyte.FetchByTestId",
            query = "select distinct new org.openelis.domain.TestWorksheetAnalyteViewDO(twa.id,twa.testId,twa.testAnalyteId, twa.repeat,twa.flagId,a.name,ta.sortOrder)"
                  + " from TestWorksheetAnalyte twa left join twa.testAnalyte ta left join ta.analyte a where twa.testId = :testId order by ta.sortOrder ")
@Entity
@Table(name = "test_worksheet_analyte")
@EntityListeners( {AuditUtil.class})
public class TestWorksheetAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer              id;

    @Column(name = "test_id")
    private Integer              testId;

    @Column(name = "test_analyte_id")
    private Integer              testAnalyteId;

    @Column(name = "repeat")
    private Integer              repeat;

    @Column(name = "flag_id")
    private Integer              flagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
    private TestAnalyte          testAnalyte;

    @Transient
    private TestWorksheetAnalyte original;

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

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        if (DataBaseUtil.isDifferent(testAnalyteId, this.testAnalyteId))
            this.testAnalyteId = testAnalyteId;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        if (DataBaseUtil.isDifferent(repeat, this.repeat))
            this.repeat = repeat;
    }

    public Integer getFlagId() {
        return flagId;
    }

    public void setFlagId(Integer flagId) {
        if (DataBaseUtil.isDifferent(flagId, this.flagId))
            this.flagId = flagId;
    }
    
    TestAnalyte getTestAnalyte() {
        return testAnalyte;
    }

    void setTestAnalyte(TestAnalyte testAnalyte) {
        this.testAnalyte = testAnalyte;
    }

    public void setClone() {
        try {
            original = (TestWorksheetAnalyte)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.TEST_WORKSHEET_ANALYTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("test_Analyte_id", testAnalyteId, original.testAnalyteId)
                 .setField("repeat", repeat, original.repeat)
                 .setField("flag_id", flagId, original.flagId);

        return audit;
    }
}
