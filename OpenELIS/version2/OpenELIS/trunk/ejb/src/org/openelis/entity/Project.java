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
import org.openelis.utils.Auditable;

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

  @Column(name="owner_id")
  private Integer ownerId;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             


  @Transient
  private Project original;

  
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

  public Datetime getStartedDate() {
    if(startedDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,startedDate);
  }
  public void setStartedDate (Datetime started_date){
    if((startedDate == null && this.startedDate != null) || 
       (startedDate != null && !startedDate.equals(this.startedDate)))
      this.startedDate = started_date.getDate();
  }

  public Datetime getCompletedDate() {
    if(completedDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,completedDate);
  }
  public void setCompletedDate (Datetime completed_date){
    if((completedDate == null && this.completedDate != null) || 
       (completedDate != null && !completedDate.equals(this.completedDate)))
      this.completedDate = completed_date.getDate();
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getReferenceTo() {
    return referenceTo;
  }
  public void setReferenceTo(String referenceTo) {
    if((referenceTo == null && this.referenceTo != null) || 
       (referenceTo != null && !referenceTo.equals(this.referenceTo)))
      this.referenceTo = referenceTo;
  }

  public Integer getOwnerId() {
    return ownerId;
  }
  public void setOwnerId(Integer ownerId) {
    if((ownerId == null && this.ownerId != null) || 
       (ownerId != null && !ownerId.equals(this.ownerId)))
      this.ownerId = ownerId;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(startedDate,original.startedDate,doc,"started_date");

      AuditUtil.getChangeXML(completedDate,original.completedDate,doc,"completed_date");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(referenceTo,original.referenceTo,doc,"reference_to");

      AuditUtil.getChangeXML(ownerId,original.ownerId,doc,"owner_id");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

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
