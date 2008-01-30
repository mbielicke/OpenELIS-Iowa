
package org.openelis.entity;

/**
  * AuxFieldValue Entity POJO for database 
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
@Table(name="aux_field_value")
@EntityListeners({AuditUtil.class})
public class AuxFieldValue implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="aux_field")
  private Integer auxField;             

  @Column(name="type")
  private Integer type;             

  @Column(name="value")
  private String value;             


  @Transient
  private AuxFieldValue original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAuxField() {
    return auxField;
  }
  public void setAuxField(Integer auxField) {
    if((auxField == null && this.auxField != null) || 
       (auxField != null && !auxField.equals(this.auxField)))
      this.auxField = auxField;
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

  
  public void setClone() {
    try {
      original = (AuxFieldValue)this.clone();
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

      if((auxField == null && original.auxField != null) || 
         (auxField != null && !auxField.equals(original.auxField))){
        Element elem = doc.createElement("aux_field");
        elem.appendChild(doc.createTextNode(original.auxField.toString().trim()));
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
    return "aux_field_value";
  }
  
}   
