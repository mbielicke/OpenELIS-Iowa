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

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table system_variable.
 */

public class WorksheetDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer  id, systemUserId, statusId, formatId;
    protected Datetime createdDate;

    public WorksheetDO() {
    }

    public WorksheetDO(Integer id, Date createdDate, Integer systemUserId,
                       Integer statusId, Integer formatId) {
        setId(id);
        setCreatedDate(DataBaseUtil.toYM(createdDate));
        setSystemUserId(systemUserId);
        setStatusId(statusId);
        setFormatId(formatId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Datetime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Datetime createdDate) {
        this.createdDate = DataBaseUtil.toYM(createdDate);
        _changed = true;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
        _changed = true;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }

    public Integer getFormatId() {
        return formatId;
    }

    public void setFormatId(Integer formatId) {
        this.formatId = formatId;
        _changed = true;
    }
}