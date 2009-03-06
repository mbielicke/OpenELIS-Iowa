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
  * TestWorksheetAnalyte Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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

@NamedQueries({@NamedQuery(name = "TestWorksheetAnalyte.TestWorksheetAnalyteDOByTestId", 
                            query = "select distinct new org.openelis.domain.TestWorksheetAnalyteDO(twa.id,twa.testId,twa.analyteId,"+
                                 " a.name,twa.repeat,twa.flagId) from TestWorksheetAnalyte twa, Analyte a where twa.testId = :testId"+ 
                                  " and a.id = twa.analyteId order by a.name")})
    

@Entity
@Table(name="test_worksheet_analyte")
@EntityListeners({AuditUtil.class})
public class TestWorksheetAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="repeat")
  private Integer repeat;             

  @Column(name="flag_id")
  private Integer flagId;             


  @Transient
  private TestWorksheetAnalyte original;

  
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

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public Integer getRepeat() {
    return repeat;
  }
  public void setRepeat(Integer repeat) {
    if((repeat == null && this.repeat != null) || 
       (repeat != null && !repeat.equals(this.repeat)))
      this.repeat = repeat;
  }

  public Integer getFlagId() {
    return flagId;
  }
  public void setFlagId(Integer flagId) {
    if((flagId == null && this.flagId != null) || 
       (flagId != null && !flagId.equals(this.flagId)))
      this.flagId = flagId;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheetAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(repeat,original.repeat,doc,"repeat");

      AuditUtil.getChangeXML(flagId,original.flagId,doc,"flag_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_worksheet_analyte";
  }
  
}   
