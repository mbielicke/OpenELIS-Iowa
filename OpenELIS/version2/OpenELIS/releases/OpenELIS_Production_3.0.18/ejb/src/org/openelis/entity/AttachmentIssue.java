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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/**
 * AttachmentIssue Entity POJO for database
 */

@NamedQueries({
               @NamedQuery(name = "AttachmentIssue.FetchList",
                           query = "select new org.openelis.domain.AttachmentIssueViewDO(ai.id, ai.attachmentId, ai.timestamp, ai.systemUserId,"
                                   + "ai.text, ai.attachment.description, '')"
                                   + " from AttachmentIssue ai order by ai.timestamp"),
               @NamedQuery(name = "AttachmentIssue.FetchByAttachmentId",
                           query = "select new org.openelis.domain.AttachmentIssueViewDO(ai.id, ai.attachmentId, ai.timestamp, ai.systemUserId,"
                                   + "ai.text, ai.attachment.description, '')"
                                   + " from AttachmentIssue ai where ai.attachmentId = :id"),
               @NamedQuery(name = "AttachmentIssue.FetchByAttachmentIds",
                           query = "select new org.openelis.domain.AttachmentIssueViewDO(ai.id, ai.attachmentId, ai.timestamp, ai.systemUserId,"
                                   + "ai.text, ai.attachment.description, '')"
                                   + " from AttachmentIssue ai where ai.attachmentId in (:ids)")})
@Entity
@Table(name = "attachment_issue")
@EntityListeners({AuditUtil.class})
public class AttachmentIssue implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer         id;

    @Column(name = "attachment_id")
    private Integer         attachmentId;

    @Column(name = "timestamp")
    private Date            timestamp;

    @Column(name = "system_user_id")
    private Integer         systemUserId;

    @Column(name = "text")
    private String          text;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", insertable = false, updatable = false)
    private Attachment      attachment;

    @Transient
    private AttachmentIssue original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        if (DataBaseUtil.isDifferent(attachmentId, this.attachmentId))
            this.attachmentId = attachmentId;
    }

    public Datetime getTimestamp() {
        return DataBaseUtil.toYM(timestamp);
    }

    public void setTimestamp(Datetime timestamp) {
        if (DataBaseUtil.isDifferentYM(timestamp, this.timestamp))
            this.timestamp = DataBaseUtil.toDate(timestamp);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (DataBaseUtil.isDifferent(this.text, text))
            this.text = text;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public void setClone() {
        try {
            original = (AttachmentIssue)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ATTACHMENT_ISSUE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("attachment_id",
                           attachmentId,
                           original.attachmentId,
                           Constants.table().ATTACHMENT)
                 .setField("timestamp", timestamp, original.timestamp)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("text", text, original.text);

        return audit;
    }
}