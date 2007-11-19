
package org.openelis.entity;

/**
  * Organization Entity POJO for database 
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
@Table(name="organization")
@EntityListeners({AuditUtil.class})
public class Organization implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="parent_organization")
  private Integer parentOrganization;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="address")
  private Integer address;             


  @Transient
  private Organization original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getParentOrganization() {
    return parentOrganization;
  }
  public void setParentOrganization(Integer parentOrganization) {
    if((parentOrganization == null && this.parentOrganization != null) || 
       (parentOrganization != null && !parentOrganization.equals(this.parentOrganization)))
      this.parentOrganization = parentOrganization;
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

  public Integer getAddress() {
    return address;
  }
  public void setAddress(Integer address) {
    if((address == null && this.address != null) || 
       (address != null && !address.equals(this.address)))
      this.address = address;
  }

  
  public void setClone() {
    try {
      original = (Organization)this.clone();
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

      if((parentOrganization == null && original.parentOrganization != null) || 
         (parentOrganization != null && !parentOrganization.equals(original.parentOrganization))){
        Element elem = doc.createElement("parent_organization");
        elem.appendChild(doc.createTextNode(original.parentOrganization.toString()));
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

      if((address == null && original.address != null) || 
         (address != null && !address.equals(original.address))){
        Element elem = doc.createElement("address");
        elem.appendChild(doc.createTextNode(original.address.toString()));
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
    return "organization";
  }
  
}   
