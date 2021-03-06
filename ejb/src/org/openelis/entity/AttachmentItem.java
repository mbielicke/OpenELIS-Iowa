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
 * AttachmentItem Entity POJO for database
 */

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.AuditUtil;

@NamedQueries({
    @NamedQuery( name = "AttachmentItem.FetchById",
                query = "select new org.openelis.domain.AttachmentItemDO(ai.id,ai.referenceId,ai.referenceTableId," +
                        "ai.attachmentId)"
                      + " from AttachmentItem ai where ai.referenceId = :id and ai.referenceTableId = :tableId"),
   @NamedQuery( name = "AttachmentItem.FetchByIds",
               query = "select new org.openelis.domain.AttachmentItemViewDO(ai.id,ai.referenceId,ai.referenceTableId," +
                       "ai.attachmentId,a.createdDate,a.sectionId,a.description,'')"
                     + " from AttachmentItem ai left join ai.attachment a where ai.referenceId in (:ids) and ai.referenceTableId = :tableId"),
   @NamedQuery( name = "AttachmentItem.FetchByAttachmentIds",
               query = "select new org.openelis.domain.AttachmentItemViewDO(ai.id,ai.referenceId,ai.referenceTableId," +
                       "ai.attachmentId,a.createdDate,a.sectionId,a.description,'')"
                     + " from AttachmentItem ai left join ai.attachment a where ai.attachmentId in (:ids)")})                  

@Entity
@Table(name = "attachment_item")
@EntityListeners({AuditUtil.class})
public class AttachmentItem implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer        id;

    @Column(name = "reference_id")
    private Integer        referenceId;

    @Column(name = "reference_table_id")
    private Integer        referenceTableId;

    @Column(name = "attachment_id")
    private Integer        attachmentId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", insertable = false, updatable = false)
    private Attachment     attachment;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId, this.referenceId))
            this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        if (DataBaseUtil.isDifferent(attachmentId, this.attachmentId))
            this.attachmentId = attachmentId;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }
}