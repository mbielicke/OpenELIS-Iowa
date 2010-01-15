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
  * AttachmentItem Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

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

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="attachment_id")
  private Integer attachmentId;             


  @Transient
  private AttachmentItem original;

  
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

  public Integer getAttachmentId() {
    return attachmentId;
  }
  public void setAttachmentId(Integer attachmentId) {
    if((attachmentId == null && this.attachmentId != null) || 
       (attachmentId != null && !attachmentId.equals(this.attachmentId)))
      this.attachmentId = attachmentId;
  }

  
  public void setClone() {
    try {
        original = (AttachmentItem)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ATTACHMENT_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId)
                 .setField("attachment_id", attachmentId, original.attachmentId);

        return audit;

  }
  
}   
