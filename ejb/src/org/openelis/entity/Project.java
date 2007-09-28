
package org.openelis.entity;

/**
  * Project Entity POJO for database 
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
@Table(name="project")
@EntityListeners({AuditUtil.class})
public class Project implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="started_date")
  private Date startedDate;             

  @Column(name="completed_date")
  private Date completedDate;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="reference_to")
  private String referenceTo;             

  @Column(name="owner")
  private Integer owner;             

  @Column(name="scriptlet")
  private Integer scriptlet;             


  @Transient
  private Project original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public Datetime getStartedDate() {
    if(startedDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,startedDate);
  }
  public void setStartedDate (Datetime started_date){
    this.startedDate = started_date.getDate();
  }

  public Datetime getCompletedDate() {
    if(completedDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,completedDate);
  }
  public void setCompletedDate (Datetime completed_date){
    this.completedDate = completed_date.getDate();
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public String getReferenceTo() {
    return referenceTo;
  }
  public void setReferenceTo(String referenceTo) {
    this.referenceTo = referenceTo;
  }

  public Integer getOwner() {
    return owner;
  }
  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  public Integer getScriptlet() {
    return scriptlet;
  }
  public void setScriptlet(Integer scriptlet) {
    this.scriptlet = scriptlet;
  }

  
  public void setClone() {
    try {
      original = (Project)this.clone();
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

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((startedDate == null && original.startedDate != null) || 
         (startedDate != null && !startedDate.equals(original.startedDate))){
        Element elem = doc.createElement("started_date");
        elem.appendChild(doc.createTextNode(original.startedDate.toString()));
        root.appendChild(elem);
      }      

      if((completedDate == null && original.completedDate != null) || 
         (completedDate != null && !completedDate.equals(original.completedDate))){
        Element elem = doc.createElement("completed_date");
        elem.appendChild(doc.createTextNode(original.completedDate.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }      

      if((referenceTo == null && original.referenceTo != null) || 
         (referenceTo != null && !referenceTo.equals(original.referenceTo))){
        Element elem = doc.createElement("reference_to");
        elem.appendChild(doc.createTextNode(original.referenceTo.toString()));
        root.appendChild(elem);
      }      

      if((owner == null && original.owner != null) || 
         (owner != null && !owner.equals(original.owner))){
        Element elem = doc.createElement("owner");
        elem.appendChild(doc.createTextNode(original.owner.toString()));
        root.appendChild(elem);
      }      

      if((scriptlet == null && original.scriptlet != null) || 
         (scriptlet != null && !scriptlet.equals(original.scriptlet))){
        Element elem = doc.createElement("scriptlet");
        elem.appendChild(doc.createTextNode(original.scriptlet.toString()));
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
    return "project";
  }
  
}   
