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
  * TestPrep Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "TestPrep.TestPrep", query = "select distinct new org.openelis.domain.TestPrepDO(tp.id, tp.testId, tp.prepTestId,tp.isOptional) " 
                                                       + "  from TestPrep tp where tp.testId = :id "),
               @NamedQuery(name = "TestPrep.TestPrepByTestId", query = "from TestPrep tp where tp.testId = :testId")})
    
@Entity
@Table(name="test_prep")
@EntityListeners({AuditUtil.class})
public class TestPrep implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="prep_test_id")
  private Integer prepTestId;             

  @Column(name="is_optional")
  private String isOptional;             

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Test test;
  
  @Transient
  private TestPrep original;

  
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

  public Integer getPrepTestId() {
    return prepTestId;
  }
  public void setPrepTestId(Integer prepTestId) {
    if((prepTestId == null && this.prepTestId != null) || 
       (prepTestId != null && !prepTestId.equals(this.prepTestId)))
      this.prepTestId = prepTestId;
  }

  public String getIsOptional() {
    return isOptional;
  }
  public void setIsOptional(String isOptional) {
    if((isOptional == null && this.isOptional != null) || 
       (isOptional != null && !isOptional.equals(this.isOptional)))
      this.isOptional = isOptional;
  }

  
  public void setClone() {
    try {
      original = (TestPrep)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(prepTestId,original.prepTestId,doc,"prep_test_id");

      AuditUtil.getChangeXML(isOptional,original.isOptional,doc,"is_optional");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_prep";
  }
public Test getTest() {
    return test;
}
public void setTest(Test test) {
    this.test = test;
}
  
}   
