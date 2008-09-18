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
  * PanelItem Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "PanelItem.IdByTestName", query = "select pi.id from PanelItem pi where pi.testName = :testName")

@Entity
@Table(name="panel_item")
@EntityListeners({AuditUtil.class})
public class PanelItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="panel_id")
  private Integer panelId;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="test_name")
  private String testName;             

  @Column(name="method_name")
  private String methodName;             


  @Transient
  private PanelItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getPanelId() {
    return panelId;
  }
  public void setPanelId(Integer panelId) {
    if((panelId == null && this.panelId != null) || 
       (panelId != null && !panelId.equals(this.panelId)))
      this.panelId = panelId;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getTestName() {
    return testName;
  }
  public void setTestName(String testName) {
    if((testName == null && this.testName != null) || 
       (testName != null && !testName.equals(this.testName)))
      this.testName = testName;
  }

  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
    if((methodName == null && this.methodName != null) || 
       (methodName != null && !methodName.equals(this.methodName)))
      this.methodName = methodName;
  }

  
  public void setClone() {
    try {
      original = (PanelItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(panelId,original.panelId,doc,"panel_id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order_id");

      AuditUtil.getChangeXML(testName,original.testName,doc,"test_name");

      AuditUtil.getChangeXML(methodName,original.methodName,doc,"method_name");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "panel_item";
  }
  
}   
