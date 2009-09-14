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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table analyte.
 */
public class AnalyteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, parentAnalyteId;
    protected String          name, isActive, parentAnalyte, externalId;

    public AnalyteDO() {
    }

    public AnalyteDO(Integer id, String name, String isActive, Integer parentAnalyteId,
                     String parentAnalyte, String externalId) {
        setId(id);
        setName(name);
        setIsActive(isActive);
        setParentAnalyteId(parentAnalyteId);
        setParentAnalyte(parentAnalyte);
        setExternalId(externalId);
        _changed = false;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = DataBaseUtil.trim(externalId);
        _changed = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getParentAnalyte() {
        return parentAnalyte;
    }

    public void setParentAnalyte(String parentAnalyte) {
        this.parentAnalyte = DataBaseUtil.trim(parentAnalyte);
        _changed = true;
    }

    public Integer getParentAnalyteId() {
        return parentAnalyteId;
    }

    public void setParentAnalyteId(Integer parentAnalyteId) {
        this.parentAnalyteId = parentAnalyteId;
        _changed = true;
    }
}