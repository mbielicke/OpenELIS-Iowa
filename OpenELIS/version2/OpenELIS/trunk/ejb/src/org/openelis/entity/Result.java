
package org.openelis.entity;

/**
  * Result Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

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


  @Transient
  private Result original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAnalysisId() {
    return analysisId;
  }
  public void setAnalysisId(Integer analysisId) {
    if((analysisId == null && this.analysisId != null) || 
       (analysisId != null && !analysisId.equals(this.analysisId)))
      this.analysisId = analysisId;
  }

  public Integer getTestAnalyteId() {
    return testAnalyteId;
  }
  public void setTestAnalyteId(Integer testAnalyteId) {
    if((testAnalyteId == null && this.testAnalyteId != null) || 
       (testAnalyteId != null && !testAnalyteId.equals(this.testAnalyteId)))
      this.testAnalyteId = testAnalyteId;
  }

  public Integer getTestResultId() {
    return testResultId;
  }
  public void setTestResultId(Integer testResultId) {
    if((testResultId == null && this.testResultId != null) || 
       (testResultId != null && !testResultId.equals(this.testResultId)))
      this.testResultId = testResultId;
  }

  public String getIsColumn() {
    return isColumn;
  }
  public void setIsColumn(String isColumn) {
    if((isColumn == null && this.isColumn != null) || 
       (isColumn != null && !isColumn.equals(this.isColumn)))
      this.isColumn = isColumn;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
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
  
}   
