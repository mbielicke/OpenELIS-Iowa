
package org.openelis.entity;

/**
  * AuxData Entity POJO for database 
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
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="aux_data")
@EntityListeners({AuditUtil.class})
public class AuxData implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="aux_field")
  private Integer auxField;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="type")
  private Integer type;             

  @Column(name="value")
  private String value;             


  @Transient
  private AuxData original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public Integer getAuxField() {
    return auxField;
  }
  public void setAuxField(Integer auxField) {
    this.auxField = auxField;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    this.referenceId = referenceId;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    this.referenceTable = referenceTable;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    this.isReportable = isReportable;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  
  public void setClone() {
    try {
      original = (AuxData)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString()));
        root.appendChild(elem);
      }      

      if((auxField == null && original.auxField != null) || 
         (auxField != null && !auxField.equals(original.auxField))){
        Element elem = doc.createElement("aux_field");
        elem.appendChild(doc.createTextNode(original.auxField.toString()));
        root.appendChild(elem);
      }      

      if((referenceId == null && original.referenceId != null) || 
         (referenceId != null && !referenceId.equals(original.referenceId))){
        Element elem = doc.createElement("reference_id");
        elem.appendChild(doc.createTextNode(original.referenceId.toString()));
        root.appendChild(elem);
      }      

      if((referenceTable == null && original.referenceTable != null) || 
         (referenceTable != null && !referenceTable.equals(original.referenceTable))){
        Element elem = doc.createElement("reference_table");
        elem.appendChild(doc.createTextNode(original.referenceTable.toString()));
        root.appendChild(elem);
      }      

      if((isReportable == null && original.isReportable != null) || 
         (isReportable != null && !isReportable.equals(original.isReportable))){
        Element elem = doc.createElement("is_reportable");
        elem.appendChild(doc.createTextNode(original.isReportable.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((value == null && original.value != null) || 
         (value != null && !value.equals(original.value))){
        Element elem = doc.createElement("value");
        elem.appendChild(doc.createTextNode(original.value.toString()));
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
    return "aux_data";
  }
  
}   
