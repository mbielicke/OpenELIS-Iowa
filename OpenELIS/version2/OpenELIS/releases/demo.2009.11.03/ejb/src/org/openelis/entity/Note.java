/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * Note Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Note.Notes", query = "select new org.openelis.domain.NoteViewDO(n.id, n.referenceId, n.referenceTableId, n.timestamp, n.isExternal, n.systemUserId, n.subject, n.text, '') "
            + "  from Note n where n.referenceTableId = :referenceTable and n.referenceId = :id ORDER BY n.timestamp DESC")})
              
            
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

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="timestamp")
  private Date timestamp;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="system_user_id")
  private Integer systemUserId;             

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

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  public Datetime getTimestamp() {
    if(timestamp == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,timestamp);
  }

  public void setTimestamp (Datetime timestamp){
      if((timestamp == null && this.timestamp != null) || (timestamp != null && this.timestamp == null) || 
                      (timestamp != null && !timestamp.equals(new Datetime(Datetime.YEAR, Datetime.SECOND, this.timestamp))))
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

  public Integer getSystemUserId() {
    return systemUserId;
  }
  public void setSystemUserId(Integer systemUserId) {
    if((systemUserId == null && this.systemUserId != null) || 
       (systemUserId != null && !systemUserId.equals(this.systemUserId)))
      this.systemUserId = systemUserId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(referenceId,original.referenceId,doc,"reference_id");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      AuditUtil.getChangeXML(timestamp,original.timestamp,doc,"timestamp");

      AuditUtil.getChangeXML(isExternal,original.isExternal,doc,"is_external");

      AuditUtil.getChangeXML(systemUserId,original.systemUserId,doc,"system_user_id");

      AuditUtil.getChangeXML(subject,original.subject,doc,"subject");

      AuditUtil.getChangeXML(text,original.text,doc,"text");

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
