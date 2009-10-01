
package org.openelis.entity;

/**
  * WorksheetQc Entity POJO for database 
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
@Table(name="worksheet_qc")
@EntityListeners({AuditUtil.class})
public class WorksheetQc implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="worksheet_analysis_id")
  private Integer worksheetAnalysisId;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="qc_analyte_id")
  private Integer qcAnalyteId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             


  @Transient
  private WorksheetQc original;

  
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

  public Integer getQcAnalyteId() {
    return qcAnalyteId;
  }
  public void setQcAnalyteId(Integer qcAnalyteId) {
    if((qcAnalyteId == null && this.qcAnalyteId != null) || 
       (qcAnalyteId != null && !qcAnalyteId.equals(this.qcAnalyteId)))
      this.qcAnalyteId = qcAnalyteId;
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
      original = (WorksheetQc)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(worksheetAnalysisId,original.worksheetAnalysisId,doc,"worksheet_analysis_id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(qcAnalyteId,original.qcAnalyteId,doc,"qc_analyte_id");

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
    return "worksheet_qc";
  }
  
}   
