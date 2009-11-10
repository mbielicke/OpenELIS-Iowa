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
 * Class represents the fields in database table project.
 */

public class ProjectDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, ownerId, scriptletId;
    protected String          name, description, isActive, referenceTo, ownerName;
    protected Datetime        startedDate, completedDate;

    public ProjectDO() {
    }

    public ProjectDO(Integer id, String name, String description, Date startedDate,
                     Date completedDate, String isActive, String referenceTo, Integer ownerId,
                     Integer scriptletId) {
        setId(id);
        setName(name);
        setDescription(description);
        setStartedDate(DataBaseUtil.toYD(startedDate));
        setCompletedDate(DataBaseUtil.toYD(completedDate));
        setIsActive(isActive);
        setReferenceTo(referenceTo);
        setOwnerId(ownerId);
        setScriptletId(scriptletId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
        _changed = true;
    }

    public Datetime getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Datetime startedDate) {
        this.startedDate = DataBaseUtil.toYD(startedDate);
        _changed = true;
    }

    public Datetime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Datetime completedDate) {
        this.completedDate = DataBaseUtil.toYD(completedDate);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getReferenceTo() {
        return referenceTo;
    }

    public void setReferenceTo(String referenceTo) {
        this.referenceTo = DataBaseUtil.trim(referenceTo);
        _changed = true;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
        _changed = true;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
        _changed = true;
    }
}
