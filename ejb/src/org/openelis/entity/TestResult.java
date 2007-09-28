
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
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="test_result")
@EntityListeners({AuditUtil.class})
public class TestResult implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test")
  private Integer test;             

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="flag")
  private Integer flag;             

  @Column(name="type")
  private Integer type;             

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
    this.id = id;
  }

  public Integer getTest() {
    return test;
  }
  public void setTest(Integer test) {
    this.test = test;
  }

  public Integer getResultGroup() {
    return resultGroup;
  }
  public void setResultGroup(Integer resultGroup) {
    this.resultGroup = resultGroup;
  }

  public Integer getFlag() {
    return flag;
  }
  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  public Integer getSignificantDigits() {
    return significantDigits;
  }
  public void setSignificantDigits(Integer significantDigits) {
    this.significantDigits = significantDigits;
  }

  public String getQuantLimit() {
    return quantLimit;
  }
  public void setQuantLimit(String quantLimit) {
    this.quantLimit = quantLimit;
  }

  public String getContLevel() {
    return contLevel;
  }
  public void setContLevel(String contLevel) {
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((test == null && original.test != null) || 
         (test != null && !test.equals(original.test))){
        Element elem = doc.createElement("test");
        elem.appendChild(doc.createTextNode(original.test.toString()));
        root.appendChild(elem);
      }      

      if((resultGroup == null && original.resultGroup != null) || 
         (resultGroup != null && !resultGroup.equals(original.resultGroup))){
        Element elem = doc.createElement("result_group");
        elem.appendChild(doc.createTextNode(original.resultGroup.toString()));
        root.appendChild(elem);
      }      

      if((flag == null && original.flag != null) || 
         (flag != null && !flag.equals(original.flag))){
        Element elem = doc.createElement("flag");
        elem.appendChild(doc.createTextNode(original.flag.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((value == null && original.value != null) || 
         (value != null && !value.equals(original.value))){
        Element elem = doc.createElement("value");
        elem.appendChild(doc.createTextNode(original.value.toString()));
        root.appendChild(elem);
      }      

      if((significantDigits == null && original.significantDigits != null) || 
         (significantDigits != null && !significantDigits.equals(original.significantDigits))){
        Element elem = doc.createElement("significant_digits");
        elem.appendChild(doc.createTextNode(original.significantDigits.toString()));
        root.appendChild(elem);
      }      

      if((quantLimit == null && original.quantLimit != null) || 
         (quantLimit != null && !quantLimit.equals(original.quantLimit))){
        Element elem = doc.createElement("quant_limit");
        elem.appendChild(doc.createTextNode(original.quantLimit.toString()));
        root.appendChild(elem);
      }      

      if((contLevel == null && original.contLevel != null) || 
         (contLevel != null && !contLevel.equals(original.contLevel))){
        Element elem = doc.createElement("cont_level");
        elem.appendChild(doc.createTextNode(original.contLevel.toString()));
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
    return "test_result";
  }
  
}   
