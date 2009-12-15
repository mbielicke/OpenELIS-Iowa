
package org.openelis.entity;

/**
  * Result Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({@NamedQuery(name = "Result.FetchByAnalysisId", query = "select new org.openelis.domain.ResultViewDO(r.id,r.analysisId,r.testAnalyteId,r.testResultId, " + 
                        " r.isColumn, r.sortOrder, r.isReportable, r.analyteId, r.typeId, r.value, a.name, ta.rowGroup, tr.resultGroup) "+
                        " from Result r LEFT JOIN r.analysis an LEFT JOIN an.test t LEFT JOIN t.testResult tr LEFT JOIN r.analyte a LEFT JOIN r.testAnalyte ta where r.analysisId = :id order by r.sortOrder"),
               @NamedQuery(name = "Result.ResultByAnalyteId", query = "select r.id from Result r where r.analyteId = :id"),
               @NamedQuery(name = "Result.AnalyteByAnalysisId", query = "select new org.openelis.domain.AnalyteDO(a.id,a.name,a.isActive,a.parentAnalyteId,a.externalId) "+
                        " from Result r LEFT JOIN r.analyte a where r.analysisId = :id order by r.sortOrder")})

@Entity
@Table(name="result")
@EntityListeners({AuditUtil.class})
public class Result implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis_id")
  private Integer analysisId;             

  @Column(name="test_analyte_id")
  private Integer testAnalyteId;             

  @Column(name="test_result_id")
  private Integer testResultId;             

  @Column(name="is_column")
  private String isColumn;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
  private Analysis analysis;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
  private Analyte analyte;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_analyte_id", insertable = false, updatable = false)
  private TestAnalyte testAnalyte;

  @Transient
  private Result original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if(DataBaseUtil.isDifferent(id, this.id))
      this.id = id;
  }

  public Integer getAnalysisId() {
    return analysisId;
  }
  public void setAnalysisId(Integer analysisId) {
    if(DataBaseUtil.isDifferent(analysisId, this.analysisId))
      this.analysisId = analysisId;
  }

  public Integer getTestAnalyteId() {
    return testAnalyteId;
  }
  public void setTestAnalyteId(Integer testAnalyteId) {
    if(DataBaseUtil.isDifferent(testAnalyteId, this.testAnalyteId))
      this.testAnalyteId = testAnalyteId;
  }

  public Integer getTestResultId() {
    return testResultId;
  }
  public void setTestResultId(Integer testResultId) {
    if(DataBaseUtil.isDifferent(testResultId, this.testResultId))
      this.testResultId = testResultId;
  }

  public String getIsColumn() {
    return isColumn;
  }
  public void setIsColumn(String isColumn) {
    if(DataBaseUtil.isDifferent(isColumn, this.isColumn))
      this.isColumn = isColumn;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if(DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
      this.sortOrder = sortOrder;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if(DataBaseUtil.isDifferent(isReportable, this.isReportable))
      this.isReportable = isReportable;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if(DataBaseUtil.isDifferent(analyteId, this.analyteId))
      this.analyteId = analyteId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if(DataBaseUtil.isDifferent(typeId, this.typeId))
      this.typeId = typeId;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if(DataBaseUtil.isDifferent(value, this.value))
      this.value = value;
  }

  
  public void setClone() {
    try {
      original = (Result)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");
      AuditUtil.getChangeXML(analysisId,original.analysisId,doc,"analysis_id");
      AuditUtil.getChangeXML(testAnalyteId,original.testAnalyteId,doc,"test_analyte_id");
      AuditUtil.getChangeXML(testResultId,original.testResultId,doc,"test_result_id");
      AuditUtil.getChangeXML(isColumn,original.isColumn,doc,"is_column");
      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");
      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");
      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");
      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");
      AuditUtil.getChangeXML(value,original.value,doc,"value");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "result";
  }
public Analyte getAnalyte() {
    return analyte;
}
public void setAnalyte(Analyte analyte) {
    this.analyte = analyte;
}
public TestAnalyte getTestAnalyte() {
    return testAnalyte;
}
public void setTestAnalyte(TestAnalyte testAnalyte) {
    this.testAnalyte = testAnalyte;
}
public Analysis getAnalysis() {
    return analysis;
}
public void setAnalysis(Analysis analysis) {
    this.analysis = analysis;
}
  
}   
