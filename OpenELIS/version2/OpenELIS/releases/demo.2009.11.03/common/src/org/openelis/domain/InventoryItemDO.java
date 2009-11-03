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

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryItemDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          name;
    protected String          description;
    protected Integer         category;
    protected Integer         store;
    protected Integer         quantityMinLevel;
    protected Integer         quantityMaxLevel;
    protected Integer         quantityToReorder;
    protected Integer         dispensedUnits;
    protected String          isReorderAuto;
    protected String          isLotMaintained;
    protected String          isSerialMaintained;
    protected String          isActive;
    protected String          isBulk;
    protected String          isNotForSale;
    protected String          isSubAssembly;
    protected String          isLabor;
    protected String          isNoInventory;
    protected String          productUri;
    protected Integer         aveLeadTime;
    protected double          aveCost;
    protected Integer         aveDailyUse;
    protected String          parentInventoryItem;
    protected Integer         parentInventoryItemId;
    protected Integer         parentRatio;
    protected String          manufacturingText;

    public InventoryItemDO() {

    }

    public InventoryItemDO(Integer id,
                           String name,
                           String description,
                           Integer category,
                           Integer store,
                           Integer quantityMinLevel,
                           Integer quantityMaxLevel,
                           Integer quantityToReorder,
                           Integer dispensedUnits,
                           String isReorderAuto,
                           String isLotMaintained,
                           String isSerialMaintained,
                           String isActive,
                           String isBulk,
                           String isNotForSale,
                           String isSubAssembly,
                           String isLabor,
                           String isNoInventory,
                           String productUri,
                           Integer aveLeadTime,
                           double aveCost,
                           Integer aveDailyUse,
                           Integer parentInventoryItemId,
                           String parentInventoryItem,
                           Integer parentRatio) {

        setId(id);
        setName(name);
        setDescription(description);
        setCategory(category);
        setStore(store);
        setQuantityMinLevel(quantityMinLevel);
        setQuantityMaxLevel(quantityMaxLevel);
        setQuantityToReorder(quantityToReorder);
        setDispensedUnits(dispensedUnits);
        setIsReorderAuto(isReorderAuto);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
        setIsActive(isActive);
        setIsBulk(isBulk);
        setIsNotForSale(isNotForSale);
        setIsSubAssembly(isSubAssembly);
        setIsLabor(isLabor);
        setIsNoInventory(isNoInventory);
        setProductUri(productUri);
        setAveLeadTime(aveLeadTime);
        setAveCost(aveCost);
        setAveDailyUse(aveDailyUse);
        setParentInventoryItemId(parentInventoryItemId);
        setParentInventoryItem(parentInventoryItem);
        setParentRatio(parentRatio);
    }

    public double getAveCost() {
        return aveCost;
    }

    public void setAveCost(double aveCost) {
        this.aveCost = aveCost;
    }

    public Integer getAveDailyUse() {
        return aveDailyUse;
    }

    public void setAveDailyUse(Integer aveDailyUse) {
        this.aveDailyUse = aveDailyUse;
    }

    public Integer getAveLeadTime() {
        return aveLeadTime;
    }

    public void setAveLeadTime(Integer aveLeadTime) {
        this.aveLeadTime = aveLeadTime;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getDispensedUnits() {
        return dispensedUnits;
    }

    public void setDispensedUnits(Integer dispensedUnits) {
        this.dispensedUnits = dispensedUnits;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
    }

    public String getIsBulk() {
        return isBulk;
    }

    public void setIsBulk(String isBulk) {
        this.isBulk = DataBaseUtil.trim(isBulk);
    }

    public String getIsLabor() {
        return isLabor;
    }

    public void setIsLabor(String isLabor) {
        this.isLabor = DataBaseUtil.trim(isLabor);
    }

    public String getIsLotMaintained() {
        return isLotMaintained;
    }

    public void setIsLotMaintained(String isLotMaintained) {
        this.isLotMaintained = DataBaseUtil.trim(isLotMaintained);
    }

    public String getIsNoInventory() {
        return isNoInventory;
    }

    public void setIsNoInventory(String isNoInventory) {
        this.isNoInventory = DataBaseUtil.trim(isNoInventory);
    }

    public String getIsNotForSale() {
        return isNotForSale;
    }

    public void setIsNotForSale(String isNotForSale) {
        this.isNotForSale = DataBaseUtil.trim(isNotForSale);
    }

    public String getIsReorderAuto() {
        return isReorderAuto;
    }

    public void setIsReorderAuto(String isReorderAuto) {
        this.isReorderAuto = DataBaseUtil.trim(isReorderAuto);
    }

    public String getIsSerialMaintained() {
        return isSerialMaintained;
    }

    public void setIsSerialMaintained(String isSerialMaintained) {
        this.isSerialMaintained = DataBaseUtil.trim(isSerialMaintained);
    }

    public String getIsSubAssembly() {
        return isSubAssembly;
    }

    public void setIsSubAssembly(String isSubAssembly) {
        this.isSubAssembly = DataBaseUtil.trim(isSubAssembly);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getProductUri() {
        return productUri;
    }

    public void setProductUri(String productUri) {
        this.productUri = DataBaseUtil.trim(productUri);
    }

    public Integer getQuantityMaxLevel() {
        return quantityMaxLevel;
    }

    public void setQuantityMaxLevel(Integer quantityMaxLevel) {
        this.quantityMaxLevel = quantityMaxLevel;
    }

    public Integer getQuantityMinLevel() {
        return quantityMinLevel;
    }

    public void setQuantityMinLevel(Integer quantityMinLevel) {
        this.quantityMinLevel = quantityMinLevel;
    }

    public Integer getQuantityToReorder() {
        return quantityToReorder;
    }

    public void setQuantityToReorder(Integer quantityToReorder) {
        this.quantityToReorder = quantityToReorder;
    }

    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    public Integer getParentInventoryItemId() {
        return parentInventoryItemId;
    }

    public void setParentInventoryItemId(Integer parentInventoryItemId) {
        this.parentInventoryItemId = parentInventoryItemId;
    }

    public Integer getParentRatio() {
        return parentRatio;
    }

    public void setParentRatio(Integer parentRatio) {
        this.parentRatio = parentRatio;
    }

    public String getParentInventoryItem() {
        return parentInventoryItem;
    }

    public void setParentInventoryItem(String parentInventoryItem) {
        this.parentInventoryItem = DataBaseUtil.trim(parentInventoryItem);
    }

    public String getManufacturingText() {
        return manufacturingText;
    }

    public void setManufacturingText(String manufacturingText) {
        this.manufacturingText = DataBaseUtil.trim(manufacturingText);
    }
}
