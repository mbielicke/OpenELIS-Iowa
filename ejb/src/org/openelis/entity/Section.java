
package org.openelis.entity;

/**
  * Section Entity POJO for database 
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
@Table(name="section")
@EntityListeners({AuditUtil.class})
public class Section implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="parent_section")
  private Integer parentSection;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="organization")
  private Integer organization;             


  @Transient
  private Section original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getParentSection() {
    return parentSection;
  }
  public void setParentSection(Integer parentSection) {
    if((parentSection == null && this.parentSection != null) || 
       (parentSection != null && !parentSection.equals(this.parentSection)))
      this.parentSection = parentSection;
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

  public String getIsExternal() {
    return isExternal;
  }
  public void setIsExternal(String isExternal) {
    if((isExternal == null && this.isExternal != null) || 
       (isExternal != null && !isExternal.equals(this.isExternal)))
      this.isExternal = isExternal;
  }

  public Integer getOrganization() {
    return organization;
  }
  public void setOrganization(Integer organization) {
    if((organization == null && this.organization != null) || 
       (organization != null && !organization.equals(this.organization)))
      this.organization = organization;
  }

  
  public void setClone() {
    try {
      original = (Section)this.clone();
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

      if((parentSection == null && original.parentSection != null) || 
         (parentSection != null && !parentSection.equals(original.parentSection))){
        Element elem = doc.createElement("parent_section");
        elem.appendChild(doc.createTextNode(original.parentSection.toString()));
        root.appendChild(elem);
      }      

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((isExternal == null && original.isExternal != null) || 
         (isExternal != null && !isExternal.equals(original.isExternal))){
        Element elem = doc.createElement("is_external");
        elem.appendChild(doc.createTextNode(original.isExternal.toString()));
        root.appendChild(elem);
      }      

      if((organization == null && original.organization != null) || 
         (organization != null && !organization.equals(original.organization))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organization.toString()));
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
    return "section";
  }
  
}   
