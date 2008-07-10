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
  * TestWorksheet Entity POJO for database 
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
@Table(name="test_worksheet")
@EntityListeners({AuditUtil.class})
public class TestWorksheet implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="batch_capacity")
  private Integer batchCapacity;             

  @Column(name="total_capacity")
  private Integer totalCapacity;             

  @Column(name="number_format_id")
  private Integer numberFormatId;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             


  @Transient
  private TestWorksheet original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTestId() {
    return testId;
  }
  public void setTestId(Integer testId) {
    if((testId == null && this.testId != null) || 
       (testId != null && !testId.equals(this.testId)))
      this.testId = testId;
  }

  public Integer getBatchCapacity() {
    return batchCapacity;
  }
  public void setBatchCapacity(Integer batchCapacity) {
    if((batchCapacity == null && this.batchCapacity != null) || 
       (batchCapacity != null && !batchCapacity.equals(this.batchCapacity)))
      this.batchCapacity = batchCapacity;
  }

  public Integer getTotalCapacity() {
    return totalCapacity;
  }
  public void setTotalCapacity(Integer totalCapacity) {
    if((totalCapacity == null && this.totalCapacity != null) || 
       (totalCapacity != null && !totalCapacity.equals(this.totalCapacity)))
      this.totalCapacity = totalCapacity;
  }

  public Integer getNumberFormatId() {
    return numberFormatId;
  }
  public void setNumberFormatId(Integer numberFormatId) {
    if((numberFormatId == null && this.numberFormatId != null) || 
       (numberFormatId != null && !numberFormatId.equals(this.numberFormatId)))
      this.numberFormatId = numberFormatId;
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
      original = (TestWorksheet)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(batchCapacity,original.batchCapacity,doc,"batch_capacity");

      AuditUtil.getChangeXML(totalCapacity,original.totalCapacity,doc,"total_capacity");

      AuditUtil.getChangeXML(numberFormatId,original.numberFormatId,doc,"number_format_id");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_worksheet";
  }
  
}   
