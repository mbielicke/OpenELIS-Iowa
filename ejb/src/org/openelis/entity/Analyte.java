
package org.openelis.entity;

/**
  * Analyte Entity POJO for database 
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
@Table(name="analyte")
@EntityListeners({AuditUtil.class})
public class Analyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="analyte_group")
  private Integer analyteGroup;             

  @Column(name="parent_analyte")
  private Integer parentAnalyte;             

  @Column(name="external_id")
  private String externalId;             


  @Transient
  private Analyte original;

  
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

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Integer getAnalyteGroup() {
    return analyteGroup;
  }
  public void setAnalyteGroup(Integer analyteGroup) {
    if((analyteGroup == null && this.analyteGroup != null) || 
       (analyteGroup != null && !analyteGroup.equals(this.analyteGroup)))
      this.analyteGroup = analyteGroup;
  }

  public Integer getParentAnalyte() {
    return parentAnalyte;
  }
  public void setParentAnalyte(Integer parentAnalyte) {
    if((parentAnalyte == null && this.parentAnalyte != null) || 
       (parentAnalyte != null && !parentAnalyte.equals(this.parentAnalyte)))
      this.parentAnalyte = parentAnalyte;
  }

  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    if((externalId == null && this.externalId != null) || 
       (externalId != null && !externalId.equals(this.externalId)))
      this.externalId = externalId;
  }

  
  public void setClone() {
    try {
      original = (Analyte)this.clone();
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

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }      

      if((analyteGroup == null && original.analyteGroup != null) || 
         (analyteGroup != null && !analyteGroup.equals(original.analyteGroup))){
        Element elem = doc.createElement("analyte_group");
        elem.appendChild(doc.createTextNode(original.analyteGroup.toString()));
        root.appendChild(elem);
      }      

      if((parentAnalyte == null && original.parentAnalyte != null) || 
         (parentAnalyte != null && !parentAnalyte.equals(original.parentAnalyte))){
        Element elem = doc.createElement("parent_analyte");
        elem.appendChild(doc.createTextNode(original.parentAnalyte.toString()));
        root.appendChild(elem);
      }      

      if((externalId == null && original.externalId != null) || 
         (externalId != null && !externalId.equals(original.externalId))){
        Element elem = doc.createElement("external_id");
        elem.appendChild(doc.createTextNode(original.externalId.toString()));
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
    return "analyte";
  }
  
}   
