
package org.openelis.entity;

/**
  * MethodResult Entity POJO for database 
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
@Table(name="method_result")
@EntityListeners({AuditUtil.class})
public class MethodResult implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="method")
  private Integer method;             

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="flags")
  private String flags;             

  @Column(name="type")
  private String type;             

  @Column(name="value")
  private String value;             


  @Transient
  private MethodResult original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getMethod() {
    return method;
  }
  public void setMethod(Integer method) {
    if((method == null && this.method != null) || 
       (method != null && !method.equals(this.method)))
      this.method = method;
  }

  public Integer getResultGroup() {
    return resultGroup;
  }
  public void setResultGroup(Integer resultGroup) {
    if((resultGroup == null && this.resultGroup != null) || 
       (resultGroup != null && !resultGroup.equals(this.resultGroup)))
      this.resultGroup = resultGroup;
  }

  public String getFlags() {
    return flags;
  }
  public void setFlags(String flags) {
    if((flags == null && this.flags != null) || 
       (flags != null && !flags.equals(this.flags)))
      this.flags = flags;
  }

  public String getType() {
    return type;
  }
  public void setType(String type) {
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

  
  public void setClone() {
    try {
      original = (MethodResult)this.clone();
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

      if((method == null && original.method != null) || 
         (method != null && !method.equals(original.method))){
        Element elem = doc.createElement("method");
        elem.appendChild(doc.createTextNode(original.method.toString().trim()));
        root.appendChild(elem);
      }      

      if((resultGroup == null && original.resultGroup != null) || 
         (resultGroup != null && !resultGroup.equals(original.resultGroup))){
        Element elem = doc.createElement("result_group");
        elem.appendChild(doc.createTextNode(original.resultGroup.toString().trim()));
        root.appendChild(elem);
      }      

      if((flags == null && original.flags != null) || 
         (flags != null && !flags.equals(original.flags))){
        Element elem = doc.createElement("flags");
        elem.appendChild(doc.createTextNode(original.flags.toString().trim()));
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

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "method_result";
  }
  
}   
