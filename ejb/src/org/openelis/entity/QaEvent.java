
package org.openelis.entity;

/**
  * Qaevent Entity POJO for database 
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
@Table(name="qaevent")
@EntityListeners({AuditUtil.class})
public class QaEvent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="test")
  private Integer test;             

  @Column(name="type")
  private Integer type;             

  @Column(name="is_billable")
  private String isBillable;             

  @Column(name="reporting_sequence")
  private Integer reportingSequence;             

  @Column(name="reporting_text")
  private String reportingText;             


  @Transient
  private QaEvent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getTest() {
    return test;
  }
  public void setTest(Integer test) {
    if((test == null && this.test != null) || 
       (test != null && !test.equals(this.test)))
      this.test = test;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public String getIsBillable() {
    return isBillable;
  }
  public void setIsBillable(String isBillable) {
    if((isBillable == null && this.isBillable != null) || 
       (isBillable != null && !isBillable.equals(this.isBillable)))
      this.isBillable = isBillable;
  }

  public Integer getReportingSequence() {
    return reportingSequence;
  }
  public void setReportingSequence(Integer reportingSequence) {
    if((reportingSequence == null && this.reportingSequence != null) || 
       (reportingSequence != null && !reportingSequence.equals(this.reportingSequence)))
      this.reportingSequence = reportingSequence;
  }

  public String getReportingText() {
    return reportingText;
  }
  public void setReportingText(String reportingText) {
    if((reportingText == null && this.reportingText != null) || 
       (reportingText != null && !reportingText.equals(this.reportingText)))
      this.reportingText = reportingText;
  }

  
  public void setClone() {
    try {
      original = (QaEvent)this.clone();
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

        if((name == null && original.name != null) || 
           (name != null && !name.equals(original.name))){
          Element elem = doc.createElement("name");
          elem.appendChild(doc.createTextNode(original.name.toString().trim()));
          root.appendChild(elem);
        }      

        if((description == null && original.description != null) || 
           (description != null && !description.equals(original.description))){
          Element elem = doc.createElement("description");
          elem.appendChild(doc.createTextNode(original.description.toString().trim()));
          root.appendChild(elem);
        }      

        if((test == null && original.test != null) || 
           (test != null && !test.equals(original.test))){
          Element elem = doc.createElement("test");
          elem.appendChild(doc.createTextNode(original.test.toString().trim()));
          root.appendChild(elem);
        }      

        if((type == null && original.type != null) || 
           (type != null && !type.equals(original.type))){
          Element elem = doc.createElement("type");
          elem.appendChild(doc.createTextNode(original.type.toString().trim()));
          root.appendChild(elem);
        }      

        if((isBillable == null && original.isBillable != null) || 
           (isBillable != null && !isBillable.equals(original.isBillable))){
          Element elem = doc.createElement("is_billable");
          elem.appendChild(doc.createTextNode(original.isBillable.toString().trim()));
          root.appendChild(elem);
        }      

        if((reportingSequence == null && original.reportingSequence != null) || 
           (reportingSequence != null && !reportingSequence.equals(original.reportingSequence))){
          Element elem = doc.createElement("reporting_sequence");
          elem.appendChild(doc.createTextNode(original.reportingSequence.toString().trim()));
          root.appendChild(elem);
        }      

        if((reportingText == null && original.reportingText != null) || 
           (reportingText != null && !reportingText.equals(original.reportingText))){
          Element elem = doc.createElement("reporting_text");
          elem.appendChild(doc.createTextNode(original.reportingText.toString().trim()));
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
    return "qaevent";
  }
  
}   
