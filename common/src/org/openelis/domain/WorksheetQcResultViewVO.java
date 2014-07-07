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
 * Class represents the fields in database table worksheet_qc_result_view.
 */

public class WorksheetQcResultViewVO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, worksheetAnalysisId, sortOrder, qcAnalyteId, qcId,
                              qcTypeId, inventoryItemId, qcLotId, locationId, preparedUnitId,
                              preparedById, analyteId, qcAnalyteTypeId;
    protected String          values[], qcName, source, qcIsActive, lotNumber, qcLotIsActive,
                              expectedValue, isTrendable, qcType, location, preparedUnit,
                              analyteName, qcAnalyteType;
    protected Datetime        preparedDate, usableDate, expireDate;
    protected Double          preparedVolume;

    public WorksheetQcResultViewVO() {
        values = new String[30];
    }

    public WorksheetQcResultViewVO(Integer id, Integer worksheetAnalysisId, Integer sortOrder,
                               Integer qcAnalyteId, String v1, String v2, String v3,
                               String v4, String v5, String v6, String v7, String v8,
                               String v9, String v10, String v11, String v12, String v13,
                               String v14, String v15, String v16, String v17, String v18,
                               String v19, String v20, String v21, String v22, String v23,
                               String v24, String v25, String v26, String v27, String v28,
                               String v29, String v30, Integer qcId, String qcName,
                               Integer qcTypeId, Integer inventoryItemId, String source,
                               String qcIsActive, Integer qcLotId, String lotNumber,
                               Integer locationId, Date preparedDate, Double preparedVolume,
                               Integer preparedUnitId, Integer preparedById, Date usableDate,
                               Date expireDate, String qcLotIsActive, Integer analyteId,
                               Integer qcAnalyteTypeId, String expectedValue, String isTrendable,
                               String qcType, String location, String preparedUnit,
                               String analyteName, String qcAnalyteType) {
        values = new String[30];
        
        setId(id);
        setWorksheetAnalysisId(worksheetAnalysisId);
        setSortOrder(sortOrder);
        setQcAnalyteId(qcAnalyteId);
        setValueAt(0, v1);
        setValueAt(1, v2);
        setValueAt(2, v3);
        setValueAt(3, v4);
        setValueAt(4, v5);
        setValueAt(5, v6);
        setValueAt(6, v7);
        setValueAt(7, v8);
        setValueAt(8, v9);
        setValueAt(9, v10);
        setValueAt(10, v11);
        setValueAt(11, v12);
        setValueAt(12, v13);
        setValueAt(13, v14);
        setValueAt(14, v15);
        setValueAt(15, v16);
        setValueAt(16, v17);
        setValueAt(17, v18);
        setValueAt(18, v19);
        setValueAt(19, v20);
        setValueAt(20, v21);
        setValueAt(21, v22);
        setValueAt(22, v23);
        setValueAt(23, v24);
        setValueAt(24, v25);
        setValueAt(25, v26);
        setValueAt(26, v27);
        setValueAt(27, v28);
        setValueAt(28, v29);
        setValueAt(29, v30);
        setQcId(qcId);
        setQcName(qcName);
        setQcTypeId(qcTypeId);
        setInventoryItemId(inventoryItemId);
        setSource(source);
        setQcIsActive(qcIsActive);
        setQcLotId(qcLotId);
        setLotNumber(lotNumber);
        setLocationId(locationId);
        setPreparedDate(DataBaseUtil.toYM(preparedDate));
        setPreparedVolume(preparedVolume);
        setPreparedUnitId(preparedUnitId);
        setPreparedById(preparedById);
        setUsableDate(DataBaseUtil.toYM(usableDate));
        setExpireDate(DataBaseUtil.toYM(expireDate));
        setQcLotIsActive(qcLotIsActive);
        setAnalyteId(analyteId);
        setQcAnalyteTypeId(qcAnalyteTypeId);
        setExpectedValue(expectedValue);
        setIsTrendable(isTrendable);
        setQcType(qcType);
        setLocation(location);
        setPreparedUnit(preparedUnit);
        setAnalyteName(analyteName);
        setQcAnalyteType(qcAnalyteType);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        this.worksheetAnalysisId = worksheetAnalysisId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getQcAnalyteId() {
        return qcAnalyteId;
    }

    public void setQcAnalyteId(Integer qcAnalyteId) {
        this.qcAnalyteId = qcAnalyteId;
    }

    public String getValueAt(int index) {
        return values[index];
    }

    public void setValueAt(int index, String value) {
        this.values[index] =  DataBaseUtil.trim(value);
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        this.qcId = qcId;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = DataBaseUtil.trim(qcName);
    }

    public Integer getQcTypeId() {
        return qcTypeId;
    }

    public void setQcTypeId(Integer qcTypeId) {
        this.qcTypeId = qcTypeId;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = DataBaseUtil.trim(source);
    }

    public String getQcIsActive() {
        return qcIsActive;
    }

    public void setQcIsActive(String qcIsActive) {
        this.qcIsActive = DataBaseUtil.trim(qcIsActive);
    }

    public Integer getQcLotId() {
        return qcLotId;
    }

    public void setQcLotId(Integer qcLotId) {
        this.qcLotId = qcLotId;
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

    public String getQcLotIsActive() {
        return qcLotIsActive;
    }

    public void setQcLotIsActive(String qcLotIsAtive) {
        this.qcLotIsActive = DataBaseUtil.trim(qcLotIsAtive);
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public Integer getQcAnalyteTypeId() {
        return qcAnalyteTypeId;
    }

    public void setQcAnalyteTypeId(Integer qcAnalyteTypeId) {
        this.qcAnalyteTypeId = qcAnalyteTypeId;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = DataBaseUtil.trim(expectedValue);
    }

    public String getIsTrendable() {
        return isTrendable;
    }

    public void setIsTrendable(String isTrendable) {
        this.isTrendable = DataBaseUtil.trim(isTrendable);
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = DataBaseUtil.trim(qcType);
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

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getQcAnalyteType() {
        return qcAnalyteType;
    }

    public void setQcAnalyteType(String qcAnalyteType) {
        this.qcAnalyteType = DataBaseUtil.trim(qcAnalyteType);
    }
}