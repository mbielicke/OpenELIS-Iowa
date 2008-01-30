
package org.openelis.entity;

/**
  * TestWorksheetItem Entity POJO for database 
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
@Table(name="test_worksheet_item")
@EntityListeners({AuditUtil.class})
public class TestWorksheetItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_worksheet")
  private Integer testWorksheet;             

  @Column(name="position")
  private Integer position;             

  @Column(name="type")
  private Integer type;             

  @Column(name="qc_name")
  private String qcName;             


  @Transient
  private TestWorksheetItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTestWorksheet() {
    return testWorksheet;
  }
  public void setTestWorksheet(Integer testWorksheet) {
    if((testWorksheet == null && this.testWorksheet != null) || 
       (testWorksheet != null && !testWorksheet.equals(this.testWorksheet)))
      this.testWorksheet = testWorksheet;
  }

  public Integer getPosition() {
    return position;
  }
  public void setPosition(Integer position) {
    if((position == null && this.position != null) || 
       (position != null && !position.equals(this.position)))
      this.position = position;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public String getQcName() {
    return qcName;
  }
  public void setQcName(String qcName) {
    if((qcName == null && this.qcName != null) || 
       (qcName != null && !qcName.equals(this.qcName)))
      this.qcName = qcName;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheetItem)this.clone();
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

      if((testWorksheet == null && original.testWorksheet != null) || 
         (testWorksheet != null && !testWorksheet.equals(original.testWorksheet))){
        Element elem = doc.createElement("test_worksheet");
        elem.appendChild(doc.createTextNode(original.testWorksheet.toString().trim()));
        root.appendChild(elem);
      }      

      if((position == null && original.position != null) || 
         (position != null && !position.equals(original.position))){
        Element elem = doc.createElement("position");
        elem.appendChild(doc.createTextNode(original.position.toString().trim()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString().trim()));
        root.appendChild(elem);
      }      

      if((qcName == null && original.qcName != null) || 
         (qcName != null && !qcName.equals(original.qcName))){
        Element elem = doc.createElement("qc_name");
        elem.appendChild(doc.createTextNode(original.qcName.toString().trim()));
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
    return "test_worksheet_item";
  }
  
}   
