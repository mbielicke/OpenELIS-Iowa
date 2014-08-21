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
  * Test Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Method;
import org.openelis.util.Datetime;
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

@NamedQueries({@NamedQuery(name = "Test.IdByTestTrailer", query = "select t.id from Test t where t.testTrailerId = :id"),
    @NamedQuery(name = "Test.IdByLabel", query =  "select distinct t.id from Test t where t.labelId = :id"),
    @NamedQuery(name = "Test.Names", query = "select distinct new org.openelis.domain.QaEventTestDropdownDO(t.id, t.name, m.name) " + 
                "  from Test t, Method m where t.methodId  = m.id and t.isActive = :isActive order by t.name, m.name"),
    @NamedQuery(name = "Test.TestMethodIdName", query = "select distinct new org.openelis.domain.TestIdNameMethodIdDO(t.id, t.name, t.methodId,m.name) " + "  from Test t left join t.method m where t.id = :id"),
    @NamedQuery(name = "Test.TestDetails", query = "select distinct new org.openelis.domain.TestDetailsDO(t.description,t.reportingDescription,t.isActive,t.activeBegin,t.activeEnd,t.isReportable," +
                                                  "t.timeTransit,t.timeHolding,"+"t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,t.labelQty,t.testTrailerId,t.scriptletId," +
                                                        "t.testFormatId,t.revisionMethodId,t.reportingMethodId,t.sortingMethodId,t.reportingSequence) " + "  from Test t where t.id = :id"),
    @NamedQuery(name = "Test.IdName", query = "select distinct new org.openelis.domain.IdNameDO(t.id, t.name) " + "  from Test t left join t.method order by t.name "),
    @NamedQuery(name = "Test.TestByName", query = "from Test t where t.name = :name order by t.name"),
    @NamedQuery(name = "Test.TestIdNameMethodSectionNames", query = "select distinct new org.openelis.domain.TestMethodSectionNamesDO(t.id,t.name,m.name,s.name)" 
             + "  from Test t left join t.method m left join t.testSection ts left join ts.section s where t.isActive = :isActive order by t.name,m.name,s.name ")})
    

@Entity
@Table(name="test")
@EntityListeners({AuditUtil.class})
public class Test implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="reporting_description")
  private String reportingDescription;             

  @Column(name="method_id")
  private Integer methodId;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="active_begin")
  private Date activeBegin;             

  @Column(name="active_end")
  private Date activeEnd;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="time_transit")
  private Integer timeTransit;             

  @Column(name="time_holding")
  private Integer timeHolding;             

  @Column(name="time_ta_average")
  private Integer timeTaAverage;             

  @Column(name="time_ta_warning")
  private Integer timeTaWarning;             

  @Column(name="time_ta_max")
  private Integer timeTaMax;             

  @Column(name="label_id")
  private Integer labelId;             

  @Column(name="label_qty")
  private Integer labelQty;             

  @Column(name="test_trailer_id")
  private Integer testTrailerId;                       

  @Column(name="scriptlet_id")
  private Integer scriptletId;             

  @Column(name="test_format_id")
  private Integer testFormatId;             

  @Column(name="revision_method_id")
  private Integer revisionMethodId; 
  
  @Column(name="reporting_method_id")
  private Integer reportingMethodId; 
  
  @Column(name="sorting_method_id")
  private Integer sortingMethodId;
  
  @Column(name="reporting_sequence")
  private Integer reportingSequence;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "method_id",insertable = false, updatable = false)
  private Method method;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestPrep> testPrep;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestTypeOfSample> testTypeOfSample;  
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestReflex> testReflex;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestWorksheet> testWorksheet;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestAnalyte> testAnalyte;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestSection> testSection;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Collection<TestResult> testResult;
  
  @Transient
  private Test original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getReportingDescription() {
    return reportingDescription;
  }
  public void setReportingDescription(String reportingDescription) {
    if((reportingDescription == null && this.reportingDescription != null) || 
       (reportingDescription != null && !reportingDescription.equals(this.reportingDescription)))
      this.reportingDescription = reportingDescription;
  }

  public Integer getMethodId() {
    return methodId;
  }
  public void setMethodId(Integer methodId) {
    if((methodId == null && this.methodId != null) || 
       (methodId != null && !methodId.equals(this.methodId)))
      this.methodId = methodId;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Datetime getActiveBegin() {
    if(activeBegin == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeBegin);
  }
  public void setActiveBegin (Datetime active_begin){
    if((active_begin == null && this.activeBegin != null) || (active_begin != null && this.activeBegin == null) ||
       (active_begin != null && !active_begin.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.activeBegin))))
      this.activeBegin = active_begin.getDate();
  }

  public Datetime getActiveEnd() {
    if(activeEnd == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeEnd);
  }
  public void setActiveEnd (Datetime active_end){
    if((active_end == null && this.activeEnd != null) || (active_end != null && this.activeEnd == null) ||
       (active_end != null && !active_end.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.activeEnd))))
      this.activeEnd = active_end.getDate();
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getTimeTransit() {
    return timeTransit;
  }
  public void setTimeTransit(Integer timeTransit) {
    if((timeTransit == null && this.timeTransit != null) || 
       (timeTransit != null && !timeTransit.equals(this.timeTransit)))
      this.timeTransit = timeTransit;
  }

  public Integer getTimeHolding() {
    return timeHolding;
  }
  public void setTimeHolding(Integer timeHolding) {
    if((timeHolding == null && this.timeHolding != null) || 
       (timeHolding != null && !timeHolding.equals(this.timeHolding)))
      this.timeHolding = timeHolding;
  }

  public Integer getTimeTaAverage() {
    return timeTaAverage;
  }
  public void setTimeTaAverage(Integer timeTaAverage) {
    if((timeTaAverage == null && this.timeTaAverage != null) || 
       (timeTaAverage != null && !timeTaAverage.equals(this.timeTaAverage)))
      this.timeTaAverage = timeTaAverage;
  }

  public Integer getTimeTaWarning() {
    return timeTaWarning;
  }
  public void setTimeTaWarning(Integer timeTaWarning) {
    if((timeTaWarning == null && this.timeTaWarning != null) || 
       (timeTaWarning != null && !timeTaWarning.equals(this.timeTaWarning)))
      this.timeTaWarning = timeTaWarning;
  }

  public Integer getTimeTaMax() {
    return timeTaMax;
  }
  public void setTimeTaMax(Integer timeTaMax) {
    if((timeTaMax == null && this.timeTaMax != null) || 
       (timeTaMax != null && !timeTaMax.equals(this.timeTaMax)))
      this.timeTaMax = timeTaMax;
  }

  public Integer getLabelId() {
    return labelId;
  }
  public void setLabelId(Integer labelId) {
    if((labelId == null && this.labelId != null) || 
       (labelId != null && !labelId.equals(this.labelId)))
      this.labelId = labelId;
  }

  public Integer getLabelQty() {
    return labelQty;
  }
  public void setLabelQty(Integer labelQty) {
    if((labelQty == null && this.labelQty != null) || 
       (labelQty != null && !labelQty.equals(this.labelQty)))
      this.labelQty = labelQty;
  }

  public Integer getTestTrailerId() {
    return testTrailerId;
  }
  public void setTestTrailerId(Integer testTrailerId) {
    if((testTrailerId == null && this.testTrailerId != null) || 
       (testTrailerId != null && !testTrailerId.equals(this.testTrailerId)))
      this.testTrailerId = testTrailerId;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
  }

  public Integer getTestFormatId() {
    return testFormatId;
  }
  public void setTestFormatId(Integer testFormatId) {
    if((testFormatId == null && this.testFormatId != null) || 
       (testFormatId != null && !testFormatId.equals(this.testFormatId)))
      this.testFormatId = testFormatId;
  }

  public Integer getRevisionMethodId() {
    return revisionMethodId;
  }
  public void setRevisionMethodId(Integer revisionMethodId) {
    if((revisionMethodId == null && this.revisionMethodId != null) || 
       (revisionMethodId != null && !revisionMethodId.equals(this.revisionMethodId)))
      this.revisionMethodId = revisionMethodId;
  }
  
  public Integer getReportingMethodId() {
      return reportingMethodId;
    }
    public void setReportingMethodId(Integer reportingMethodId) {
      if((reportingMethodId == null && this.reportingMethodId != null) || 
         (reportingMethodId != null && !reportingMethodId.equals(this.reportingMethodId)))
        this.reportingMethodId = reportingMethodId;
    }
    
    public Integer getSortingMethodId() {
        return sortingMethodId;
      }
    public void setSortingMethodId(Integer sortingMethodId) {
        if((sortingMethodId == null && this.sortingMethodId != null) || 
           (sortingMethodId != null && !sortingMethodId.equals(this.sortingMethodId)))
          this.sortingMethodId = sortingMethodId;
      }
     
    public Integer getReportingSequence() {
          return reportingSequence;
        }
        public void setReportingSequence(Integer reportingSequence) {
          if((reportingSequence == null && this.reportingSequence != null) || 
             (reportingSequence != null && !reportingSequence.equals(this.reportingSequence)))
            this.reportingSequence = reportingSequence;
        }
  
  public void setClone() {
    try {
      original = (Test)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(reportingDescription,original.reportingDescription,doc,"reporting_description");

      AuditUtil.getChangeXML(methodId,original.methodId,doc,"method_id");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(activeBegin,original.activeBegin,doc,"active_begin");

      AuditUtil.getChangeXML(activeEnd,original.activeEnd,doc,"active_end");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(timeTransit,original.timeTransit,doc,"time_transit");

      AuditUtil.getChangeXML(timeHolding,original.timeHolding,doc,"time_holding");

      AuditUtil.getChangeXML(timeTaAverage,original.timeTaAverage,doc,"time_ta_average");

      AuditUtil.getChangeXML(timeTaWarning,original.timeTaWarning,doc,"time_ta_warning");

      AuditUtil.getChangeXML(timeTaMax,original.timeTaMax,doc,"time_ta_max");

      AuditUtil.getChangeXML(labelId,original.labelId,doc,"label_id");

      AuditUtil.getChangeXML(labelQty,original.labelQty,doc,"label_qty");

      AuditUtil.getChangeXML(testTrailerId,original.testTrailerId,doc,"test_trailer_id");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      AuditUtil.getChangeXML(testFormatId,original.testFormatId,doc,"test_format_id");

      AuditUtil.getChangeXML(revisionMethodId,original.revisionMethodId,doc,"revision_method_id");
      
      AuditUtil.getChangeXML(reportingMethodId,original.reportingMethodId,doc,"reporting_method_id");
      
      AuditUtil.getChangeXML(sortingMethodId,original.sortingMethodId,doc,"sorting_method_id");
      
      AuditUtil.getChangeXML(reportingSequence,original.reportingSequence,doc,"reporting_sequence");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test";
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

  
}   
