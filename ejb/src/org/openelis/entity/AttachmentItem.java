
package org.openelis.entity;

/**
  * AttachmentItem Entity POJO for database 
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
@Table(name="attachment_item")
@EntityListeners({AuditUtil.class})
public class AttachmentItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="attachment")
  private Integer attachment;             


  @Transient
  private AttachmentItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    this.referenceId = referenceId;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    this.referenceTable = referenceTable;
  }

  public Integer getAttachment() {
    return attachment;
  }
  public void setAttachment(Integer attachment) {
    this.attachment = attachment;
  }

  
  public void setClone() {
    try {
      original = (AttachmentItem)this.clone();
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

      if((referenceId == null && original.referenceId != null) || 
         (referenceId != null && !referenceId.equals(original.referenceId))){
        Element elem = doc.createElement("reference_id");
        elem.appendChild(doc.createTextNode(original.referenceId.toString()));
        root.appendChild(elem);
      }      

      if((referenceTable == null && original.referenceTable != null) || 
         (referenceTable != null && !referenceTable.equals(original.referenceTable))){
        Element elem = doc.createElement("reference_table");
        elem.appendChild(doc.createTextNode(original.referenceTable.toString()));
        root.appendChild(elem);
      }      

      if((attachment == null && original.attachment != null) || 
         (attachment != null && !attachment.equals(original.attachment))){
        Element elem = doc.createElement("attachment");
        elem.appendChild(doc.createTextNode(original.attachment.toString()));
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
    return "attachment_item";
  }
  
}   
