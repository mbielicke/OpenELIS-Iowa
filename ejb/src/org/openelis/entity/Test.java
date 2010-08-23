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
 * Test Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Test.FetchById",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable," +
                        "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId," +
                        "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId," +
                        "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name)"
                      + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.id = :id"),                                                    
    @NamedQuery( name = "Test.FetchByName",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable," +
                        "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId," +
                        "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId," +
                        "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name)"
                      + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.name = :name order by t.name"),
    @NamedQuery( name = "Test.FetchByNameMethodName",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        " t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                        " t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId," +
                        " t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId," +
                        " t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) " +
                        " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.name = :name and " +
                        " m.name=:methodName order by t.name"),
    @NamedQuery( name = "Test.FetchNameMethodSectionByName",
                query = "select distinct new org.openelis.domain.PanelVO(t.id,t.name,m.name,s.name)"
                      + "  from Test t left join t.method m left join t.testSection ts left join ts.section s where t.isActive = 'Y' and t.name like :name order by t.name,m.name,s.name "),
    @NamedQuery( name = "Test.FetchWithMethodByName", 
                query = "select new org.openelis.domain.TestMethodVO(t.id, t.name,t.description, m.id, m.name,m.description)"
                      + " from Test t LEFT JOIN t.method m where t.name like :name and t.isActive='Y' order by t.name"),
    @NamedQuery( name = "Test.FetchByNameSampleItemType",
                query = "select distinct new org.openelis.domain.TestMethodVO(t.id, t.name, t.description, m.id, m.name, m.description)"
                      + " from Test t left join t.method m LEFT JOIN t.testTypeOfSample type where t.name like :name and type.typeOfSampleId = :typeId and t.isActive='Y' order by t.name"),
    @NamedQuery( name = "Test.FetchTestMethodSampleTypeList",
                query = "select distinct new org.openelis.domain.TestMethodSampleTypeVO(t.id, t.name, m.name, type.typeOfSampleId, d.entry)"
                       + " from Test t left join t.method m INNER JOIN t.testTypeOfSample type LEFT JOIN type.dictionary d where t.isActive='Y' order by t.name, m.name, d.entry"),
    @NamedQuery( name = "Test.FetchByPanelId",
                query = "select distinct new org.openelis.domain.TestMethodVO(t.id, t.name, t.description, t.method.id, t.method.name, t.method.description)"
                      + " from Test t, PanelItem i where t.name = i.testName and t.method.name = i.methodName and t.isActive ='Y' and i.panelId = :panelId order by t.name ") })

@Entity
@Table(name = "test")
@EntityListeners( {AuditUtil.class})
public class Test implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                          id;

    @Column(name = "name")
    private String                           name;

    @Column(name = "description")
    private String                           description;

    @Column(name = "reporting_description")
    private String                           reportingDescription;

    @Column(name = "method_id")
    private Integer                          methodId;

    @Column(name = "is_active")
    private String                           isActive;

    @Column(name = "active_begin")
    private Date                             activeBegin;

    @Column(name = "active_end")
    private Date                             activeEnd;

    @Column(name = "is_reportable")
    private String                           isReportable;

    @Column(name = "time_transit")
    private Integer                          timeTransit;

    @Column(name = "time_holding")
    private Integer                          timeHolding;

    @Column(name = "time_ta_average")
    private Integer                          timeTaAverage;

    @Column(name = "time_ta_warning")
    private Integer                          timeTaWarning;

    @Column(name = "time_ta_max")
    private Integer                          timeTaMax;

    @Column(name = "label_id")
    private Integer                          labelId;

    @Column(name = "label_qty")
    private Integer                          labelQty;

    @Column(name = "test_trailer_id")
    private Integer                          testTrailerId;

    @Column(name = "scriptlet_id")
    private Integer                          scriptletId;

    @Column(name = "test_format_id")
    private Integer                          testFormatId;

    @Column(name = "revision_method_id")
    private Integer                          revisionMethodId;

    @Column(name = "reporting_method_id")
    private Integer                          reportingMethodId;

    @Column(name = "sorting_method_id")
    private Integer                          sortingMethodId;

    @Column(name = "reporting_sequence")
    private Integer                          reportingSequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id", insertable = false, updatable = false)
    private Method                           method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scriptlet_id", insertable = false, updatable = false)
    private Scriptlet                        scriptlet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_trailer_id", insertable = false, updatable = false)
    private TestTrailer                      testTrailer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", insertable = false, updatable = false)
    private Label                            label;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestPrep>             testPrep;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestTypeOfSample>     testTypeOfSample;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestReflex>           testReflex;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestWorksheet>        testWorksheet;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestAnalyte>          testAnalyte;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestSection>          testSection;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestResult>           testResult;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Collection<TestWorksheetAnalyte> testWorksheetAnalyte;

    @Transient
    private Test                             original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name,this.name))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description,this.description))
            this.description = description;
    }

    public String getReportingDescription() {
        return reportingDescription;
    }

    public void setReportingDescription(String reportingDescription) {
        if (DataBaseUtil.isDifferent(reportingDescription,this.reportingDescription))
            this.reportingDescription = reportingDescription;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        if (DataBaseUtil.isDifferent(methodId,this.methodId))
            this.methodId = methodId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive,this.isActive))
            this.isActive = isActive;
    }

    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(activeBegin);
    }

    public void setActiveBegin(Datetime active_begin) {
        if (DataBaseUtil.isDifferentYD(active_begin,this.activeBegin))
            this.activeBegin = active_begin.getDate();        
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime active_end) {
        if (DataBaseUtil.isDifferentYD(active_end,this.activeEnd))
            this.activeEnd = active_end.getDate();
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        if (DataBaseUtil.isDifferent(isReportable,this.isReportable))
            this.isReportable = isReportable;
    }

    public Integer getTimeTransit() {
        return timeTransit;
    }

    public void setTimeTransit(Integer timeTransit) {
        if (DataBaseUtil.isDifferent(timeTransit,this.timeTransit))
            this.timeTransit = timeTransit;
    }

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public void setTimeHolding(Integer timeHolding) {
        if (DataBaseUtil.isDifferent(timeHolding,this.timeHolding))
            this.timeHolding = timeHolding;
    }

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public void setTimeTaAverage(Integer timeTaAverage) {
        if (DataBaseUtil.isDifferent(timeTaAverage,this.timeTaAverage))
            this.timeTaAverage = timeTaAverage;
    }

    public Integer getTimeTaWarning() {
        return timeTaWarning;
    }

    public void setTimeTaWarning(Integer timeTaWarning) {
        if (DataBaseUtil.isDifferent(timeTaWarning,this.timeTaWarning))
            this.timeTaWarning = timeTaWarning;
    }

    public Integer getTimeTaMax() {
        return timeTaMax;
    }

    public void setTimeTaMax(Integer timeTaMax) {
        if (DataBaseUtil.isDifferent(timeTaMax,this.timeTaMax))
            this.timeTaMax = timeTaMax;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        if (DataBaseUtil.isDifferent(labelId,this.labelId))
            this.labelId = labelId;
    }

    public Integer getLabelQty() {
        return labelQty;
    }

    public void setLabelQty(Integer labelQty) {
        if (DataBaseUtil.isDifferent(labelQty,this.labelQty))
            this.labelQty = labelQty;
    }

    public Integer getTestTrailerId() {
        return testTrailerId;
    }

    public void setTestTrailerId(Integer testTrailerId) {
        if (DataBaseUtil.isDifferent(testTrailerId,this.testTrailerId))
            this.testTrailerId = testTrailerId;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(scriptletId,this.scriptletId))
            this.scriptletId = scriptletId;
    }

    public Integer getTestFormatId() {
        return testFormatId;
    }

    public void setTestFormatId(Integer testFormatId) {
        if (DataBaseUtil.isDifferent(testFormatId,this.testFormatId))
            this.testFormatId = testFormatId;
    }

    public Integer getRevisionMethodId() {
        return revisionMethodId;
    }

    public void setRevisionMethodId(Integer revisionMethodId) {
        if (DataBaseUtil.isDifferent(revisionMethodId,this.revisionMethodId))
            this.revisionMethodId = revisionMethodId;
    }

    public Integer getReportingMethodId() {
        return reportingMethodId;
    }

    public void setReportingMethodId(Integer reportingMethodId) {
        if (DataBaseUtil.isDifferent(reportingMethodId,this.reportingMethodId))
            this.reportingMethodId = reportingMethodId;
    }

    public Integer getSortingMethodId() {
        return sortingMethodId;
    }

    public void setSortingMethodId(Integer sortingMethodId) {
        if (DataBaseUtil.isDifferent(sortingMethodId,this.sortingMethodId))
            this.sortingMethodId = sortingMethodId;
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        if (DataBaseUtil.isDifferent(reportingSequence,this.reportingSequence))
            this.reportingSequence = reportingSequence;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Collection<TestPrep> getTestPrep() {
        return testPrep;
    }

    public void setTestPrep(Collection<TestPrep> testPrep) {
        this.testPrep = testPrep;
    }

    public Collection<TestTypeOfSample> getTestTypeOfSample() {
        return testTypeOfSample;
    }

    public void setTestTypeOfSample(Collection<TestTypeOfSample> testTypeOfSample) {
        this.testTypeOfSample = testTypeOfSample;
    }

    public Collection<TestReflex> getTestReflex() {
        return testReflex;
    }

    public void setTestReflex(Collection<TestReflex> testReflex) {
        this.testReflex = testReflex;
    }

    public Collection<TestWorksheet> getTestWorksheet() {
        return testWorksheet;
    }

    public void setTestWorksheet(Collection<TestWorksheet> testWorksheet) {
        this.testWorksheet = testWorksheet;
    }

    public Collection<TestAnalyte> getTestAnalyte() {
        return testAnalyte;
    }

    public void setTestAnalyte(Collection<TestAnalyte> testAnalyte) {
        this.testAnalyte = testAnalyte;
    }

    public Collection<TestSection> getTestSection() {
        return testSection;
    }

    public void setTestSection(Collection<TestSection> testSection) {
        this.testSection = testSection;
    }

    public Collection<TestResult> getTestResult() {
        return testResult;
    }

    public void setTestResult(Collection<TestResult> testResult) {
        this.testResult = testResult;
    }

    public Collection<TestWorksheetAnalyte> getTestWorksheetAnalyte() {
        return testWorksheetAnalyte;
    }

    public void setTestWorksheetAnalyte(Collection<TestWorksheetAnalyte> testWorksheetAnalyte) {
        this.testWorksheetAnalyte = testWorksheetAnalyte;
    }

    public Scriptlet getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Scriptlet scriptlet) {
        this.scriptlet = scriptlet;
    }

    public TestTrailer getTestTrailer() {
        return testTrailer;
    }

    public void setTestTrailer(TestTrailer testTrailer) {
        this.testTrailer = testTrailer;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setClone() {
        try {
            original = (Test)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.TEST);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("reporting_description", reportingDescription, original.reportingDescription)
                 .setField("method_id", methodId, original.methodId, ReferenceTable.METHOD)
                 .setField("is_active", isActive, original.isActive)
                 .setField("active_begin", activeBegin, original.activeBegin)
                 .setField("active_end", activeEnd, original.activeEnd)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("time_transit", timeTransit, original.timeTransit)
                 .setField("time_holding", timeHolding, original.timeHolding)
                 .setField("time_ta_average", timeTaAverage, original.timeTaAverage)
                 .setField("time_ta_warning", timeTaWarning, original.timeTaWarning)
                 .setField("time_ta_max", timeTaMax, original.timeTaMax)
                 .setField("label_id", labelId, original.labelId, ReferenceTable.LABEL)
                 .setField("label_qty", labelQty, original.labelQty)
                 .setField("test_trailer_id", testTrailerId, original.testTrailerId, ReferenceTable.TEST_TRAILER)
                 .setField("scriptlet_id", scriptletId, original.scriptletId, ReferenceTable.SCRIPTLET)
                 .setField("test_format_id", testFormatId, original.testFormatId, ReferenceTable.DICTIONARY)
                 .setField("revision_method_id", revisionMethodId, original.revisionMethodId, ReferenceTable.DICTIONARY)
                 .setField("reporting_method_id", reportingMethodId, original.reportingMethodId, ReferenceTable.DICTIONARY)
                 .setField("sorting_method_id", sortingMethodId, original.sortingMethodId, ReferenceTable.DICTIONARY)
                 .setField("reporting_sequence", reportingSequence, original.reportingSequence);
                 

        return audit;

    }
}
