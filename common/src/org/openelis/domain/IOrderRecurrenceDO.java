/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

/**
 * Class represents the fields in database table order_recurrence
 */

public class IOrderRecurrenceDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, iorderId, frequency, unitId;
    protected String          isActive;
    protected Datetime        activeBegin, activeEnd;

    public IOrderRecurrenceDO() {
    }
    
    public IOrderRecurrenceDO(Integer id, Integer iorderId, String isActive,
                             Date activeBegin, Date activeEnd, 
                             Integer frequency, Integer unitId) {
        setId(id);
        setIorderId(iorderId);
        setIsActive(isActive);
        setActiveBegin(DataBaseUtil.toYD(activeBegin));
        setActiveEnd(DataBaseUtil.toYD(activeEnd));
        setFrequency(frequency);
        setUnitId(unitId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getIorderId() {
        return iorderId;
    }

    public void setIorderId(Integer iorderId) {
        this.iorderId = iorderId;
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

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
        _changed = true;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
        _changed = true;
    }
}
