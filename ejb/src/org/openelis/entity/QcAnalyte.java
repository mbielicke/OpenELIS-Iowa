
package org.openelis.entity;

/**
  * QcAnalyte Entity POJO for database 
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
@Table(name="qc_analyte")
@EntityListeners({AuditUtil.class})
public class QcAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="qc")
  private Integer qc;             

  @Column(name="analyte")
  private Integer analyte;             

  @Column(name="type")
  private Integer type;             

  @Column(name="value")
  private String value;             

  @Column(name="is_trendable")
  private String isTrendable;             


  @Transient
  private QcAnalyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getQc() {
    return qc;
  }
  public void setQc(Integer qc) {
    this.qc = qc;
  }

  public Integer getAnalyte() {
    return analyte;
  }
  public void setAnalyte(Integer analyte) {
    this.analyte = analyte;
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

  public String getIsTrendable() {
    return isTrendable;
  }
  public void setIsTrendable(String isTrendable) {
    this.isTrendable = isTrendable;
  }

  
  public void setClone() {
    try {
      original = (QcAnalyte)this.clone();
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

      if((qc == null && original.qc != null) || 
         (qc != null && !qc.equals(original.qc))){
        Element elem = doc.createElement("qc");
        elem.appendChild(doc.createTextNode(original.qc.toString()));
        root.appendChild(elem);
      }      

      if((analyte == null && original.analyte != null) || 
         (analyte != null && !analyte.equals(original.analyte))){
        Element elem = doc.createElement("analyte");
        elem.appendChild(doc.createTextNode(original.analyte.toString()));
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

      if((isTrendable == null && original.isTrendable != null) || 
         (isTrendable != null && !isTrendable.equals(original.isTrendable))){
        Element elem = doc.createElement("is_trendable");
        elem.appendChild(doc.createTextNode(original.isTrendable.toString()));
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
    return "qc_analyte";
  }
  
}   
