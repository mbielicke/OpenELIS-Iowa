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
 * Preferences Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "getPreference", 
                query = "select new org.openelis.domain.PreferencesDO(id,systemUserId,key,text) from Preferences where systemUserId = :systemUser and key = :key")})
@Entity
@Table(name = "preferences")
@EntityListeners( {AuditUtil.class})
public class Preferences implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer     id;

    @Column(name = "system_user_id")
    private Integer     systemUserId;

    @Column(name = "key")
    private String      key;

    @Column(name = "text")
    private String      text;

    @Transient
    private Preferences original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (DataBaseUtil.isDifferent(key, this.key))
            this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (DataBaseUtil.isDifferent(text ,this.text))
            this.text = text;
    }

    public void setClone() {
        try {
            original = (Preferences)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.PREFERENCES);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("key", key, original.key)
                 .setField("text", text, original.text);

        return audit;
    }
}