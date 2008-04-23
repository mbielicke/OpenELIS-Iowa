
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

	@NamedQueries({@NamedQuery(name = "getAuxFieldByAnalyteId", query = "select a.id from AuxField a where a.analyte = :id")})

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

  @Column(name="analyte")
  private Integer analyte;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="is_required")
  private String isRequired;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="scriptlet")
  private Integer scriptlet;             


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

  public Integer getAnalyte() {
    return analyte;
  }
  public void setAnalyte(Integer analyte) {
    if((analyte == null && this.analyte != null) || 
       (analyte != null && !analyte.equals(this.analyte)))
      this.analyte = analyte;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    if((referenceTable == null && this.referenceTable != null) || 
       (referenceTable != null && !referenceTable.equals(this.referenceTable)))
      this.referenceTable = referenceTable;
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

  public Integer getScriptlet() {
    return scriptlet;
  }
  public void setScriptlet(Integer scriptlet) {
    if((scriptlet == null && this.scriptlet != null) || 
       (scriptlet != null && !scriptlet.equals(this.scriptlet)))
      this.scriptlet = scriptlet;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString().trim()));
        root.appendChild(elem);
      }      

      if((analyte == null && original.analyte != null) || 
         (analyte != null && !analyte.equals(original.analyte))){
        Element elem = doc.createElement("analyte");
        elem.appendChild(doc.createTextNode(original.analyte.toString().trim()));
        root.appendChild(elem);
      }      

      if((referenceTable == null && original.referenceTable != null) || 
         (referenceTable != null && !referenceTable.equals(original.referenceTable))){
        Element elem = doc.createElement("reference_table");
        elem.appendChild(doc.createTextNode(original.referenceTable.toString().trim()));
        root.appendChild(elem);
      }      

      if((isRequired == null && original.isRequired != null) || 
         (isRequired != null && !isRequired.equals(original.isRequired))){
        Element elem = doc.createElement("is_required");
        elem.appendChild(doc.createTextNode(original.isRequired.toString().trim()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString().trim()));
        root.appendChild(elem);
      }      

      if((isReportable == null && original.isReportable != null) || 
         (isReportable != null && !isReportable.equals(original.isReportable))){
        Element elem = doc.createElement("is_reportable");
        elem.appendChild(doc.createTextNode(original.isReportable.toString().trim()));
        root.appendChild(elem);
      }      

      if((scriptlet == null && original.scriptlet != null) || 
         (scriptlet != null && !scriptlet.equals(original.scriptlet))){
        Element elem = doc.createElement("scriptlet");
        elem.appendChild(doc.createTextNode(original.scriptlet.toString().trim()));
        root.appendChild(elem);
      }      

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
