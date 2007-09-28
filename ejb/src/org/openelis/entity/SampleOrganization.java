
package org.openelis.entity;

/**
  * SampleOrganization Entity POJO for database 
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
@Table(name="sample_organization")
@EntityListeners({AuditUtil.class})
public class SampleOrganization implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample")
  private Integer sample;             

  @Column(name="organization")
  private Integer organization;             

  @Column(name="type")
  private Integer type;             


  @Transient
  private SampleOrganization original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getSample() {
    return sample;
  }
  public void setSample(Integer sample) {
    this.sample = sample;
  }

  public Integer getOrganization() {
    return organization;
  }
  public void setOrganization(Integer organization) {
    this.organization = organization;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  
  public void setClone() {
    try {
      original = (SampleOrganization)this.clone();
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

      if((sample == null && original.sample != null) || 
         (sample != null && !sample.equals(original.sample))){
        Element elem = doc.createElement("sample");
        elem.appendChild(doc.createTextNode(original.sample.toString()));
        root.appendChild(elem);
      }      

      if((organization == null && original.organization != null) || 
         (organization != null && !organization.equals(original.organization))){
        Element elem = doc.createElement("organization");
        elem.appendChild(doc.createTextNode(original.organization.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
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
    return "sample_organization";
  }
  
}   
