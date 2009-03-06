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
  * TestAnalyte Entity POJO for database 
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
@NamedQueries({@NamedQuery(name = "TestAnalyte.TestAnalyteByAnalyteId", query = "select t.id from TestAnalyte t where t.analyteId = :id"),
               @NamedQuery(name = "TestAnalyte.TestAnalyteByTestId", query = "from TestAnalyte ta where ta.testId = :testId"),
               @NamedQuery(name = "TestAnalyte.IdName", query = "select distinct new org.openelis.domain.IdNameDO(ta.id, a.name) from TestAnalyte ta left join ta.analyte a where ta.testId = :testId order by a.name"),
               @NamedQuery(name = "TestAnalyte.TestAnalyteDOListByTestId", query = "select distinct new org.openelis.domain.TestAnalyteDO(ta.id,ta.testId,ta.analyteGroup,ta.resultGroup,ta.sortOrder,ta.typeId,ta.analyteId,a.name,ta.isReportable,ta.scriptletId)" +
                    "                  from TestAnalyte ta, Analyte a where ta.testId = :testId and a.id = ta.analyteId order by ta.sortOrder"),
               @NamedQuery(name = "TestAnalyte.TestAnalytesByResultGroupAndTestId", query = "select ta.resultGroup, ta.id from TestAnalyte ta  where ta.testId = :testId " +
                    " group by ta.resultGroup, ta.id order by ta.resultGroup, ta.id"),
               @NamedQuery(name = "TestAnalyte.TestAnalytesNotAddedToWorksheet", query = "select distinct new org.openelis.domain.IdNameDO(ta.analyteId,a.name)" +
                    " from TestAnalyte ta, Analyte a where ta.testId = :testId and a.id = ta.analyteId " +
                    " and ta.analyteId not in (select distinct twa.analyteId from TestWorksheetAnalyte twa where twa.testId = :testId)" +
                    " order by a.name")})
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
  
  @Column(name="analyte_group")
  private Integer analyteGroup;

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analyte_id",insertable = false, updatable = false)
  private Analyte analyte;
  
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

  public Integer getAnalyteGroup() {
      return analyteGroup;
    }
    public void setAnalyteGroup(Integer analyteGroup) {
      if((analyteGroup == null && this.analyteGroup != null) || 
         (analyteGroup != null && !analyteGroup.equals(this.analyteGroup)))
        this.analyteGroup = analyteGroup;
    }
  
  public Integer getResultGroup() {
    return resultGroup;
  }
  public void setResultGroup(Integer resultGroup) {
    if((resultGroup == null && this.resultGroup != null) || 
       (resultGroup != null && !resultGroup.equals(this.resultGroup)))
      this.resultGroup = resultGroup;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
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
      
      AuditUtil.getChangeXML(analyteGroup,original.analyteGroup,doc,"analyte_group");

      AuditUtil.getChangeXML(resultGroup,original.resultGroup,doc,"result_group");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

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
public Analyte getAnalyte() {
    return analyte;
}
public void setAnalyte(Analyte analyte) {
    this.analyte = analyte;
}
  
}   
