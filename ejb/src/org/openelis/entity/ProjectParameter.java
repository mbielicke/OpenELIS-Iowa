/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

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
import org.openelis.utils.Auditable;

@Entity
@Table(name="project_parameter")
@EntityListeners({AuditUtil.class})
public class ProjectParameter implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="project_id")
  private Integer projectId;             

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
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getProjectId() {
    return projectId;
  }
  public void setProjectId(Integer projectId) {
    if((projectId == null && this.projectId != null) || 
       (projectId != null && !projectId.equals(this.projectId)))
      this.projectId = projectId;
  }

  public String getParameter() {
    return parameter;
  }
  public void setParameter(String parameter) {
    if((parameter == null && this.parameter != null) || 
       (parameter != null && !parameter.equals(this.parameter)))
      this.parameter = parameter;
  }

  public String getOperation() {
    return operation;
  }
  public void setOperation(String operation) {
    if((operation == null && this.operation != null) || 
       (operation != null && !operation.equals(this.operation)))
      this.operation = operation;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(projectId,original.projectId,doc,"project_id");

      AuditUtil.getChangeXML(parameter,original.parameter,doc,"parameter");

      AuditUtil.getChangeXML(operation,original.operation,doc,"operation");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

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
