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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.entity;

/**
  * TestResult Entity POJO for database 
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
@Table(name="test_result")
@EntityListeners({AuditUtil.class})
public class TestResult implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="result_group_id")
  private Integer resultGroupId;             

  @Column(name="flag_id")
  private Integer flagId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             

  @Column(name="significant_digits")
  private Integer significantDigits;             

  @Column(name="quant_limit")
  private String quantLimit;             

  @Column(name="cont_level")
  private String contLevel;             


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

  public Integer getResultGroupId() {
    return resultGroupId;
  }
  public void setResultGroupId(Integer resultGroupId) {
    if((resultGroupId == null && this.resultGroupId != null) || 
       (resultGroupId != null && !resultGroupId.equals(this.resultGroupId)))
      this.resultGroupId = resultGroupId;
  }

  public Integer getFlagId() {
    return flagId;
  }
  public void setFlagId(Integer flagId) {
    if((flagId == null && this.flagId != null) || 
       (flagId != null && !flagId.equals(this.flagId)))
      this.flagId = flagId;
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

      AuditUtil.getChangeXML(resultGroupId,original.resultGroupId,doc,"result_group_id");

      AuditUtil.getChangeXML(flagId,original.flagId,doc,"flag_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      AuditUtil.getChangeXML(significantDigits,original.significantDigits,doc,"significant_digits");

      AuditUtil.getChangeXML(quantLimit,original.quantLimit,doc,"quant_limit");

      AuditUtil.getChangeXML(contLevel,original.contLevel,doc,"cont_level");

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
