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
  * TestAnalyte Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
@NamedQueries({@NamedQuery(name = "TestAnalyte.TestAnalyteByAnalyteId", query = "select t.id from TestAnalyte t where t.analyteId = :id")})

@Entity
@Table(name="test_analyte")
@EntityListeners({AuditUtil.class})
public class TestAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="result_group_id")
  private Integer resultGroupId;             

  @Column(name="sort_order_id")
  private Integer sortOrderId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             


  @Transient
  private TestAnalyte original;

  
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

  public Integer getResultGroupId() {
    return resultGroupId;
  }
  public void setResultGroupId(Integer resultGroupId) {
    if((resultGroupId == null && this.resultGroupId != null) || 
       (resultGroupId != null && !resultGroupId.equals(this.resultGroupId)))
      this.resultGroupId = resultGroupId;
  }

  public Integer getSortOrderId() {
    return sortOrderId;
  }
  public void setSortOrderId(Integer sortOrderId) {
    if((sortOrderId == null && this.sortOrderId != null) || 
       (sortOrderId != null && !sortOrderId.equals(this.sortOrderId)))
      this.sortOrderId = sortOrderId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
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
      original = (TestAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(resultGroupId,original.resultGroupId,doc,"result_group_id");

      AuditUtil.getChangeXML(sortOrderId,original.sortOrderId,doc,"sort_order_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_analyte";
  }
  
}   
