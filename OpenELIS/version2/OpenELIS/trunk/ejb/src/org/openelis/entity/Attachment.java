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
  * Attachment Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
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

  @Column(name="type_id")
  private Integer typeId;             

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
      if (DataBaseUtil.isDifferent(id, this.id))
          this.id = id;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if(DataBaseUtil.isDifferent(typeId,this.typeId))
      this.typeId = typeId;
  }

  public String getFilename() {
    return filename;
  }
  public void setFilename(String filename) {
    if(DataBaseUtil.isDifferent(filename,this.filename))
      this.filename = filename;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if(DataBaseUtil.isDifferent(description,this.description))
      this.description = description;
  }

  public String getStorageReference() {
    return storageReference;
  }
  public void setStorageReference(String storageReference) {
    if(DataBaseUtil.isDifferent(storageReference,this.storageReference))
      this.storageReference = storageReference;
  }

  
  public void setClone() {
    try {
        original = (Attachment)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ATTACHMENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("filename", filename, original.filename)
                 .setField("description", description, original.description)
                 .setField("storage_reference", storageReference, original.storageReference);

        return audit;
  }
  
}   
