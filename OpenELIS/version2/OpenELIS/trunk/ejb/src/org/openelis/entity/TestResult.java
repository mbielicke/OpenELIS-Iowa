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
  * TestResult Entity POJO for database 
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

@NamedQueries({@NamedQuery(name = "TestResult.IdByTestId", query = "select tr.id from TestResult tr where tr.testId = :testId"),
               @NamedQuery(name = "TestResult.IdValueByTestAnalyteId", query = "select distinct new org.openelis.domain.IdNameDO(tr.id,tr.value) " +
                        " from TestResult tr, TestAnalyte ta where tr.testId = :testId" +
                        " and tr.resultGroup = ta.resultGroup and ta.id = :analyteId"),
               @NamedQuery(name = "TestResult.IdValueByTestId", query = "select distinct new org.openelis.domain.IdNameDO(tr.id,tr.value) " +
                                    " from TestResult tr where tr.testId = :testId order by tr.value "),
               @NamedQuery(name = "TestResult.TestResultDOList", query = "select distinct new org.openelis.domain.TestResultDO(tr.id,tr.testId,tr.resultGroup,"+
                        " tr.sortOrder,tr.flagsId,tr.typeId,tr.value, tr.significantDigits,tr.roundingMethodId, "+
                        " tr.quantLimit,tr.contLevel,tr.hazardLevel)  from TestResult tr " +
                        " where tr.testId = :testId and tr.resultGroup = :resultGroup order by tr.sortOrder "),
               @NamedQuery(name = "TestResult.ResultGroupsByTestId", query = "select distinct new org.openelis.domain.IdNameDO(tr.resultGroup,null) " +
                        " from TestResult tr where tr.testId = :testId order by tr.resultGroup "),
               @NamedQuery(name = "TestResult.ResultCountByValue", query = "select tr.id " +
                        " from TestResult tr where tr.value = :value")         })
@Entity
@Table(name="test_result")
@EntityListeners({AuditUtil.class})
public class TestResult implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="sort_order")
  private Integer sortOrder;
  
  @Column(name="flags_id")
  private Integer flagsId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             

  @Column(name="significant_digits")
  private Integer significantDigits;             

  @Column(name="rounding_method_id")
  private Integer roundingMethodId;
  
  @Column(name="quant_limit")
  private String quantLimit;             

  @Column(name="cont_level")
  private String contLevel;      
  
  @Column(name="hazard_level")
  private String hazardLevel;


  @Transient
  private TestResult original;

  
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

  public Integer sortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }
  
  public Integer getResultGroup() {
      return resultGroup;
    }
  public void setResultGroup(Integer resultGroup) {
      if((resultGroup == null && this.resultGroup != null) || 
         (resultGroup != null && !resultGroup.equals(this.resultGroup)))
        this.resultGroup = resultGroup;
    }

  public Integer getFlagsId() {
    return flagsId;
  }
  public void setFlagsId(Integer flagsId) {
    if((flagsId == null && this.flagsId != null) || 
       (flagsId != null && !flagsId.equals(this.flagsId)))
      this.flagsId = flagsId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
      this.value = value;
  }

  public Integer getRoundingMethodId() {
    return roundingMethodId;
  }
  public void setRoundingMethodId(Integer roundingMethodId) {
    if((roundingMethodId == null && this.roundingMethodId != null) || 
       (roundingMethodId != null && !roundingMethodId.equals(this.roundingMethodId)))
      this.roundingMethodId = roundingMethodId;
  }
  
  public Integer getSignificantDigits() {
      return significantDigits;
    }  
  public void setSignificantDigits(Integer significantDigits) {
      if((significantDigits == null && this.significantDigits != null) || 
         (significantDigits != null && !significantDigits.equals(this.significantDigits)))
        this.significantDigits = significantDigits;
    }

  public String getQuantLimit() {
    return quantLimit;
  }
  public void setQuantLimit(String quantLimit) {
    if((quantLimit == null && this.quantLimit != null) || 
       (quantLimit != null && !quantLimit.equals(this.quantLimit)))
      this.quantLimit = quantLimit;
  }

  public String getContLevel() {
    return contLevel;
  }
  public void setContLevel(String contLevel) {
    if((contLevel == null && this.contLevel != null) || 
       (contLevel != null && !contLevel.equals(this.contLevel)))
      this.contLevel = contLevel;
  }

  public String getHazardLevel() {
      return hazardLevel;
    }
  public void setHazardLevel(String hazardLevel) {
      if((hazardLevel == null && this.hazardLevel != null) || 
         (hazardLevel != null && !hazardLevel.equals(this.hazardLevel)))
        this.hazardLevel = hazardLevel;
    }
  
  public void setClone() {
    try {
      original = (TestResult)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(resultGroup,original.resultGroup,doc,"result_group");
      
      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(flagsId,original.flagsId,doc,"flags_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      AuditUtil.getChangeXML(significantDigits,original.significantDigits,doc,"significant_digits");
      
      AuditUtil.getChangeXML(roundingMethodId,original.roundingMethodId,doc,"rounding_method_id");

      AuditUtil.getChangeXML(quantLimit,original.quantLimit,doc,"quant_limit");

      AuditUtil.getChangeXML(contLevel,original.contLevel,doc,"cont_level");
      
      AuditUtil.getChangeXML(hazardLevel,original.hazardLevel,doc,"hazard_level");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_result";
  }
  
}   
