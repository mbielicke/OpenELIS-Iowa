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
  * AnalysisUser Entity POJO for database 
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
@Table(name="analysis_user")
@EntityListeners({AuditUtil.class})
public class AnalysisUser implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis_id")
  private Integer analysisId;             

  @Column(name="system_user_id")
  private Integer systemUserId;             

  @Column(name="action_id")
  private Integer actionId;             


  @Transient
  private AnalysisUser original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAnalysisId() {
    return analysisId;
  }
  public void setAnalysisId(Integer analysisId) {
    if((analysisId == null && this.analysisId != null) || 
       (analysisId != null && !analysisId.equals(this.analysisId)))
      this.analysisId = analysisId;
  }

  public Integer getSystemUserId() {
    return systemUserId;
  }
  public void setSystemUserId(Integer systemUserId) {
    if((systemUserId == null && this.systemUserId != null) || 
       (systemUserId != null && !systemUserId.equals(this.systemUserId)))
      this.systemUserId = systemUserId;
  }

  public Integer getActionId() {
    return actionId;
  }
  public void setActionId(Integer actionId) {
    if((actionId == null && this.actionId != null) || 
       (actionId != null && !actionId.equals(this.actionId)))
      this.actionId = actionId;
  }

  
  public void setClone() {
    try {
      original = (AnalysisUser)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(analysisId,original.analysisId,doc,"analysis_id");

      AuditUtil.getChangeXML(systemUserId,original.systemUserId,doc,"system_user_id");

      AuditUtil.getChangeXML(actionId,original.actionId,doc,"action_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "analysis_user";
  }
  
}   
