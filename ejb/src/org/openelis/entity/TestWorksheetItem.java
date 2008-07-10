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
  * TestWorksheetItem Entity POJO for database 
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
@Table(name="test_worksheet_item")
@EntityListeners({AuditUtil.class})
public class TestWorksheetItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_worksheet_id")
  private Integer testWorksheetId;             

  @Column(name="position")
  private Integer position;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="qc_name")
  private String qcName;             


  @Transient
  private TestWorksheetItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTestWorksheetId() {
    return testWorksheetId;
  }
  public void setTestWorksheetId(Integer testWorksheetId) {
    if((testWorksheetId == null && this.testWorksheetId != null) || 
       (testWorksheetId != null && !testWorksheetId.equals(this.testWorksheetId)))
      this.testWorksheetId = testWorksheetId;
  }

  public Integer getPosition() {
    return position;
  }
  public void setPosition(Integer position) {
    if((position == null && this.position != null) || 
       (position != null && !position.equals(this.position)))
      this.position = position;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getQcName() {
    return qcName;
  }
  public void setQcName(String qcName) {
    if((qcName == null && this.qcName != null) || 
       (qcName != null && !qcName.equals(this.qcName)))
      this.qcName = qcName;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheetItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testWorksheetId,original.testWorksheetId,doc,"test_worksheet_id");

      AuditUtil.getChangeXML(position,original.position,doc,"position");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(qcName,original.qcName,doc,"qc_name");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_worksheet_item";
  }
  
}   
