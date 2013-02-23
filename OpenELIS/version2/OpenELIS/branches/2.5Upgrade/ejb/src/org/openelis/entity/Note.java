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
 * Note Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Note.FetchById",
                query = "select new org.openelis.domain.NoteViewDO(n.id,n.referenceId,n.referenceTableId," +
                		"n.timestamp,n.isExternal,n.systemUserId,n.subject,n.text, '')"
                      + "  from Note n where  n.referenceId = :id and n.referenceTableId = :tableId ORDER BY n.timestamp DESC"),
   @NamedQuery( name = "Note.FetchByIds",
               query = "select new org.openelis.domain.NoteViewDO(n.id,n.referenceId,n.referenceTableId," +
                       "n.timestamp,n.isExternal,n.systemUserId,n.subject,n.text, '')"
                     + "  from Note n where  n.referenceId in (:ids) and n.referenceTableId = :tableId ORDER BY n.referenceId, n.timestamp DESC"),                  
    @NamedQuery( name = "Note.FetchByRefTableRefIdIsExternal",
                 query = "select new org.openelis.domain.NoteViewDO(n.id,n.referenceId,n.referenceTableId," +
                         "n.timestamp,n.isExternal,n.systemUserId,n.subject,n.text, '')"
                       + "  from Note n where n.referenceTableId = :referenceTable and n.referenceId = :id and n.isExternal=:isExternal ORDER BY n.timestamp DESC")})
@Entity
@Table(name = "note")
@EntityListeners({AuditUtil.class})
public class Note implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "reference_table_id")
    private Integer referenceTableId;

    @Column(name = "timestamp")
    private Date    timestamp;

    @Column(name = "is_external")
    private String  isExternal;

    @Column(name = "system_user_id")
    private Integer systemUserId;

    @Column(name = "subject")
    private String  subject;

    @Column(name = "text")
    private String  text;

    @Transient
    private Note    original;

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

    public Datetime getTimestamp() {
        return DataBaseUtil.toYS(timestamp);
    }

    public void setTimestamp(Datetime timestamp) {
        if (DataBaseUtil.isDifferentYS(timestamp, this.timestamp))
            this.timestamp = DataBaseUtil.toDate(timestamp);
    }

    public String getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(String isExternal) {
        if (DataBaseUtil.isDifferent(isExternal, this.isExternal))
            this.isExternal = isExternal;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if (DataBaseUtil.isDifferent(subject, this.subject))
            this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (DataBaseUtil.isDifferent(text, this.text))
            this.text = text;
    }

    public void setClone() {
        try {
            original = (Note)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().NOTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId)
                 .setField("timestamp", timestamp, original.timestamp)
                 .setField("is_external", isExternal, original.isExternal)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("subject", subject, original.subject)
                 .setField("text", text, original.text);

        return audit;
    }
}
