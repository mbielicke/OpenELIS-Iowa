/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * Analysis Entity POJO for database 
  */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Analysis.FetchById", 
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name)"
                      + " from Analysis a LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam LEFT JOIN a.test t where a.id = :id"),
    @NamedQuery( name = "Analysis.UpdatePrintedDateByIds", 
                query = " update Analysis set printedDate = :printedDate where id in (:ids)"),                  
    @NamedQuery( name = "Analysis.FetchBySampleId",
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name)"
                      + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat " + 
                        " LEFT JOIN pat.method pam LEFT JOIN a.test t where si.sampleId = :id order by  si.itemSequence, t.name, t.method.name "),
    @NamedQuery( name = "Analysis.FetchBySampleIdForSDWISUnload",
                 query = "select new org.openelis.domain.SDWISUnloadReportVO(a.id, si.itemSequence, t.name, t.method.name, a.sectionId, se.organizationId, a.isReportable, a.unitOfMeasureId, a.statusId, a.startedDate, a.completedDate)"
                       + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.section se LEFT JOIN a.test t"
                       + " where si.sampleId = :id order by se.organizationId, a.sectionId, si.itemSequence, t.name, t.method.name "),
    @NamedQuery( name = "Analysis.FetchBySampleItemId",
                query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision," + 
                        "a.testId, a.sectionId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.isReportable, a.unitOfMeasureId, a.statusId," + 
                        "a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, t.name, t.reportingDescription, t.method.id," +
                        "t.method.name, t.method.reportingDescription, pat.name, pam.name)"
                      + " from Analysis a LEFT JOIN a.sampleItem si LEFT JOIN a.preAnalysis pa LEFT JOIN pa.test pat LEFT JOIN pat.method pam LEFT JOIN a.test t where a.sampleItemId = :id order by t.name, t.method.name "),
    @NamedQuery( name = "Analysis.FetchForCachingByStatusId",
                query = "select distinct new org.openelis.domain.AnalysisCacheVO(a.id, a.statusId, a.sectionId, a.availableDate, a.startedDate, a.completedDate, a.releasedDate, s.id, s.domain, s.accessionNumber," +
                		"s.receivedDate, s.collectionDate, s.collectionTime, t.name,t.timeHolding, t.timeTaAverage, t.method.name)"
                      + " from Analysis a, SampleItem si, Sample s, Test t"
                      + " where a.sampleItemId = si.id and si.sampleId = s.id and a.testId = t.id and a.statusId = :statusId order by s.accessionNumber "),
    @NamedQuery( name = "Analysis.FetchOtherForCaching",
                query = "select distinct new org.openelis.domain.AnalysisCacheVO(a.id, a.statusId, a.sectionId, a.availableDate, a.startedDate, a.completedDate, a.releasedDate, s.id, s.domain, s.accessionNumber," +
                        "s.receivedDate, s.collectionDate, s.collectionTime, t.name,t.timeHolding, t.timeTaAverage, t.method.name)"
                      + " from Analysis a, SampleItem si, Sample s, Test t, Dictionary d"
                      + " where a.sampleItemId = si.id and si.sampleId = s.id and a.testId = t.id and a.statusId = d.id and d.systemName not in ('analysis_logged_in', 'analysis_initiated',"
                      + " 'analysis_completed', 'analysis_released', 'analysis_cancelled')) order by s.accessionNumber"),
    @NamedQuery( name = "Analysis.FetchReleasedForCaching",
                query = "select distinct new org.openelis.domain.AnalysisCacheVO(a.id, a.statusId, a.sectionId, a.availableDate, a.startedDate, a.completedDate, a.releasedDate, s.id, s.domain, s.accessionNumber," +
                        "s.receivedDate, s.collectionDate, s.collectionTime, t.name,t.timeHolding, t.timeTaAverage, t.method.name)"
                      + " from Analysis a, SampleItem si, Sample s, Test t, Dictionary d"
                      + " where a.sampleItemId = si.id and si.sampleId = s.id and a.testId = t.id and a.statusId = d.id and d.systemName = 'analysis_released' and a.releasedDate >= :releasedDate order by s.accessionNumber"),          
    @NamedQuery( name = "Analysis.FetchForMCLViolation",
                query = "select distinct new org.openelis.domain.MCLViolationReportVO(s.id, s.accessionNumber, s.collectionDate, s.collectionTime, ss.stateLabId, ss.facilityId, d1.entry, ss.samplePointId, ss.location, p.number0, p.name, p.alternateStNum, o.name, a.id, a.sectionId, a.unitOfMeasureId, a.startedDate, a.releasedDate, d2.entry, t.name, t.method.name)"
                      + " from Analysis a, SampleItem si, Sample s, SampleSDWIS ss, PWS p, SampleOrganization so, Organization o, Test t, Dictionary d1, Dictionary d2, Dictionary d3"
                      + " where a.sampleItemId = si.id and si.sampleId = s.id and ss.sampleId = s.id and ss.pwsId = p.id and ss.sampleTypeId = d1.id and so.sampleId = s.id and so.organizationId = o.id and a.testId = t.id and a.unitOfMeasureId = d2.id and"
                      + " so.typeId = d3.id and d3.systemName = 'org_report_to' and a.releasedDate between :startDate and :endDate order by s.accessionNumber, a.releasedDate")})
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

    @Column(name = "pre_analysis_id")
    private Integer                     preAnalysisId;

    @Column(name = "parent_analysis_id")
    private Integer                     parentAnalysisId;

    @Column(name = "parent_result_id")
    private Integer                     parentResultId;

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

  public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.ANALYSIS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_item_id", sampleItemId, original.sampleItemId, ReferenceTable.SAMPLE_ITEM)
                 .setField("revision", revision, original.revision)
                 .setField("test_id", testId, original.testId, ReferenceTable.TEST)
                 .setField("section_id", sectionId, original.sectionId, ReferenceTable.SECTION)
                 .setField("pre_analysis_id", preAnalysisId, original.preAnalysisId, ReferenceTable.ANALYSIS)
                 .setField("parent_analysis_id", parentAnalysisId, original.parentAnalysisId, ReferenceTable.ANALYSIS)
                 .setField("parent_result_id", parentResultId, original.parentResultId, ReferenceTable.RESULT)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("unit_of_measure_id", unitOfMeasureId, original.unitOfMeasureId, ReferenceTable.DICTIONARY)
                 .setField("status_id", statusId, original.statusId, ReferenceTable.DICTIONARY)
                 .setField("available_date", availableDate, original.availableDate)
                 .setField("started_date", startedDate, original.startedDate)
                 .setField("completed_date", completedDate, original.completedDate)
                 .setField("released_date", releasedDate, original.releasedDate)
                 .setField("printed_date", printedDate, original.printedDate);

        return audit;
    }
}   
