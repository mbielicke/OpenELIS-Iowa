/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.entity;

/**
 * Attachment Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery( name = "Attachment.FetchById",
                           query = "select new org.openelis.domain.AttachmentDO(a.id,a.createdDate,a.typeId,a.sectionId,a.description,a.storageReference)"
                                   + " from Attachment a where a.id = :id"),
               @NamedQuery( name = "Attachment.FetchByIds",
                           query = "select distinct new org.openelis.domain.AttachmentDO(a.id,a.createdDate,a.typeId,a.sectionId,a.description,a.storageReference)"
                                   + " from Attachment a where a.id in (:ids)"),
               @NamedQuery( name = "Attachment.FetchByIdsDescending",
                           query = "select distinct new org.openelis.domain.AttachmentDO(a.id,a.createdDate,a.typeId,a.sectionId,a.description,a.storageReference)"
                                   + " from Attachment a where a.id in (:ids) order by a.id desc"),               
               @NamedQuery( name = "Attachment.FetchForRemove",
                           query = "select distinct new org.openelis.domain.AttachmentDO(a.id,a.createdDate,a.typeId,a.sectionId,a.description,a.storageReference)"
                                 + " from Attachment a where a.createdDate < :createdDate and a.id not in (select i.attachmentId from AttachmentItem i)"
                                 + " and a.id not in (select i.attachmentId from AttachmentIssue i)"),
               @NamedQuery( name = "Attachment.FetchByDescriptionReferenceIdReferenceTableId",
                           query = "select distinct new org.openelis.domain.AttachmentDO(a.id,a.createdDate,a.typeId,a.sectionId,a.description,a.storageReference)"
                                 + " from Attachment a left join a.attachmentItem i where a.description like (:description) and i.referenceId = :referenceId"
                                 + " and i.referenceTableId = :referenceTableId")})
@Entity
@Table(name = "attachment")
@EntityListeners({AuditUtil.class})
public class Attachment implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                    id;

    @Column(name = "created_date")
    private Date                       createdDate;

    @Column(name = "type_id")
    private Integer                    typeId;

    @Column(name = "section_id")
    private Integer                    sectionId;

    @Column(name = "description")
    private String                     description;

    @Column(name = "storage_reference")
    private String                     storageReference;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", insertable = false, updatable = false)
    private Collection<AttachmentItem> attachmentItem;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", insertable = false, updatable = false)
    private Collection<AttachmentIssue> attachmentIssue;

    @Transient
    private Attachment                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Datetime getCreatedDate() {
        return DataBaseUtil.toYM(createdDate);
    }

    public void setCreatedDate(Datetime createdDate) {
        if (DataBaseUtil.isDifferentYM(createdDate, this.createdDate))
            this.createdDate = DataBaseUtil.toDate(createdDate);
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        if (DataBaseUtil.isDifferent(sectionId, this.sectionId))
            this.sectionId = sectionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        if (DataBaseUtil.isDifferent(storageReference, this.storageReference))
            this.storageReference = storageReference;
    }

    public Collection<AttachmentItem> getAttachmentItem() {
        return attachmentItem;
    }

    public void setAttachmentItem(Collection<AttachmentItem> attachmentItem) {
        this.attachmentItem = attachmentItem;
    }

    public Collection<AttachmentIssue> getAttachmentIssue() {
        return attachmentIssue;
    }

    public void setAttachmentIssue(Collection<AttachmentIssue> attachmentIssue) {
        this.attachmentIssue = attachmentIssue;
    }

    public void setClone() {
        try {
            original = (Attachment)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ATTACHMENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("created_date", createdDate, original.createdDate)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("section_id", sectionId, original.sectionId, Constants.table().SECTION)
                 .setField("description", description, original.description)
                 .setField("storage_reference", storageReference, original.storageReference);

        return audit;
    }
}
