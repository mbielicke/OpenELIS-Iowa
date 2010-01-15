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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

@NamedQueries({
    @NamedQuery( name  = "History.FetchByReferenceIdAndTable",
                 query = "select new org.openelis.domain.HistoryVO(h.id,h.referenceId,h.referenceTableId," +
                         "h.timestamp,h.activityId,h.systemUserId,h.changes)"
                       + " from History h where referenceId = :referenceId and referenceTableId = :referenceTableId")})

@Entity
@Table(name = "history")
public class History {
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

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "system_user_id")
    private Integer systemUserId;
    
    @Lob
    @Column(name = "changes")
    private String  changes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    public Datetime getTimestamp() {
        return DataBaseUtil.toYS(timestamp);
    }

    public void setTimestamp(Datetime timestamp) {
        this.timestamp = DataBaseUtil.toDate(timestamp);
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activity) {
        this.activityId = activity;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUser) {
        this.systemUserId = systemUser;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }
}