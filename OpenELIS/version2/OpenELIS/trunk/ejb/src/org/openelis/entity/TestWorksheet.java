
package org.openelis.entity;

/**
  * TestWorksheet Entity POJO for database 
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
@Table(name="test_worksheet")
@EntityListeners({AuditUtil.class})
public class TestWorksheet implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test")
  private Integer test;             

  @Column(name="batch_capacity")
  private Integer batchCapacity;             

  @Column(name="total_capacity")
  private Integer totalCapacity;             

  @Column(name="number_format")
  private Integer numberFormat;             

  @Column(name="scriptlet")
  private Integer scriptlet;             


  @Transient
  private TestWorksheet original;

  
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

  public Integer getBatchCapacity() {
    return batchCapacity;
  }
  public void setBatchCapacity(Integer batchCapacity) {
    this.batchCapacity = batchCapacity;
  }

  public Integer getTotalCapacity() {
    return totalCapacity;
  }
  public void setTotalCapacity(Integer totalCapacity) {
    this.totalCapacity = totalCapacity;
  }

  public Integer getNumberFormat() {
    return numberFormat;
  }
  public void setNumberFormat(Integer numberFormat) {
    this.numberFormat = numberFormat;
  }

  public Integer getScriptlet() {
    return scriptlet;
  }
  public void setScriptlet(Integer scriptlet) {
    this.scriptlet = scriptlet;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheet)this.clone();
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

      if((batchCapacity == null && original.batchCapacity != null) || 
         (batchCapacity != null && !batchCapacity.equals(original.batchCapacity))){
        Element elem = doc.createElement("batch_capacity");
        elem.appendChild(doc.createTextNode(original.batchCapacity.toString()));
        root.appendChild(elem);
      }      

      if((totalCapacity == null && original.totalCapacity != null) || 
         (totalCapacity != null && !totalCapacity.equals(original.totalCapacity))){
        Element elem = doc.createElement("total_capacity");
        elem.appendChild(doc.createTextNode(original.totalCapacity.toString()));
        root.appendChild(elem);
      }      

      if((numberFormat == null && original.numberFormat != null) || 
         (numberFormat != null && !numberFormat.equals(original.numberFormat))){
        Element elem = doc.createElement("number_format");
        elem.appendChild(doc.createTextNode(original.numberFormat.toString()));
        root.appendChild(elem);
      }      

      if((scriptlet == null && original.scriptlet != null) || 
         (scriptlet != null && !scriptlet.equals(original.scriptlet))){
        Element elem = doc.createElement("scriptlet");
        elem.appendChild(doc.createTextNode(original.scriptlet.toString()));
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
    return "test_worksheet";
  }
  
}   
