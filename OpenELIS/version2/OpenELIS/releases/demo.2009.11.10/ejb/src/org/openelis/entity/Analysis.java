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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Analysis.AnalysisTestBySampleItemId", query = "select new org.openelis.domain.AnalysisViewDO(a.id, a.sampleItemId, a.revision, " + 
                " a.testId, a.sectionId, a.preAnalysisId, a.parentAnalysisId, a.parentResultId, a.isReportable, a.unitOfMeasureId, a.statusId, " + 
                " a.availableDate, a.startedDate, a.completedDate, a.releasedDate, a.printedDate, s.name, t.name, t.method.id, t.method.name) from " +
                " Analysis a LEFT JOIN a.section s LEFT JOIN a.test t where a.sampleItemId = :id")})
                
@Entity
@Table(name="analysis")
@EntityListeners({AuditUtil.class})
public class Analysis implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_item_id")
  private Integer sampleItemId;             

  @Column(name="revision")
  private Integer revision;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="section_id")
  private Integer sectionId;             

  @Column(name="pre_analysis_id")
  private Integer preAnalysisId;             

  @Column(name="parent_analysis_id")
  private Integer parentAnalysisId;             

  @Column(name="parent_result_id")
  private Integer parentResultId;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="unit_of_measure_id")
  private Integer unitOfMeasureId;             

  @Column(name="status_id")
  private Integer statusId;             

  @Column(name="available_date")
  private Date availableDate;             

  @Column(name="started_date")
  private Date startedDate;             

  @Column(name="completed_date")
  private Date completedDate;             

  @Column(name="released_date")
  private Date releasedDate;             

  @Column(name="printed_date")
  private Date printedDate;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id", insertable = false, updatable = false)
  private Test test;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "section_id", insertable = false, updatable = false)
  private Section section;
  
  //analysis qa events
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "analysis_id")
  private Collection<AnalysisQaevent> analysisQAEvent;

  @Transient
  private Analysis original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSampleItemId() {
    return sampleItemId;
  }
  public void setSampleItemId(Integer sampleItemId) {
    if((sampleItemId == null && this.sampleItemId != null) || 
       (sampleItemId != null && !sampleItemId.equals(this.sampleItemId)))
      this.sampleItemId = sampleItemId;
  }

  public Integer getRevision() {
    return revision;
  }
  public void setRevision(Integer revision) {
    if((revision == null && this.revision != null) || 
       (revision != null && !revision.equals(this.revision)))
      this.revision = revision;
  }

  public Integer getTestId() {
    return testId;
  }
  public void setTestId(Integer testId) {
    if((testId == null && this.testId != null) || 
       (testId != null && !testId.equals(this.testId)))
      this.testId = testId;
  }

  public Integer getSectionId() {
    return sectionId;
  }
  public void setSectionId(Integer sectionId) {
    if((sectionId == null && this.sectionId != null) || 
       (sectionId != null && !sectionId.equals(this.sectionId)))
      this.sectionId = sectionId;
  }

  public Integer getPreAnalysisId() {
    return preAnalysisId;
  }
  public void setPreAnalysisId(Integer preAnalysisId) {
    if((preAnalysisId == null && this.preAnalysisId != null) || 
       (preAnalysisId != null && !preAnalysisId.equals(this.preAnalysisId)))
      this.preAnalysisId = preAnalysisId;
  }

  public Integer getParentAnalysisId() {
    return parentAnalysisId;
  }
  public void setParentAnalysisId(Integer parentAnalysisId) {
    if((parentAnalysisId == null && this.parentAnalysisId != null) || 
       (parentAnalysisId != null && !parentAnalysisId.equals(this.parentAnalysisId)))
      this.parentAnalysisId = parentAnalysisId;
  }

  public Integer getParentResultId() {
    return parentResultId;
  }
  public void setParentResultId(Integer parentResultId) {
    if((parentResultId == null && this.parentResultId != null) || 
       (parentResultId != null && !parentResultId.equals(this.parentResultId)))
      this.parentResultId = parentResultId;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getUnitOfMeasureId() {
    return unitOfMeasureId;
  }
  public void setUnitOfMeasureId(Integer unitOfMeasureId) {
    if((unitOfMeasureId == null && this.unitOfMeasureId != null) || 
       (unitOfMeasureId != null && !unitOfMeasureId.equals(this.unitOfMeasureId)))
      this.unitOfMeasureId = unitOfMeasureId;
  }

  public Integer getStatusId() {
    return statusId;
  }
  public void setStatusId(Integer statusId) {
    if((statusId == null && this.statusId != null) || 
       (statusId != null && !statusId.equals(this.statusId)))
      this.statusId = statusId;
  }

  public Datetime getAvailableDate() {
    if(availableDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,availableDate);
  }
  public void setAvailableDate (Datetime available_date){
    if((availableDate == null && this.availableDate != null) || 
       (availableDate != null && !availableDate.equals(this.availableDate)))
      this.availableDate = available_date.getDate();
  }

  public Datetime getStartedDate() {
    if(startedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,startedDate);
  }
  public void setStartedDate (Datetime started_date){
    if((startedDate == null && this.startedDate != null) || 
       (startedDate != null && !startedDate.equals(this.startedDate)))
      this.startedDate = started_date.getDate();
  }

  public Datetime getCompletedDate() {
    if(completedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,completedDate);
  }
  public void setCompletedDate (Datetime completed_date){
    if((completedDate == null && this.completedDate != null) || 
       (completedDate != null && !completedDate.equals(this.completedDate)))
      this.completedDate = completed_date.getDate();
  }

  public Datetime getReleasedDate() {
    if(releasedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,releasedDate);
  }
  public void setReleasedDate (Datetime released_date){
    if((releasedDate == null && this.releasedDate != null) || 
       (releasedDate != null && !releasedDate.equals(this.releasedDate)))
      this.releasedDate = released_date.getDate();
  }

  public Datetime getPrintedDate() {
    if(printedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,printedDate);
  }
  public void setPrintedDate (Datetime printed_date){
    if((printedDate == null && this.printedDate != null) || 
       (printedDate != null && !printedDate.equals(this.printedDate)))
      this.printedDate = printed_date.getDate();
  }

  
  public void setClone() {
    try {
      original = (Analysis)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleItemId,original.sampleItemId,doc,"sample_item_id");

      AuditUtil.getChangeXML(revision,original.revision,doc,"revision");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(sectionId,original.sectionId,doc,"section_id");

      AuditUtil.getChangeXML(preAnalysisId,original.preAnalysisId,doc,"pre_analysis_id");

      AuditUtil.getChangeXML(parentAnalysisId,original.parentAnalysisId,doc,"parent_analysis_id");

      AuditUtil.getChangeXML(parentResultId,original.parentResultId,doc,"parent_result_id");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(unitOfMeasureId,original.unitOfMeasureId,doc,"unit_of_measure_id");

      AuditUtil.getChangeXML(statusId,original.statusId,doc,"status_id");

      AuditUtil.getChangeXML(availableDate,original.availableDate,doc,"available_date");

      AuditUtil.getChangeXML(startedDate,original.startedDate,doc,"started_date");

      AuditUtil.getChangeXML(completedDate,original.completedDate,doc,"completed_date");

      AuditUtil.getChangeXML(releasedDate,original.releasedDate,doc,"released_date");

      AuditUtil.getChangeXML(printedDate,original.printedDate,doc,"printed_date");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "analysis";
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
  
}   
