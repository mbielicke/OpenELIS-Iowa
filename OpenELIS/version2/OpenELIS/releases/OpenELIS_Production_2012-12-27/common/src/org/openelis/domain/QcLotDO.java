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
 * Class represents the fields in database table qc_lot.
 */

public class QcLotDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, qcId, preparedUnitId, preparedById, locationId;
    protected String          lotNumber, isActive;
    protected Datetime        preparedDate, usableDate, expireDate;
    protected Double          preparedVolume;

    public QcLotDO() {
    }

    public QcLotDO(Integer id, Integer qcId, String lotNumber, Integer locationId,
                   Date preparedDate, Double preparedVolume, Integer preparedUnitId,
                   Integer preparedById, Date usableDate, Date expireDate,
                   String isActive) {
        setId(id);
        setQcId(qcId);
        setLotNumber(lotNumber);
        setLocationId(locationId);
        setPreparedDate(DataBaseUtil.toYM(preparedDate));
        setPreparedVolume(preparedVolume);
        setPreparedUnitId(preparedUnitId);
        setPreparedById(preparedById);
        setUsableDate(DataBaseUtil.toYM(usableDate));
        setExpireDate(DataBaseUtil.toYM(expireDate));
        setIsActive(isActive);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer typeId) {
        this.qcId = typeId;
        _changed = true;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
        _changed = true;
    }
    
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
        _changed = true;
    }

    public Double getPreparedVolume() {
        return preparedVolume;
    }

    public void setPreparedVolume(Double preparedVolume) {
        this.preparedVolume = preparedVolume;
        _changed = true;
    }

    public Integer getPreparedUnitId() {
        return preparedUnitId;
    }

    public void setPreparedUnitId(Integer preparedUnitId) {
        this.preparedUnitId = preparedUnitId;
        _changed = true;
    }

    public Integer getPreparedById() {
        return preparedById;
    }

    public void setPreparedById(Integer preparedById) {
        this.preparedById = preparedById;
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isAtive) {
        this.isActive = DataBaseUtil.trim(isAtive);
        _changed = true;
    }

    public Datetime getPreparedDate() {
        return preparedDate;
    }

    public void setPreparedDate(Datetime preparedDate) {
        this.preparedDate = DataBaseUtil.toYM(preparedDate);
        _changed = true;
    }

    public Datetime getUsableDate() {
        return usableDate;
    }

    public void setUsableDate(Datetime usableDate) {
        this.usableDate = DataBaseUtil.toYM(usableDate);
        _changed = true;
    }

    public Datetime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Datetime expireDate) {
        this.expireDate = DataBaseUtil.toYM(expireDate);
        _changed = true;
    }
}