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

/**
 * The class extends worksheet reagent DO and carries commonly used fields i.e. qc
 * name and prepared by name. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any changes 
 * to read/display fields.
 */

public class WorksheetReagentViewDO extends WorksheetReagentDO {

    private static final long serialVersionUID = 1L;

    protected Datetime expireDate, preparedDate, usableDate;
    protected Double   preparedVolume;
    protected Integer  locationId, preparedById, preparedUnitId;
    protected String   isActive, location, lotNumber, preparedByName, preparedUnit,
                       qcName;

    public WorksheetReagentViewDO() {
    }

    public WorksheetReagentViewDO(Integer id, Integer worksheetId, Integer sortOrder,
                                  Integer qcLotId, String lotNumber, Integer locationId,
                                  Date preparedDate,  Double preparedVolume, Integer preparedUnitId,
                                  Integer preparedById, Date usableDate, Date expireDate,
                                  String isActive, String location, String preparedUnit,
                                  String qcName) {
        super(id, worksheetId, sortOrder, qcLotId);
        setLotNumber(lotNumber);
        setLocationId(locationId);
        setPreparedDate(DataBaseUtil.toYM(preparedDate));
        setPreparedVolume(preparedVolume);
        setPreparedUnitId(preparedUnitId);
        setPreparedById(preparedById);
        setUsableDate(DataBaseUtil.toYM(usableDate));
        setExpireDate(DataBaseUtil.toYM(expireDate));
        setIsActive(isActive);
        setLocation(location);
        setPreparedUnit(preparedUnit);
        setQcName(qcName);
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
    }
    
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Datetime getPreparedDate() {
        return preparedDate;
    }

    public void setPreparedDate(Datetime preparedDate) {
        this.preparedDate = DataBaseUtil.toYM(preparedDate);
    }

    public Double getPreparedVolume() {
        return preparedVolume;
    }

    public void setPreparedVolume(Double preparedVolume) {
        this.preparedVolume = preparedVolume;
    }

    public Integer getPreparedUnitId() {
        return preparedUnitId;
    }

    public void setPreparedUnitId(Integer preparedUnitId) {
        this.preparedUnitId = preparedUnitId;
    }

    public Integer getPreparedById() {
        return preparedById;
    }

    public void setPreparedById(Integer preparedById) {
        this.preparedById = preparedById;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isAtive) {
        this.isActive = DataBaseUtil.trim(isAtive);
    }

    public Datetime getUsableDate() {
        return usableDate;
    }

    public void setUsableDate(Datetime usableDate) {
        this.usableDate = DataBaseUtil.toYM(usableDate);
    }

    public Datetime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Datetime expireDate) {
        this.expireDate = DataBaseUtil.toYM(expireDate);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }
    
    public String getPreparedUnit() {
        return preparedUnit;
    }

    public void setPreparedUnit(String preparedUnit) {
        this.preparedUnit = DataBaseUtil.trim(preparedUnit);
    }
    
    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = DataBaseUtil.trim(qcName);
    }

    public String getPreparedByName() {
        return preparedByName;
    }

    public void setPreparedByName(String preparedByName) {
        this.preparedByName = DataBaseUtil.trim(preparedByName);
    }
}