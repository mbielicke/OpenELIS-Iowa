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
 * Analysis Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

//@formatter:off  
@NamedQueries( {
    @NamedQuery( name = "Analysis.FetchById", 
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.panelId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.typeId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name, s.name, p.name)"
                      + " from Analysis a LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam LEFT JOIN a.section s LEFT JOIN a.panel p LEFT JOIN a.test t"
                      +	"  where a.id = :id"),
    @NamedQuery( name = "Analysis.FetchByIds", 
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.panelId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.typeId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name, s.name, p.name)"
                      + " from Analysis a LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam LEFT JOIN a.section s LEFT JOIN a.panel p LEFT JOIN a.test t"
                      + "  where a.id in (:ids)"),
    @NamedQuery( name = "Analysis.UpdatePrintedDateByIds", 
                query = " update Analysis set printedDate = :printedDate where id in (:ids)"),                  
    @NamedQuery( name = "Analysis.FetchBySampleId",
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.panelId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.typeId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name, s.name, p.name)"
                      + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat " + 
                        " LEFT JOIN pat.method pam LEFT JOIN a.section s LEFT JOIN a.panel p LEFT JOIN a.test t where si.sampleId = :id order by  si.itemSequence, t.name, t.method.name "),                       
    @NamedQuery( name = "Analysis.FetchBySampleIdForSDWISUnload",
                 query = "select new org.openelis.domain.SDWISUnloadReportVO(a.id, si.itemSequence, t.name, t.method.name, a.sectionId, se.organizationId, a.isReportable, a.unitOfMeasureId, a.statusId, a.startedDate, a.completedDate)"
                       + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.section se LEFT JOIN a.test t"
                       + " where si.sampleId = :id order by se.organizationId, a.sectionId, si.itemSequence, t.name, t.method.name "),
    @NamedQuery( name = "Analysis.FetchBySampleItemId",
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.panelId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.typeId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name, s.name, p.name)"
                      + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.section s LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam"
                      +	" LEFT JOIN a.panel p LEFT JOIN a.test t where a.sampleItemId = :id order by t.name, t.method.name "),
    @NamedQuery( name = "Analysis.FetchBySampleItemIds",
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.panelId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.typeId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name, s.name, p.name)"
                      + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.section s LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam"
                      + " LEFT JOIN a.panel p LEFT JOIN a.test t where a.sampleItemId in (:ids) order by si.sampleId, si.itemSequence, t.name, t.method.name "),          
    @NamedQuery( name = "Analysis.FetchForMCLViolation",
                query = "select distinct new org.openelis.domain.MCLViolationReportVO(s.id, s.accessionNumber, s.collectionDate, s.collectionTime, ss.stateLabId, ss.facilityId, ss.sampleTypeId, d1.entry, ss.sampleCategoryId, ss.samplePointId, ss.location, ss.collector, p.number0, p.name, p.alternateStNum, o.name, a.id, a.sectionId, se.name, a.unitOfMeasureId, a.startedDate, a.releasedDate, d2.entry, t.name, t.method.name)"
                      + " from Analysis a, SampleItem si, Sample s, SampleSDWIS ss, PWS p, SampleOrganization so, Organization o, Test t, Section se, Dictionary d1, Dictionary d2, Dictionary d3"
                      + " where a.sampleItemId = si.id and si.sampleId = s.id and ss.sampleId = s.id and ss.pwsId = p.id and ss.sampleTypeId = d1.id and so.sampleId = s.id and so.organizationId = o.id and a.testId = t.id and a.sectionId = se.id and a.unitOfMeasureId = d2.id and"
                      + " so.typeId = d3.id and d3.systemName = 'org_report_to' and a.releasedDate between :startDate and :endDate and a.isReportable = 'Y' order by p.number0, s.accessionNumber, a.sectionId, a.releasedDate"),
    @NamedQuery( name = "Analysis.FetchByPatientId",
                query = "select distinct new org.openelis.domain.SampleAnalysisVO(s.id, s.accessionNumber, s.receivedDate, a.id, t.id, t.name, m.name)"
                      + " from Analysis a join a.sampleItem si join si.sample s left join s.sampleClinical sc left join s.sampleNeonatal sn"
                      + " join a.test t join t.method m"
                      + " where sc.patientId = :patientId or sn.patientId = :patientId"
                      + " order by s.accessionNumber desc, t.name, m.name")})
@NamedNativeQueries({
    @NamedNativeQuery(name = "Analysis.FetchForTurnaroundWarningReport",
                     query = "select distinct a.id a_id, a.available_date, t.time_ta_warning, se.id se_id, CAST(se.name AS varchar(20)) se_name" +
                             " from analysis a, test t, dictionary d1, section se, section_parameter sp, dictionary d2" +
                             " where a.test_id = t.id and a.status_id = d1.id and d1.system_name not in ('analysis_released', 'analysis_cancelled') and" +
                             " a.section_id = se.id and sp.section_id = se.id and sp.type_id = d2.id and d2.system_name = 'section_ta_warn' and" +
                             " a.released_date is null and a.available_date is not null" +
                             " order by se_name, a.available_date, a_id",
		  resultSetMapping = "Analysis.FetchForTurnaroundWarningReportMapping"),
    @NamedNativeQuery(name = "Analysis.FetchForTurnaroundMaximumReport",
                     query = "select distinct a.id a_id, a.available_date, t.time_ta_max, se.id se_id, CAST(se.name AS varchar(20)) se_name" +
                             " from analysis a, test t, dictionary d1, section se, section_parameter sp, dictionary d2" +
                             " where a.test_id = t.id and a.status_id = d1.id and d1.system_name not in ('analysis_released', 'analysis_cancelled') and" +
                             " a.section_id = se.id and sp.section_id = se.id and sp.type_id = d2.id and d2.system_name = 'section_ta_max' and" +
                             " a.released_date is null and a.available_date is not null" +
                             " order by se_name, a.available_date, a_id",
            resultSetMapping = "Analysis.FetchForTurnaroundMaximumReportMapping")})
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "Analysis.FetchForTurnaroundWarningReportMapping",
                      columns = {@ColumnResult(name = "a_id"),
                                 @ColumnResult(name = "available_date"),
                                 @ColumnResult(name = "time_ta_warning"),
                                 @ColumnResult(name = "se_id"),
                                 @ColumnResult(name = "se_name")}),
    @SqlResultSetMapping(name = "Analysis.FetchForTurnaroundMaximumReportMapping",
                      columns = {@ColumnResult(name = "a_id"),
                                 @ColumnResult(name = "available_date"),
                                 @ColumnResult(name = "time_ta_max"),
                                 @ColumnResult(name = "se_id"),
                                 @ColumnResult(name = "se_name")})})

//@formatter:on
@Entity
@Table(name = "analysis")
@EntityListeners({AuditUtil.class})
public class Analysis implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                     id;

    @Column(name = "sample_item_id")
    private Integer                     sampleItemId;

    @Column(name = "revision")
    private Integer                     revision;

    @Column(name = "test_id")
    private Integer                     testId;

    @Column(name = "section_id")
    private Integer                     sectionId;

    @Column(name = "panel_id")
    private Integer                     panelId;

    @Column(name = "pre_analysis_id")
    private Integer                     preAnalysisId;

    @Column(name = "parent_analysis_id")
    private Integer                     parentAnalysisId;

    @Column(name = "parent_result_id")
    private Integer                     parentResultId;

    @Column(name = "type_id")
    private Integer                     typeId;

    @Column(name = "is_reportable")
    private String                      isReportable;

    @Column(name = "unit_of_measure_id")
    private Integer                     unitOfMeasureId;

    @Column(name = "status_id")
    private Integer                     statusId;

    @Column(name = "available_date")
    private Date                        availableDate;

    @Column(name = "started_date")
    private Date                        startedDate;

    @Column(name = "completed_date")
    private Date                        completedDate;

    @Column(name = "released_date")
    private Date                        releasedDate;

    @Column(name = "printed_date")
    private Date                        printedDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_item_id", insertable = false, updatable = false)
    private SampleItem                  sampleItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_analysis_id", insertable = false, updatable = false)
    private Analysis                    preAnalysis;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test                        test;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section                     section;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id", insertable = false, updatable = false)
    private Panel                       panel;

    // analysis qa events
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private Collection<AnalysisQaevent> analysisQAEvent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private Collection<Result>          result;

    @Transient
    private Analysis                    original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        if (DataBaseUtil.isDifferent(sampleItemId, this.sampleItemId))
            this.sampleItemId = sampleItemId;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        if (DataBaseUtil.isDifferent(revision, this.revision))
            this.revision = revision;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        if (DataBaseUtil.isDifferent(sectionId, this.sectionId))
            this.sectionId = sectionId;
    }

    public Integer getPanelId() {
        return panelId;
    }

    public void setPanelId(Integer panelId) {
        if (DataBaseUtil.isDifferent(panelId, this.panelId))
            this.panelId = panelId;
    }

    public Integer getPreAnalysisId() {
        return preAnalysisId;
    }

    public void setPreAnalysisId(Integer preAnalysisId) {
        if (DataBaseUtil.isDifferent(preAnalysisId, this.preAnalysisId))
            this.preAnalysisId = preAnalysisId;
    }

    public Integer getParentAnalysisId() {
        return parentAnalysisId;
    }

    public void setParentAnalysisId(Integer parentAnalysisId) {
        if (DataBaseUtil.isDifferent(parentAnalysisId, this.parentAnalysisId))
            this.parentAnalysisId = parentAnalysisId;
    }

    public Integer getParentResultId() {
        return parentResultId;
    }

    public void setParentResultId(Integer parentResultId) {
        if (DataBaseUtil.isDifferent(parentResultId, this.parentResultId))
            this.parentResultId = parentResultId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        if (DataBaseUtil.isDifferent(isReportable, this.isReportable))
            this.isReportable = isReportable;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        if (DataBaseUtil.isDifferent(unitOfMeasureId, this.unitOfMeasureId))
            this.unitOfMeasureId = unitOfMeasureId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Datetime getAvailableDate() {
        return DataBaseUtil.toYM(availableDate);
    }

    public void setAvailableDate(Datetime availableDate) {
        if (DataBaseUtil.isDifferentYM(availableDate, this.availableDate))
            this.availableDate = DataBaseUtil.toDate(availableDate);
    }

    public Datetime getStartedDate() {
        return DataBaseUtil.toYM(startedDate);
    }

    public void setStartedDate(Datetime startedDate) {
        if (DataBaseUtil.isDifferentYM(startedDate, this.startedDate))
            this.startedDate = DataBaseUtil.toDate(startedDate);
    }

    public Datetime getCompletedDate() {
        return DataBaseUtil.toYM(completedDate);
    }

    public void setCompletedDate(Datetime completedDate) {
        if (DataBaseUtil.isDifferentYM(completedDate, this.completedDate))
            this.completedDate = DataBaseUtil.toDate(completedDate);
    }

    public Datetime getReleasedDate() {
        return DataBaseUtil.toYM(releasedDate);
    }

    public void setReleasedDate(Datetime releasedDate) {
        if (DataBaseUtil.isDifferentYM(releasedDate, this.releasedDate))
            this.releasedDate = DataBaseUtil.toDate(releasedDate);
    }

    public Datetime getPrintedDate() {
        return DataBaseUtil.toYM(printedDate);
    }

    public void setPrintedDate(Datetime printedDate) {
        if (DataBaseUtil.isDifferentYM(printedDate, this.printedDate))
            this.printedDate = DataBaseUtil.toDate(printedDate);
    }

    public SampleItem getSampleItem() {
        return sampleItem;
    }

    public void setSampleItem(SampleItem sampleItem) {
        this.sampleItem = sampleItem;
    }

    public Analysis getPreAnalysis() {
        return preAnalysis;
    }

    public void setPreAnalysis(Analysis preAnalysis) {
        this.preAnalysis = preAnalysis;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    public Collection<AnalysisQaevent> getAnalysisQAEvent() {
        return analysisQAEvent;
    }

    public void setAnalysisQAEvent(Collection<AnalysisQaevent> analysisQAEvent) {
        this.analysisQAEvent = analysisQAEvent;
    }

    public Collection<Result> getResult() {
        return result;
    }

    public void setResult(Collection<Result> result) {
        this.result = result;
    }

    public void setClone() {
        try {
            original = (Analysis)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ANALYSIS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_item_id",
                           sampleItemId,
                           original.sampleItemId,
                           Constants.table().SAMPLE_ITEM)
                 .setField("revision", revision, original.revision)
                 .setField("test_id", testId, original.testId, Constants.table().TEST)
                 .setField("section_id", sectionId, original.sectionId, Constants.table().SECTION)
                 .setField("panel_id", panelId, original.panelId, Constants.table().PANEL)
                 .setField("pre_analysis_id",
                           preAnalysisId,
                           original.preAnalysisId,
                           Constants.table().ANALYSIS)
                 .setField("parent_analysis_id",
                           parentAnalysisId,
                           original.parentAnalysisId,
                           Constants.table().ANALYSIS)
                 .setField("parent_result_id",
                           parentResultId,
                           original.parentResultId,
                           Constants.table().RESULT)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("unit_of_measure_id",
                           unitOfMeasureId,
                           original.unitOfMeasureId,
                           Constants.table().DICTIONARY)
                 .setField("status_id", statusId, original.statusId, Constants.table().DICTIONARY)
                 .setField("available_date", availableDate, original.availableDate)
                 .setField("started_date", startedDate, original.startedDate)
                 .setField("completed_date", completedDate, original.completedDate)
                 .setField("released_date", releasedDate, original.releasedDate)
                 .setField("printed_date", printedDate, original.printedDate);

        return audit;
    }
}