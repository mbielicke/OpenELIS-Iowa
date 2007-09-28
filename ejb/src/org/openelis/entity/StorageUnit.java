
package org.openelis.entity;

/**
  * StorageUnit Entity POJO for database 
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
@Table(name="storage_unit")
@EntityListeners({AuditUtil.class})
public class StorageUnit implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="category")
  private String category;             

  @Column(name="description")
  private String description;             

  @Column(name="is_singular")
  private String isSingular;             


  @Transient
  private StorageUnit original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getIsSingular() {
    return isSingular;
  }
  public void setIsSingular(String isSingular) {
    this.isSingular = isSingular;
  }

  
  public void setClone() {
    try {
      original = (StorageUnit)this.clone();
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

      if((category == null && original.category != null) || 
         (category != null && !category.equals(original.category))){
        Element elem = doc.createElement("category");
        elem.appendChild(doc.createTextNode(original.category.toString()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((isSingular == null && original.isSingular != null) || 
         (isSingular != null && !isSingular.equals(original.isSingular))){
        Element elem = doc.createElement("is_singular");
        elem.appendChild(doc.createTextNode(original.isSingular.toString()));
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
    return "storage_unit";
  }
  
}   
