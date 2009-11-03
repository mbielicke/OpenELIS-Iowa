/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * TestReflex Entity POJO for database 
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
@NamedQueries({@NamedQuery(name = "TestReflex.TestReflexDOList", 
                 query = "select new org.openelis.domain.TestReflexDO(tr.id, tr.testId,tr.testAnalyteId," +
                         " tr.testResultId, tr.flagsId, tr.addTestId, r.value) " +
                         " from TestReflex tr left join tr.testResult r where tr.testId = :testId"),
               @NamedQuery(name = "TestReflex.TestReflexesByTestAndTestResult", 
                 query = " from TestReflex tr where tr.testId = :testId and tr.testResultId = :testResultId  "), 
               @NamedQuery(name = "TestReflex.TestReflexesByTestAndTestAnalyte", 
                 query = " from TestReflex tr where tr.testId = :testId and tr.testAnalyteId = :testAnalyteId  ")  })

@Entity
@Table(name="test_reflex")
@EntityListeners({AuditUtil.class})
public class TestReflex implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="test_analyte_id")
  private Integer testAnalyteId;             

  @Column(name="test_result_id")
  private Integer testResultId;             

  @Column(name="flags_id")
  private Integer flagsId;             

  @Column(name="add_test_id")
  private Integer addTestId;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flags_id",insertable = false, updatable = false)
  private Dictionary dictionary;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_result_id",insertable = false, updatable = false)
  private TestResult testResult;

  @Transient
  private TestReflex original;

  
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

  public Integer getTestAnalyteId() {
    return testAnalyteId;
  }
  public void setTestAnalyteId(Integer testAnalyteId) {
    if((testAnalyteId == null && this.testAnalyteId != null) || 
       (testAnalyteId != null && !testAnalyteId.equals(this.testAnalyteId)))
      this.testAnalyteId = testAnalyteId;
  }

  public Integer getTestResultId() {
    return testResultId;
  }
  public void setTestResultId(Integer testResultId) {
    if((testResultId == null && this.testResultId != null) || 
       (testResultId != null && !testResultId.equals(this.testResultId)))
      this.testResultId = testResultId;
  }

  public Integer getFlagsId() {
    return flagsId;
  }
  public void setFlagsId(Integer flagsId) {
    if((flagsId == null && this.flagsId != null) || 
       (flagsId != null && !flagsId.equals(this.flagsId)))
      this.flagsId = flagsId;
  }

  public Integer getAddTestId() {
    return addTestId;
  }
  public void setAddTestId(Integer addTestId) {
    if((addTestId == null && this.addTestId != null) || 
       (addTestId != null && !addTestId.equals(this.addTestId)))
      this.addTestId = addTestId;
  }

  
  public void setClone() {
    try {
      original = (TestReflex)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(testAnalyteId,original.testAnalyteId,doc,"test_analyte_id");

      AuditUtil.getChangeXML(testResultId,original.testResultId,doc,"test_result_id");

      AuditUtil.getChangeXML(flagsId,original.flagsId,doc,"flags_id");

      AuditUtil.getChangeXML(addTestId,original.addTestId,doc,"add_test_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_reflex";
  }
public Dictionary getDictionary() {
    return dictionary;
}
public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
}
public TestResult getTestResult() {
    return testResult;
}
public void setTestResult(TestResult testResult) {
    this.testResult = testResult;
}
  
}   
