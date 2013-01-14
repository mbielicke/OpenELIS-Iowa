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

package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

import org.openelis.gwt.common.RPC;

/**
 * The class is used to carry the fields in history table and some additional
 * fields. The fields are considered read/display and do not get committed to
 * the database.
 */

public class HistoryVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id, referenceId, referenceTableId, activityId, systemUserId;
    protected Datetime        timestamp;
    protected String          changes, systemUserLoginName;

    public HistoryVO() {
    }

    public HistoryVO(Integer id, Integer referenceId, Integer referenceTableId, Date timestamp,
                     Integer activityId, Integer systemUserId, String changes) {
        setId(id);
        setReferenceId(referenceId);
        setReferenceTableId(referenceTableId);
        setTimestamp(DataBaseUtil.toYS(timestamp));
        setActivityId(activityId);
        setSystemUserId(systemUserId);
        setChanges(changes);
    }

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

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
    }

    public String getSystemUserLoginName() {
        return systemUserLoginName;
    }

    public void setSystemUserLoginName(String systemUserLoginName) {
        this.systemUserLoginName = DataBaseUtil.trim(systemUserLoginName);
    }

    public Datetime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Datetime timestamp) {
        this.timestamp = DataBaseUtil.toYS(timestamp);
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = DataBaseUtil.trim(changes);
    }
}