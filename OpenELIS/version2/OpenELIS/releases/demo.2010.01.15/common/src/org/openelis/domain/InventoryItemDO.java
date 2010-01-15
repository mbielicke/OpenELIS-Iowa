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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table inventory_item.  
 */
public class InventoryItemDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, categoryId, storeId, quantityMinLevel, quantityMaxLevel,
                              quantityToReorder, dispensedUnitsId, averageLeadTime,
                              averageDailyUse, parentInventoryItemId, parentRatio;
    protected String          name, description, isReorderAuto, isLotMaintained,
                              isSerialMaintained, isActive, isBulk, isNotForSale,
                              isSubAssembly, isLabor, isNotInventoried, productUri;
    protected Double          averageCost;

    public InventoryItemDO() {
    }

    public InventoryItemDO(Integer id, String name, String description, Integer categoryId,
                           Integer storeId, Integer quantityMinLevel, Integer quantityMaxLevel,
                           Integer quantityToReorder, Integer dispensedUnitsId,
                           String isReorderAuto, String isLotMaintained, String isSerialMaintained,
                           String isActive, String isBulk, String isNotForSale,
                           String isSubAssembly, String isLabor, String isNotInventoried,
                           String productUri, Integer averageLeadTime, Double averageCost,
                           Integer averageDailyUse, Integer parentInventoryItemId, Integer parentRatio) {
        setId(id);
        setName(name);
        setDescription(description);
        setCategoryId(categoryId);
        setStoreId(storeId);
        setQuantityMinLevel(quantityMinLevel);
        setQuantityMaxLevel(quantityMaxLevel);
        setQuantityToReorder(quantityToReorder);
        setDispensedUnitsId(dispensedUnitsId);
        setIsReorderAuto(isReorderAuto);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
        setIsActive(isActive);
        setIsBulk(isBulk);
        setIsNotForSale(isNotForSale);
        setIsSubAssembly(isSubAssembly);
        setIsLabor(isLabor);
        setIsNotInventoried(isNotInventoried);
        setProductUri(productUri);
        setAverageLeadTime(averageLeadTime);
        setAverageCost(averageCost);
        setAverageDailyUse(averageDailyUse);
        setParentInventoryItemId(parentInventoryItemId);
        setParentRatio(parentRatio);
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        _changed = true;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
        _changed = true;
    }

    public Integer getQuantityMinLevel() {
        return quantityMinLevel;
    }

    public void setQuantityMinLevel(Integer quantityMinLevel) {
        this.quantityMinLevel = quantityMinLevel;
        _changed = true;
    }

    public Integer getQuantityMaxLevel() {
        return quantityMaxLevel;
    }

    public void setQuantityMaxLevel(Integer quantityMaxLevel) {
        this.quantityMaxLevel = quantityMaxLevel;
        _changed = true;
    }

    public Integer getQuantityToReorder() {
        return quantityToReorder;
    }

    public void setQuantityToReorder(Integer quantityToReorder) {
        this.quantityToReorder = quantityToReorder;
        _changed = true;
    }

    public Integer getDispensedUnitsId() {
        return dispensedUnitsId;
    }

    public void setDispensedUnitsId(Integer dispensedUnitsId) {
        this.dispensedUnitsId = dispensedUnitsId;
        _changed = true;
    }

    public String getIsReorderAuto() {
        return isReorderAuto;
    }

    public void setIsReorderAuto(String isReorderAuto) {
        this.isReorderAuto = DataBaseUtil.trim(isReorderAuto);
        _changed = true;
    }

    public String getIsLotMaintained() {
        return isLotMaintained;
    }

    public void setIsLotMaintained(String isLotMaintained) {
        this.isLotMaintained = DataBaseUtil.trim(isLotMaintained);
        _changed = true;
    }

    public String getIsSerialMaintained() {
        return isSerialMaintained;
    }

    public void setIsSerialMaintained(String isSerialMaintained) {
        this.isSerialMaintained = DataBaseUtil.trim(isSerialMaintained);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getIsBulk() {
        return isBulk;
    }

    public void setIsBulk(String isBulk) {
        this.isBulk = DataBaseUtil.trim(isBulk);
        _changed = true;
    }

    public String getIsNotForSale() {
        return isNotForSale;
    }

    public void setIsNotForSale(String isNotForSale) {
        this.isNotForSale = DataBaseUtil.trim(isNotForSale);
        _changed = true;
    }

    public String getIsSubAssembly() {
        return isSubAssembly;
    }

    public void setIsSubAssembly(String isSubAssembly) {
        this.isSubAssembly = DataBaseUtil.trim(isSubAssembly);
        _changed = true;
    }

    public String getIsLabor() {
        return isLabor;
    }

    public void setIsLabor(String isLabor) {
        this.isLabor = DataBaseUtil.trim(isLabor);
        _changed = true;
    }

    public String getIsNotInventoried() {
        return isNotInventoried;
    }

    public void setIsNotInventoried(String isNotInventoried) {
        this.isNotInventoried = DataBaseUtil.trim(isNotInventoried);
        _changed = true;
    }

    public String getProductUri() {
        return productUri;
    }

    public void setProductUri(String productUri) {
        this.productUri = DataBaseUtil.trim(productUri);
        _changed = true;
    }

    public Integer getAverageLeadTime() {
        return averageLeadTime;
    }

    public void setAverageLeadTime(Integer averageLeadTime) {
        this.averageLeadTime = averageLeadTime;
        _changed = true;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
        _changed = true;
    }

    public Integer getAverageDailyUse() {
        return averageDailyUse;
    }

    public void setAverageDailyUse(Integer averageDailyUse) {
        this.averageDailyUse = averageDailyUse;
        _changed = true;
    }

    public Integer getParentInventoryItemId() {
        return parentInventoryItemId;
    }

    public void setParentInventoryItemId(Integer parentInventoryItemId) {
        this.parentInventoryItemId = parentInventoryItemId;
        _changed = true;
    }

    public Integer getParentRatio() {
        return parentRatio;
    }

    public void setParentRatio(Integer parentRatio) {
        this.parentRatio = parentRatio;
        _changed = true;
    }
}