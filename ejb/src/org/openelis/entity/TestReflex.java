
package org.openelis.entity;

/**
  * TestReflex Entity POJO for database 
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
@Table(name="test_reflex")
@EntityListeners({AuditUtil.class})
public class TestReflex implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test")
  private Integer test;             

  @Column(name="test_analyte")
  private Integer testAnalyte;             

  @Column(name="test_result")
  private Integer testResult;             

  @Column(name="flags")
  private Integer flags;             

  @Column(name="add_test")
  private Integer addTest;             


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

  public Integer getTest() {
    return test;
  }
  public void setTest(Integer test) {
    if((test == null && this.test != null) || 
       (test != null && !test.equals(this.test)))
      this.test = test;
  }

  public Integer getTestAnalyte() {
    return testAnalyte;
  }
  public void setTestAnalyte(Integer testAnalyte) {
    if((testAnalyte == null && this.testAnalyte != null) || 
       (testAnalyte != null && !testAnalyte.equals(this.testAnalyte)))
      this.testAnalyte = testAnalyte;
  }

  public Integer getTestResult() {
    return testResult;
  }
  public void setTestResult(Integer testResult) {
    if((testResult == null && this.testResult != null) || 
       (testResult != null && !testResult.equals(this.testResult)))
      this.testResult = testResult;
  }

  public Integer getFlags() {
    return flags;
  }
  public void setFlags(Integer flags) {
    if((flags == null && this.flags != null) || 
       (flags != null && !flags.equals(this.flags)))
      this.flags = flags;
  }

  public Integer getAddTest() {
    return addTest;
  }
  public void setAddTest(Integer addTest) {
    if((addTest == null && this.addTest != null) || 
       (addTest != null && !addTest.equals(this.addTest)))
      this.addTest = addTest;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((test == null && original.test != null) || 
         (test != null && !test.equals(original.test))){
        Element elem = doc.createElement("test");
        elem.appendChild(doc.createTextNode(original.test.toString().trim()));
        root.appendChild(elem);
      }      

      if((testAnalyte == null && original.testAnalyte != null) || 
         (testAnalyte != null && !testAnalyte.equals(original.testAnalyte))){
        Element elem = doc.createElement("test_analyte");
        elem.appendChild(doc.createTextNode(original.testAnalyte.toString().trim()));
        root.appendChild(elem);
      }      

      if((testResult == null && original.testResult != null) || 
         (testResult != null && !testResult.equals(original.testResult))){
        Element elem = doc.createElement("test_result");
        elem.appendChild(doc.createTextNode(original.testResult.toString().trim()));
        root.appendChild(elem);
      }      

      if((flags == null && original.flags != null) || 
         (flags != null && !flags.equals(original.flags))){
        Element elem = doc.createElement("flags");
        elem.appendChild(doc.createTextNode(original.flags.toString().trim()));
        root.appendChild(elem);
      }      

      if((addTest == null && original.addTest != null) || 
         (addTest != null && !addTest.equals(original.addTest))){
        Element elem = doc.createElement("add_test");
        elem.appendChild(doc.createTextNode(original.addTest.toString().trim()));
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
    return "test_reflex";
  }
  
}   
