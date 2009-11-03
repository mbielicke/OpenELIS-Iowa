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
  * Result Entity POJO for database 
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

@NamedQueries({@NamedQuery(name = "Result.ResultByAnalyteId", query = "select r.id from Result r where r.analyteId = :id")})

@Entity
@Table(name="result")
@EntityListeners({AuditUtil.class})
public class Result implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis_id")
  private Integer analysisId;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             

  @Column(name="test_result_id")
  private Integer testResultId;             

  @Column(name="quant_limit")
  private String quantLimit;             


  @Transient
  private Result original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAnalysisId() {
    return analysisId;
  }
  public void setAnalysisId(Integer analysisId) {
    if((analysisId == null && this.analysisId != null) || 
       (analysisId != null && !analysisId.equals(this.analysisId)))
      this.analysisId = analysisId;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
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

  public Integer getTestResultId() {
    return testResultId;
  }
  public void setTestResultId(Integer testResultId) {
    if((testResultId == null && this.testResultId != null) || 
       (testResultId != null && !testResultId.equals(this.testResultId)))
      this.testResultId = testResultId;
  }

  public String getQuantLimit() {
    return quantLimit;
  }
  public void setQuantLimit(String quantLimit) {
    if((quantLimit == null && this.quantLimit != null) || 
       (quantLimit != null && !quantLimit.equals(this.quantLimit)))
      this.quantLimit = quantLimit;
  }

  
  public void setClone() {
    try {
      original = (Result)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(analysisId,original.analysisId,doc,"analysis_id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order_id");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      AuditUtil.getChangeXML(testResultId,original.testResultId,doc,"test_result_id");

      AuditUtil.getChangeXML(quantLimit,original.quantLimit,doc,"quant_limit");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "result";
  }
  
}   
