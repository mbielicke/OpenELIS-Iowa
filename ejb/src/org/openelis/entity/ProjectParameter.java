
package org.openelis.entity;

/**
  * ProjectParameter Entity POJO for database 
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
@Table(name="project_parameter")
@EntityListeners({AuditUtil.class})
public class ProjectParameter implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="project")
  private Integer project;             

  @Column(name="parameter")
  private String parameter;             

  @Column(name="operation")
  private String operation;             

  @Column(name="value")
  private String value;             


  @Transient
  private ProjectParameter original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getProject() {
    return project;
  }
  public void setProject(Integer project) {
    this.project = project;
  }

  public String getParameter() {
    return parameter;
  }
  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getOperation() {
    return operation;
  }
  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  
  public void setClone() {
    try {
      original = (ProjectParameter)this.clone();
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

      if((project == null && original.project != null) || 
         (project != null && !project.equals(original.project))){
        Element elem = doc.createElement("project");
        elem.appendChild(doc.createTextNode(original.project.toString()));
        root.appendChild(elem);
      }      

      if((parameter == null && original.parameter != null) || 
         (parameter != null && !parameter.equals(original.parameter))){
        Element elem = doc.createElement("parameter");
        elem.appendChild(doc.createTextNode(original.parameter.toString()));
        root.appendChild(elem);
      }      

      if((operation == null && original.operation != null) || 
         (operation != null && !operation.equals(original.operation))){
        Element elem = doc.createElement("operation");
        elem.appendChild(doc.createTextNode(original.operation.toString()));
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
    return "project_parameter";
  }
  
}   
