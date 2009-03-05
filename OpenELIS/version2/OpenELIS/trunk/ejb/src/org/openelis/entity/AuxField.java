
package org.openelis.entity;

/**
  * AuxField Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
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
@Table(name="aux_field")
@EntityListeners({AuditUtil.class})
public class AuxField implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="description")
  private String description;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="method_id")
  private Integer methodId;             

  @Column(name="unit_of_measure_id")
  private Integer unitOfMeasureId;             

  @Column(name="is_required")
  private String isRequired;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             


  @Transient
  private AuxField original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
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

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  public Integer getMethodId() {
    return methodId;
  }
  public void setMethodId(Integer methodId) {
    if((methodId == null && this.methodId != null) || 
       (methodId != null && !methodId.equals(this.methodId)))
      this.methodId = methodId;
  }

  public Integer getUnitOfMeasureId() {
    return unitOfMeasureId;
  }
  public void setUnitOfMeasureId(Integer unitOfMeasureId) {
    if((unitOfMeasureId == null && this.unitOfMeasureId != null) || 
       (unitOfMeasureId != null && !unitOfMeasureId.equals(this.unitOfMeasureId)))
      this.unitOfMeasureId = unitOfMeasureId;
  }

  public String getIsRequired() {
    return isRequired;
  }
  public void setIsRequired(String isRequired) {
    if((isRequired == null && this.isRequired != null) || 
       (isRequired != null && !isRequired.equals(this.isRequired)))
      this.isRequired = isRequired;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
  }

  
  public void setClone() {
    try {
      original = (AuxField)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      AuditUtil.getChangeXML(methodId,original.methodId,doc,"method_id");

      AuditUtil.getChangeXML(unitOfMeasureId,original.unitOfMeasureId,doc,"unit_of_measure_id");

      AuditUtil.getChangeXML(isRequired,original.isRequired,doc,"is_required");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "aux_field";
  }
  
}   
