
package org.openelis.entity;

/**
  * Method Entity POJO for database 
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
@Table(name="method")
@EntityListeners({AuditUtil.class})
public class Method implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="reporting_description")
  private String reportingDescription;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="active_begin")
  private Date activeBegin;             

  @Column(name="active_end")
  private Date activeEnd;             


  @Transient
  private Method original;

  
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

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getReportingDescription() {
    return reportingDescription;
  }
  public void setReportingDescription(String reportingDescription) {
    if((reportingDescription == null && this.reportingDescription != null) || 
       (reportingDescription != null && !reportingDescription.equals(this.reportingDescription)))
      this.reportingDescription = reportingDescription;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Datetime getActiveBegin() {
    if(activeBegin == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeBegin);
  }
  public void setActiveBegin (Datetime active_begin){
    if((activeBegin == null && this.activeBegin != null) || 
       (activeBegin != null && !activeBegin.equals(this.activeBegin)))
      this.activeBegin = active_begin.getDate();
  }

  public Datetime getActiveEnd() {
    if(activeEnd == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeEnd);
  }
  public void setActiveEnd (Datetime active_end){
    if((activeEnd == null && this.activeEnd != null) || 
       (activeEnd != null && !activeEnd.equals(this.activeEnd)))
      this.activeEnd = active_end.getDate();
  }

  
  public void setClone() {
    try {
      original = (Method)this.clone();
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

      if((reportingDescription == null && original.reportingDescription != null) || 
         (reportingDescription != null && !reportingDescription.equals(original.reportingDescription))){
        Element elem = doc.createElement("reporting_description");
        elem.appendChild(doc.createTextNode(original.reportingDescription.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }      

      if((activeBegin == null && original.activeBegin != null) || 
         (activeBegin != null && !activeBegin.equals(original.activeBegin))){
        Element elem = doc.createElement("active_begin");
        elem.appendChild(doc.createTextNode(original.activeBegin.toString()));
        root.appendChild(elem);
      }      

      if((activeEnd == null && original.activeEnd != null) || 
         (activeEnd != null && !activeEnd.equals(original.activeEnd))){
        Element elem = doc.createElement("active_end");
        elem.appendChild(doc.createTextNode(original.activeEnd.toString()));
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
    return "method";
  }
  
}   
