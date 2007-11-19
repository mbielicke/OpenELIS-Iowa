
package org.openelis.entity;

/**
  * Attachment Entity POJO for database 
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
@Table(name="attachment")
@EntityListeners({AuditUtil.class})
public class Attachment implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="type")
  private Integer type;             

  @Column(name="filename")
  private String filename;             

  @Column(name="description")
  private String description;             

  @Column(name="storage_reference")
  private String storageReference;             


  @Transient
  private Attachment original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public String getFilename() {
    return filename;
  }
  public void setFilename(String filename) {
    if((filename == null && this.filename != null) || 
       (filename != null && !filename.equals(this.filename)))
      this.filename = filename;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getStorageReference() {
    return storageReference;
  }
  public void setStorageReference(String storageReference) {
    if((storageReference == null && this.storageReference != null) || 
       (storageReference != null && !storageReference.equals(this.storageReference)))
      this.storageReference = storageReference;
  }

  
  public void setClone() {
    try {
      original = (Attachment)this.clone();
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

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((filename == null && original.filename != null) || 
         (filename != null && !filename.equals(original.filename))){
        Element elem = doc.createElement("filename");
        elem.appendChild(doc.createTextNode(original.filename.toString()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((storageReference == null && original.storageReference != null) || 
         (storageReference != null && !storageReference.equals(original.storageReference))){
        Element elem = doc.createElement("storage_reference");
        elem.appendChild(doc.createTextNode(original.storageReference.toString()));
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
    return "attachment";
  }
  
}   
