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
 * Lock Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@Entity
@Table(name = "lock")
public class Lock {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "reference_table_id")
    private Integer referenceTableId;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "expires")
    private Date    expires;

    @Column(name = "system_user_id")
    private Integer systemUserId;

    @Column(name = "session_id")
    private String  sessionId;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId,this.referenceId))
            this.referenceId = referenceId;
    }

    public Datetime getExpires() {
        return DataBaseUtil.toYM(expires);
    }

    public void setExpires(Datetime expires) {
        if ( DataBaseUtil.isDifferentYM(expires,this.expires))
            this.expires = DataBaseUtil.toDate(expires);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (DataBaseUtil.isDifferent(sessionId, this.sessionId))
            this.sessionId = sessionId;
    }

}
