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
 * TestAnalyte Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery(name = "TestAnalyte.FetchById",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                            + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                            + " from TestAnalyte ta left join ta.analyte a where ta.id = :id"),
               @NamedQuery(name = "TestAnalyte.FetchByAnalyteId",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from TestAnalyte ta left join ta.analyte a where ta.analyteId = :id order by ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.FetchByTestId",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from TestAnalyte ta left join ta.analyte a where ta.testId = :id order by ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.FetchByTestIds",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from TestAnalyte ta left join ta.analyte a where ta.testId in (:ids) order by ta.testId, ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.FetchByAnalysisId",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from Analysis an LEFT JOIN an.test t LEFT JOIN t.testAnalyte ta left join ta.analyte a "
                                   + " where an.id = :analysisId order by ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.FetchRowAnalytesByTestId",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from TestAnalyte ta left join ta.analyte a where ta.testId = :id and ta.isColumn = 'N' order by ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.FetchRowAnalytesByTestIds",
                           query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"
                                   + "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name, a.externalId)"
                                   + " from TestAnalyte ta left join ta.analyte a where ta.testId in (:ids) and ta.isColumn = 'N' order by ta.testId, ta.sortOrder")})
@Entity
@Table(name = "test_analyte")
@EntityListeners({AuditUtil.class})
public class TestAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer     id;

    @Column(name = "test_id")
    private Integer     testId;

    @Column(name = "row_group")
    private Integer     rowGroup;

    @Column(name = "result_group")
    private Integer     resultGroup;

    @Column(name = "sort_order")
    private Integer     sortOrder;

    @Column(name = "type_id")
    private Integer     typeId;

    @Column(name = "analyte_id")
    private Integer     analyteId;

    @Column(name = "is_reportable")
    private String      isReportable;

    @Column(name = "is_column")
    private String      isColumn;

    @Column(name = "scriptlet_id")
    private Integer     scriptletId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte     analyte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test        test;

    @Transient
    private TestAnalyte original;

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

    public Integer getRowGroup() {
        return rowGroup;
    }

    public void setRowGroup(Integer rowGroup) {
        if (DataBaseUtil.isDifferent(rowGroup, this.rowGroup))
            this.rowGroup = rowGroup;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        if (DataBaseUtil.isDifferent(resultGroup, this.resultGroup))
            this.resultGroup = resultGroup;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        if (DataBaseUtil.isDifferent(isReportable, this.isReportable))
            this.isReportable = isReportable;
    }

    public String getIsColumn() {
        return isReportable;
    }

    public void setIsColumn(String isColumn) {
        if (DataBaseUtil.isDifferent(isColumn, this.isColumn))
            this.isColumn = isColumn;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(scriptletId, this.scriptletId))
            this.scriptletId = scriptletId;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public void setClone() {
        try {
            original = (TestAnalyte)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().TEST_ANALYTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("row_group", rowGroup, original.rowGroup)
                 .setField("is_column", isColumn, original.isColumn)
                 .setField("analyte_id", analyteId, original.analyteId, Constants.table().ANALYTE)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("result_group", resultGroup, original.resultGroup)
                 .setField("scriptlet_id",
                           scriptletId,
                           original.scriptletId,
                           Constants.table().DICTIONARY);

        return audit;
    }
}
