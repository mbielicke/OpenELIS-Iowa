
package org.openelis.entity;

/**
  * OrganizationContact Entity POJO for database 
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
@Table(name="organization_contact")
@EntityListeners({AuditUtil.class})
public class OrganizationContact implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="organization")
  private Integer organization;             

  @Column(name="contact_type")
  private Integer contactType;             

  @Column(name="name")
  private String name;             


  @Transient
  private OrganizationContact original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getOrganization() {
    return organization;
  }
  public void setOrganization(Integer organization) {
    this.organization = organization;
  }

  public Integer getContactType() {
    return contactType;
  }
  public void setContactType(Integer contactType) {
    this.contactType = contactType;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  public void setClone() {
    try {
      original = (OrganizationContact)this.clone();
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

      if((organization == null && original.organization != null) || 
         (organization != null && !organization.equals(original.organization))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organization.toString()));
        root.appendChild(elem);
      }      

      if((contactType == null && original.contactType != null) || 
         (contactType != null && !contactType.equals(original.contactType))){
        Element elem = doc.createElement("contact_type");
        elem.appendChild(doc.createTextNode(original.contactType.toString()));
        root.appendChild(elem);
      }      

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
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
    return "organization_contact";
  }
  
}   
