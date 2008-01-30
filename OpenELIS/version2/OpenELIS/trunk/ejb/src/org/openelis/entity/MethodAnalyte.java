
package org.openelis.entity;

/**
  * MethodAnalyte Entity POJO for database 
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
@Table(name="method_analyte")
@EntityListeners({AuditUtil.class})
public class MethodAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="method")
  private Integer method;             

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="type")
  private String type;             

  @Column(name="analyte")
  private Integer analyte;             


  @Transient
  private MethodAnalyte original;

  
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

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getType() {
    return type;
  }
  public void setType(String type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public Integer getAnalyte() {
    return analyte;
  }
  public void setAnalyte(Integer analyte) {
    if((analyte == null && this.analyte != null) || 
       (analyte != null && !analyte.equals(this.analyte)))
      this.analyte = analyte;
  }

  
  public void setClone() {
    try {
      original = (MethodAnalyte)this.clone();
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

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString().trim()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString().trim()));
        root.appendChild(elem);
      }      

      if((analyte == null && original.analyte != null) || 
         (analyte != null && !analyte.equals(original.analyte))){
        Element elem = doc.createElement("analyte");
        elem.appendChild(doc.createTextNode(original.analyte.toString().trim()));
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
    return "method_analyte";
  }
  
}   
