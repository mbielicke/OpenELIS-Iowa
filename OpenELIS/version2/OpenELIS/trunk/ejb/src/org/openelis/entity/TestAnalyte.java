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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
@NamedQueries({
    @NamedQuery(name =  "TestAnalyte.FetchByAnalyteId",
                query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"+
                        "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name,s.name)"
                      + " from TestAnalyte ta left join ta.scriptlet s left join ta.analyte a where ta.analyteId = :id"),
    @NamedQuery(name =  "TestAnalyte.FetchByTestId",
                query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"+
                		"ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name,s.name)"
                      + " from TestAnalyte ta left join ta.scriptlet s left join ta.analyte a where ta.testId = :testId order by ta.sortOrder"),
    @NamedQuery(name =  "TestAnalyte.FetchByAnalysisId",
                query = "select distinct new org.openelis.domain.TestAnalyteViewDO(ta.id,ta.testId,ta.sortOrder,"+
                        "ta.rowGroup,ta.isColumn,ta.analyteId,ta.typeId,ta.isReportable,ta.resultGroup,ta.scriptletId,a.name,s.name)"
                      + " from Analysis an LEFT JOIN an.test t LEFT JOIN t.testAnalyte ta left join ta.scriptlet s left join ta.analyte a " + 
                        " where an.id = :analysisId order by ta.sortOrder")})
@Entity
@Table(name = "test_analyte")
@EntityListeners( {AuditUtil.class})
public class TestAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue
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
    @JoinColumn(name = "scriptlet_id", insertable = false, updatable = false)
    private Scriptlet   scriptlet;

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
        if (DataBaseUtil.isDifferent(rowGroup,this.rowGroup))
            this.rowGroup = rowGroup;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        if (DataBaseUtil.isDifferent(resultGroup,this.resultGroup))
            this.resultGroup = resultGroup;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder,this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId,this.typeId))
            this.typeId = typeId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId,this.analyteId))
            this.analyteId = analyteId;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        if (DataBaseUtil.isDifferent(isReportable,this.isReportable))
            this.isReportable = isReportable;
    }

    public String getIsColumn() {
        return isReportable;
    }

    public void setIsColumn(String isColumn) {
        if (DataBaseUtil.isDifferent(isColumn,this.isColumn))
            this.isColumn = isColumn;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(scriptletId,this.scriptletId))
            this.scriptletId = scriptletId;
    }
    
    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public Scriptlet getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Scriptlet scriptlet) {
        this.scriptlet = scriptlet;
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
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(testId, original.testId, doc, "test_id");
            AuditUtil.getChangeXML(rowGroup, original.rowGroup, doc, "row_group");
            AuditUtil.getChangeXML(resultGroup, original.resultGroup, doc, "result_group");
            AuditUtil.getChangeXML(sortOrder, original.sortOrder, doc, "sort_order");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(analyteId, original.analyteId, doc, "analyte_id");
            AuditUtil.getChangeXML(isReportable, original.isReportable, doc, "is_reportable");
            AuditUtil.getChangeXML(isColumn, original.isColumn, doc, "is_column");
            AuditUtil.getChangeXML(scriptletId, original.scriptletId, doc, "scriptlet_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "test_analyte";
    }

}
