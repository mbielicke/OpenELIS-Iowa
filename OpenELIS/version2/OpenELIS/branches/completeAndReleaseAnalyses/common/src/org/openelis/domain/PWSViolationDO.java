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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class PWSViolationDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, tinwsysIsNumber, sampleId;

    protected String          facilityId, series;

    protected Datetime        violationDate;

    public PWSViolationDO() {
    }

    public PWSViolationDO(Integer id, Integer tinwsysIsNumber, String facilityId, String series,
                          Date violationDate, Integer sampleId) {
        setId(id);
        setTinwsysIsNumber(tinwsysIsNumber);
        setFacilityId(facilityId);
        setSeries(series);
        setViolationDate(DataBaseUtil.toYD(violationDate));
        setSampleId(sampleId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
        _changed = true;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = DataBaseUtil.trim(facilityId);
        _changed = true;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = DataBaseUtil.trim(series);
        _changed = true;
    }

    public Datetime getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(Datetime violationDate) {
        this.violationDate = DataBaseUtil.toYD(violationDate);
        _changed = true;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
        _changed = true;
    }
}
