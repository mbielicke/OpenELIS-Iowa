
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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="result")
@EntityListeners({AuditUtil.class})
public class Result implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis")
  private Integer analysis;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="analyte")
  private Integer analyte;             

  @Column(name="type")
  private Integer type;             

  @Column(name="value")
  private String value;             

  @Column(name="test_result")
  private Integer testResult;             

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

  public Integer getAnalysis() {
    return analysis;
  }
  public void setAnalysis(Integer analysis) {
    if((analysis == null && this.analysis != null) || 
       (analysis != null && !analysis.equals(this.analysis)))
      this.analysis = analysis;
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

  public Integer getAnalyte() {
    return analyte;
  }
  public void setAnalyte(Integer analyte) {
    if((analyte == null && this.analyte != null) || 
       (analyte != null && !analyte.equals(this.analyte)))
      this.analyte = analyte;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
      this.value = value;
  }

  public Integer getTestResult() {
    return testResult;
  }
  public void setTestResult(Integer testResult) {
    if((testResult == null && this.testResult != null) || 
       (testResult != null && !testResult.equals(this.testResult)))
      this.testResult = testResult;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((analysis == null && original.analysis != null) || 
         (analysis != null && !analysis.equals(original.analysis))){
        Element elem = doc.createElement("analysis");
        elem.appendChild(doc.createTextNode(original.analysis.toString().trim()));
        root.appendChild(elem);
      }      

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString().trim()));
        root.appendChild(elem);
      }      

      if((isReportable == null && original.isReportable != null) || 
         (isReportable != null && !isReportable.equals(original.isReportable))){
        Element elem = doc.createElement("is_reportable");
        elem.appendChild(doc.createTextNode(original.isReportable.toString().trim()));
        root.appendChild(elem);
      }      

      if((analyte == null && original.analyte != null) || 
         (analyte != null && !analyte.equals(original.analyte))){
        Element elem = doc.createElement("analyte");
        elem.appendChild(doc.createTextNode(original.analyte.toString().trim()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString().trim()));
        root.appendChild(elem);
      }      

      if((value == null && original.value != null) || 
         (value != null && !value.equals(original.value))){
        Element elem = doc.createElement("value");
        elem.appendChild(doc.createTextNode(original.value.toString().trim()));
        root.appendChild(elem);
      }      

      if((testResult == null && original.testResult != null) || 
         (testResult != null && !testResult.equals(original.testResult))){
        Element elem = doc.createElement("test_result");
        elem.appendChild(doc.createTextNode(original.testResult.toString().trim()));
        root.appendChild(elem);
      }      

      if((quantLimit == null && original.quantLimit != null) || 
         (quantLimit != null && !quantLimit.equals(original.quantLimit))){
        Element elem = doc.createElement("quant_limit");
        elem.appendChild(doc.createTextNode(original.quantLimit.toString().trim()));
        root.appendChild(elem);
      }      

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
