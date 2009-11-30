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

import org.w3c.dom.Document;
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

import org.w3c.dom.Element;

import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery(name = "Test.FetchById",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                        "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,"+
                        "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId,"+
                        "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) "
                      + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.id = :id"),                                                    
    @NamedQuery(name = "Test.FetchByName",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                        "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,"+
                        "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId,"+
                        "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) "
                      + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.name = :name order by t.name"),
    @NamedQuery(name = "Test.FetchByNameMethodName",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        " t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                        " t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,"+
                        " t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId,"+
                        " t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) " +
                        " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.name = :name and " +
                        " m.name=:methodName order by t.name"),
    @NamedQuery(name = "Test.FetchNameMethodSectionByName",
                query = "select distinct new org.openelis.domain.PanelVO(t.id,t.name,m.name,s.name)"
                      + "  from Test t left join t.method m left join t.testSection ts left join ts.section s where t.isActive = 'Y' and t.name like :name order by t.name,m.name,s.name "),
    @NamedQuery(name = "Test.FetchWithMethodByName", 
                query = "select new org.openelis.domain.TestMethodVO(t.id, t.name,t.description, m.id, m.name,m.description)"
                      + " from Test t LEFT JOIN t.method m where t.name like :name and t.isActive='Y' order by t.name"),
    @NamedQuery(name = "Test.FetchByNameSampleItemType",
                query = "select distinct new org.openelis.domain.TestMethodVO(t.id, t.name, t.description, m.id, m.name, m.description) "
                      + " from Test t left join t.method m LEFT JOIN t.testTypeOfSample type where t.name like :name and type.typeOfSampleId = :typeId and t.isActive='Y' order by t.name"),
    @NamedQuery(name = "Test.FetchByMethod",
                query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                        "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                        "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,"+
                        "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId,"+
                        "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) "
                      + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.methodId = :id and t.isActive = 'Y'")})
    

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

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(description, original.description, doc, "description");
            AuditUtil.getChangeXML(reportingDescription, original.reportingDescription, doc, "reporting_description");
            AuditUtil.getChangeXML(methodId, original.methodId, doc, "method_id");
            AuditUtil.getChangeXML(isActive, original.isActive, doc, "is_active");
            AuditUtil.getChangeXML(activeBegin, original.activeBegin, doc, "active_begin");
            AuditUtil.getChangeXML(activeEnd, original.activeEnd, doc, "active_end");
            AuditUtil.getChangeXML(isReportable, original.isReportable, doc, "is_reportable");
            AuditUtil.getChangeXML(timeTransit, original.timeTransit, doc, "time_transit");
            AuditUtil.getChangeXML(timeHolding, original.timeHolding, doc, "time_holding");
            AuditUtil.getChangeXML(timeTaAverage, original.timeTaAverage, doc, "time_ta_average");
            AuditUtil.getChangeXML(timeTaWarning, original.timeTaWarning, doc, "time_ta_warning");
            AuditUtil.getChangeXML(timeTaMax, original.timeTaMax, doc, "time_ta_max");
            AuditUtil.getChangeXML(labelId, original.labelId, doc, "label_id");
            AuditUtil.getChangeXML(labelQty, original.labelQty, doc, "label_qty");
            AuditUtil.getChangeXML(testTrailerId, original.testTrailerId, doc, "test_trailer_id");
            AuditUtil.getChangeXML(scriptletId, original.scriptletId, doc, "scriptlet_id");
            AuditUtil.getChangeXML(testFormatId, original.testFormatId, doc, "test_format_id");
            AuditUtil.getChangeXML(revisionMethodId, original.revisionMethodId, doc, "revision_method_id");
            AuditUtil.getChangeXML(reportingMethodId, original.reportingMethodId, doc, "reporting_method_id");
            AuditUtil.getChangeXML(sortingMethodId, original.sortingMethodId, doc, "sorting_method_id");
            AuditUtil.getChangeXML(reportingSequence, original.reportingSequence, doc, "reporting_sequence");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getTableName() {
        return "test";
    }

}
