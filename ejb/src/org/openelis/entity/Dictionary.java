
package org.openelis.entity;

/**
  * Dictionary Entity POJO for database 
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
@Table(name="dictionary")
@EntityListeners({AuditUtil.class})
public class Dictionary implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="category")
  private Integer category;             

  @Column(name="related_entry")
  private Integer relatedEntry;             

  @Column(name="system_name")
  private String systemName;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="local_abbrev")
  private String localAbbrev;             

  @Column(name="entry")
  private String entry;             


  @Transient
  private Dictionary original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getCategory() {
    return category;
  }
  public void setCategory(Integer category) {
    if((category == null && this.category != null) || 
       (category != null && !category.equals(this.category)))
      this.category = category;
  }

  public Integer getRelatedEntry() {
    return relatedEntry;
  }
  public void setRelatedEntry(Integer relatedEntry) {
    if((relatedEntry == null && this.relatedEntry != null) || 
       (relatedEntry != null && !relatedEntry.equals(this.relatedEntry)))
      this.relatedEntry = relatedEntry;
  }

  public String getSystemName() {
    return systemName;
  }
  public void setSystemName(String systemName) {
    if((systemName == null && this.systemName != null) || 
       (systemName != null && !systemName.equals(this.systemName)))
      this.systemName = systemName;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getLocalAbbrev() {
    return localAbbrev;
  }
  public void setLocalAbbrev(String localAbbrev) {
    if((localAbbrev == null && this.localAbbrev != null) || 
       (localAbbrev != null && !localAbbrev.equals(this.localAbbrev)))
      this.localAbbrev = localAbbrev;
  }

  public String getEntry() {
    return entry;
  }
  public void setEntry(String entry) {
    if((entry == null && this.entry != null) || 
       (entry != null && !entry.equals(this.entry)))
      this.entry = entry;
  }

  
  public void setClone() {
    try {
      original = (Dictionary)this.clone();
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

      if((relatedEntry == null && original.relatedEntry != null) || 
         (relatedEntry != null && !relatedEntry.equals(original.relatedEntry))){
        Element elem = doc.createElement("related_entry");
        elem.appendChild(doc.createTextNode(original.relatedEntry.toString()));
        root.appendChild(elem);
      }      

      if((systemName == null && original.systemName != null) || 
         (systemName != null && !systemName.equals(original.systemName))){
        Element elem = doc.createElement("system_name");
        elem.appendChild(doc.createTextNode(original.systemName.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }      

      if((localAbbrev == null && original.localAbbrev != null) || 
         (localAbbrev != null && !localAbbrev.equals(original.localAbbrev))){
        Element elem = doc.createElement("local_abbrev");
        elem.appendChild(doc.createTextNode(original.localAbbrev.toString()));
        root.appendChild(elem);
      }      

      if((entry == null && original.entry != null) || 
         (entry != null && !entry.equals(original.entry))){
        Element elem = doc.createElement("entry");
        elem.appendChild(doc.createTextNode(original.entry.toString()));
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
    return "dictionary";
  }
  
}   
