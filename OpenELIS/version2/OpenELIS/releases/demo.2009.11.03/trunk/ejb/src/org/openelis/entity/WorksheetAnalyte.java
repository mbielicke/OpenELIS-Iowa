
package org.openelis.entity;

/**
  * WorksheetAnalyte Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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
@Table(name="worksheet_analyte")
@EntityListeners({AuditUtil.class})
public class WorksheetAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="worksheet_analysis_id")
  private Integer worksheetAnalysisId;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="value")
  private String value;             

  @Column(name="result_id")
  private Integer resultId;             


  @Transient
  private WorksheetAnalyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getWorksheetAnalysisId() {
    return worksheetAnalysisId;
  }
  public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
    if((worksheetAnalysisId == null && this.worksheetAnalysisId != null) || 
       (worksheetAnalysisId != null && !worksheetAnalysisId.equals(this.worksheetAnalysisId)))
      this.worksheetAnalysisId = worksheetAnalysisId;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
      this.value = value;
  }

  public Integer getResultId() {
    return resultId;
  }
  public void setResultId(Integer resultId) {
    if((resultId == null && this.resultId != null) || 
       (resultId != null && !resultId.equals(this.resultId)))
      this.resultId = resultId;
  }

  
  public void setClone() {
    try {
      original = (WorksheetAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(worksheetAnalysisId,original.worksheetAnalysisId,doc,"worksheet_analysis_id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      AuditUtil.getChangeXML(resultId,original.resultId,doc,"result_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "worksheet_analyte";
  }
  
}   
