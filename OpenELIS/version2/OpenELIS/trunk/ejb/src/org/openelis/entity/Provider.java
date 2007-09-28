
package org.openelis.entity;

/**
  * Provider Entity POJO for database 
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
@Table(name="provider")
@EntityListeners({AuditUtil.class})
public class Provider implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="person")
  private Integer person;             

  @Column(name="type")
  private Integer type;             

  @Column(name="external_id")
  private String externalId;             

  @Column(name="npi")
  private String npi;             


  @Transient
  private Provider original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getPerson() {
    return person;
  }
  public void setPerson(Integer person) {
    this.person = person;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getNpi() {
    return npi;
  }
  public void setNpi(String npi) {
    this.npi = npi;
  }

  
  public void setClone() {
    try {
      original = (Provider)this.clone();
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

      if((person == null && original.person != null) || 
         (person != null && !person.equals(original.person))){
        Element elem = doc.createElement("person");
        elem.appendChild(doc.createTextNode(original.person.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((externalId == null && original.externalId != null) || 
         (externalId != null && !externalId.equals(original.externalId))){
        Element elem = doc.createElement("external_id");
        elem.appendChild(doc.createTextNode(original.externalId.toString()));
        root.appendChild(elem);
      }      

      if((npi == null && original.npi != null) || 
         (npi != null && !npi.equals(original.npi))){
        Element elem = doc.createElement("npi");
        elem.appendChild(doc.createTextNode(original.npi.toString()));
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
    return "provider";
  }
  
}   
