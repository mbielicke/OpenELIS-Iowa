
package org.openelis.entity;

/**
  * Note Entity POJO for database 
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
@Table(name="note")
@EntityListeners({AuditUtil.class})
public class Note implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="timestamp")
  private Date timestamp;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="system_user")
  private Integer systemUser;             

  @Column(name="subject")
  private String subject;             

  @Column(name="text")
  private String text;             


  @Transient
  private Note original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    if((referenceId == null && this.referenceId != null) || 
       (referenceId != null && !referenceId.equals(this.referenceId)))
      this.referenceId = referenceId;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    if((referenceTable == null && this.referenceTable != null) || 
       (referenceTable != null && !referenceTable.equals(this.referenceTable)))
      this.referenceTable = referenceTable;
  }

  public Datetime getTimestamp() {
    if(timestamp == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,timestamp);
  }
  public void setTimestamp (Datetime timestamp){
    if((timestamp == null && this.timestamp != null) || 
       (timestamp != null && !timestamp.equals(this.timestamp)))
      this.timestamp = timestamp.getDate();
  }

  public String getIsExternal() {
    return isExternal;
  }
  public void setIsExternal(String isExternal) {
    if((isExternal == null && this.isExternal != null) || 
       (isExternal != null && !isExternal.equals(this.isExternal)))
      this.isExternal = isExternal;
  }

  public Integer getSystemUser() {
    return systemUser;
  }
  public void setSystemUser(Integer systemUser) {
    if((systemUser == null && this.systemUser != null) || 
       (systemUser != null && !systemUser.equals(this.systemUser)))
      this.systemUser = systemUser;
  }

  public String getSubject() {
    return subject;
  }
  public void setSubject(String subject) {
    if((subject == null && this.subject != null) || 
       (subject != null && !subject.equals(this.subject)))
      this.subject = subject;
  }

  public String getText() {
    return text;
  }
  public void setText(String text) {
    if((text == null && this.text != null) || 
       (text != null && !text.equals(this.text)))
      this.text = text;
  }

  
  public void setClone() {
    try {
      original = (Note)this.clone();
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

      if((referenceId == null && original.referenceId != null) || 
         (referenceId != null && !referenceId.equals(original.referenceId))){
        Element elem = doc.createElement("reference_id");
        elem.appendChild(doc.createTextNode(original.referenceId.toString().trim()));
        root.appendChild(elem);
      }      

      if((referenceTable == null && original.referenceTable != null) || 
         (referenceTable != null && !referenceTable.equals(original.referenceTable))){
        Element elem = doc.createElement("reference_table");
        elem.appendChild(doc.createTextNode(original.referenceTable.toString().trim()));
        root.appendChild(elem);
      }      

      if((timestamp == null && original.timestamp != null) || 
         (timestamp != null && !timestamp.equals(original.timestamp))){
        Element elem = doc.createElement("timestamp");
        elem.appendChild(doc.createTextNode(original.timestamp.toString().trim()));
        root.appendChild(elem);
      }      

      if((isExternal == null && original.isExternal != null) || 
         (isExternal != null && !isExternal.equals(original.isExternal))){
        Element elem = doc.createElement("is_external");
        elem.appendChild(doc.createTextNode(original.isExternal.toString().trim()));
        root.appendChild(elem);
      }      

      if((systemUser == null && original.systemUser != null) || 
         (systemUser != null && !systemUser.equals(original.systemUser))){
        Element elem = doc.createElement("system_user");
        elem.appendChild(doc.createTextNode(original.systemUser.toString().trim()));
        root.appendChild(elem);
      }      

      if((subject == null && original.subject != null) || 
         (subject != null && !subject.equals(original.subject))){
        Element elem = doc.createElement("subject");
        elem.appendChild(doc.createTextNode(original.subject.toString().trim()));
        root.appendChild(elem);
      }      

      if((text == null && original.text != null) || 
         (text != null && !text.equals(original.text))){
        Element elem = doc.createElement("text");
        elem.appendChild(doc.createTextNode(original.text.toString().trim()));
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
    return "note";
  }
  
}   
