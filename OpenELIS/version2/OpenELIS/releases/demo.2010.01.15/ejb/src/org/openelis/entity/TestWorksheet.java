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
 * TestWorksheet Entity POJO for database
 */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "TestWorksheet.FetchByTestId",
           query = "select distinct new org.openelis.domain.TestWorksheetViewDO(tw.id,tw.testId,tw.batchCapacity," +
                   "tw.totalCapacity,tw.formatId,tw.scriptletId,s.name)"
                 + " from TestWorksheet tw left join tw.scriptlet s where tw.testId = :testId")
@Entity
@Table(name = "test_worksheet")
@EntityListeners( {AuditUtil.class})
public class TestWorksheet implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                       id;

    @Column(name = "test_id")
    private Integer                       testId;

    @Column(name = "batch_capacity")
    private Integer                       batchCapacity;

    @Column(name = "total_capacity")
    private Integer                       totalCapacity;

    @Column(name = "format_id")
    private Integer                       formatId;

    @Column(name = "scriptlet_id")
    private Integer                       scriptletId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_worksheet_id", insertable = false, updatable = false)
    private Collection<TestWorksheetItem> testWorksheetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scriptlet_id", insertable = false, updatable = false)
    private Scriptlet                     scriptlet;

    @Transient
    private TestWorksheet                 original;

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

    public Integer getBatchCapacity() {
        return batchCapacity;
    }

    public void setBatchCapacity(Integer batchCapacity) {
        if (DataBaseUtil.isDifferent(batchCapacity, this.batchCapacity))
            this.batchCapacity = batchCapacity;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        if (DataBaseUtil.isDifferent(totalCapacity, this.totalCapacity))
            this.totalCapacity = totalCapacity;
    }

    public Integer getFormatId() {
        return formatId;
    }

    public void setFormatId(Integer formatId) {
        if (DataBaseUtil.isDifferent(formatId, this.formatId))
            this.formatId = formatId;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(scriptletId, this.scriptletId))
            this.scriptletId = scriptletId;
    }

    public Collection<TestWorksheetItem> getTestWorksheetItem() {
        return testWorksheetItem;
    }

    public void setTestWorksheetItem(Collection<TestWorksheetItem> testWorksheetItem) {
        this.testWorksheetItem = testWorksheetItem;
    }

    public Scriptlet getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Scriptlet scriptlet) {
        this.scriptlet = scriptlet;
    }

    public void setClone() {
        try {
            original = (TestWorksheet)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.TEST_WORKSHEET);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("batch_capacity", batchCapacity, original.batchCapacity)
                 .setField("total_capacity", totalCapacity, original.totalCapacity)
                 .setField("format_id", formatId, original.formatId)
                 .setField("scriptlet_id", scriptletId, original.scriptletId);

        return audit;
    }
}
