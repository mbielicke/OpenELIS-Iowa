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

/**
 * Class represents the fields in database table instrument
 */

public class InstrumentDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, typeId, scriptletId;
    protected String          name, description, modelNumber, serialNumber, location, isActive;
    protected Datetime        activeBegin, activeEnd;

    public InstrumentDO() {
    }

    public InstrumentDO(Integer id, String name, String description, String modelNumber,
                        String serialNumber, Integer typeId, String location, String isActive,
                        Date activeBegin, Date activeEnd, Integer scriptletId) {
        setId(id);
        setName(name);
        setDescription(description);
        setModelNumber(modelNumber);
        setSerialNumber(serialNumber);
        setTypeId(typeId);
        setLocation(location);
        setIsActive(isActive);
        setActiveBegin(DataBaseUtil.toYD(activeBegin));
        setActiveEnd(DataBaseUtil.toYD(activeEnd));
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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = DataBaseUtil.trim(modelNumber);
        _changed = true;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = DataBaseUtil.trim(serialNumber);
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Datetime activeBegin) {
        this.activeBegin = DataBaseUtil.toYD(activeBegin);
        _changed = true;
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Datetime activeEnd) {
        this.activeEnd = DataBaseUtil.toYD(activeEnd);
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