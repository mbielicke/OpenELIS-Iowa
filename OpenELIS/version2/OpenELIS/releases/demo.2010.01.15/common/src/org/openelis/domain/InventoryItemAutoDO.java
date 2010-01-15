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

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryItemAutoDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          name;
    protected String          store;
    protected Integer         locationId;
    protected String          location;
    protected Integer         quantityOnHand;
    protected String          description;
    protected String          dispensedUnits;
    protected String          lotNum;
    protected Datetime        expDate;
    protected String          isBulk;
    protected String          isLotMaintained;
    protected String          isSerialMaintained;
    protected Integer         parentInventoryItemId;
    protected Integer         parentRatio;

    public Integer getParentRatio() {
        return parentRatio;
    }

    public void setParentRatio(Integer parentRatio) {
        this.parentRatio = parentRatio;
    }

    public InventoryItemAutoDO() {

    }

    public InventoryItemAutoDO(Integer id, String name) {
        setId(id);
        setName(name);
    }

    public InventoryItemAutoDO(Integer id, String name, String store) {
        setId(id);
        setName(name);
        setStore(store);
    }

    public InventoryItemAutoDO(Integer id,
                               String name,
                               String store,
                               String description,
                               String dispensedUnits,
                               String isBulk,
                               String isSerialMaintained) {
        setId(id);
        setName(name);
        setStore(store);
        setDescription(description);
        setDispensedUnits(dispensedUnits);
        setIsBulk(isBulk);
        setIsSerialMaintained(isSerialMaintained);
    }

    public InventoryItemAutoDO(Integer id,
                               String name,
                               String store,
                               Integer locationId,
                               String childStorageLocName,
                               String childStorageLocLocation,
                               String parentStorageLocName,
                               String childStorageUnit,
                               String lotNum,
                               Date expDate,
                               Integer quantityOnHand) {
        setId(id);
        setName(name);
        setStore(store);
        setLocationId(locationId);
        setLocation(DataBaseUtil.formatStorageLocation(childStorageLocName, childStorageLocLocation, childStorageUnit, parentStorageLocName));
        setLotNum(lotNum);
        setExpDate(expDate);
        setQuantityOnHand(quantityOnHand);
    }

    public InventoryItemAutoDO(Integer id,
                               String name,
                               String description,
                               String store,
                               Integer locationId,
                               String childStorageLocName,
                               String childStorageLocLocation,
                               String parentStorageLocName,
                               String childStorageUnit,
                               String lotNum,
                               Date expDate,
                               Integer quantityOnHand,
                               String dispensedUnits,
                               String isBulk,
                               String isLotMaintained,
                               String isSerialMaintained,
                               Integer parentRatio,
                               Integer parentInventoryItemId) {
        setId(id);
        setName(name);
        setDescription(description);
        setStore(store);
        setLocationId(locationId);
        setLocation(DataBaseUtil.formatStorageLocation(childStorageLocName, childStorageLocLocation, childStorageUnit, parentStorageLocName));
        setLotNum(lotNum);
        setExpDate(expDate);
        setQuantityOnHand(quantityOnHand);
        setDispensedUnits(dispensedUnits);
        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);

        setParentInventoryItemId(parentInventoryItemId);
        setParentRatio(parentRatio);
    }

    public InventoryItemAutoDO(Integer id,
                               String name,
                               String store,
                               String description,
                               String dispensedUnits,
                               String isBulk,
                               String isLotMaintained,
                               String isSerialMaintained,
                               Integer parentRatio,
                               Integer parentInventoryItemId) {
        setId(id);
        setName(name);
        setStore(store);
        setDescription(description);
        setDispensedUnits(dispensedUnits);

        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
        setParentInventoryItemId(parentInventoryItemId);
        setParentRatio(parentRatio);
    }

    public InventoryItemAutoDO(Integer id,
                               String name,
                               String store,
                               String description,
                               String dispensedUnits,
                               String isBulk,
                               String isLotMaintained,
                               String isSerialMaintained) {
        setId(id);
        setName(name);
        setStore(store);
        setDescription(description);
        setDispensedUnits(dispensedUnits);

        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = DataBaseUtil.trim(store);
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public String getDispensedUnits() {
        return dispensedUnits;
    }

    public void setDispensedUnits(String dispensedUnits) {
        this.dispensedUnits = DataBaseUtil.trim(dispensedUnits);
    }

    public Datetime getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = new Datetime(Datetime.YEAR, Datetime.DAY, expDate);
    }

    public String getLotNum() {
        return lotNum;
    }

    public void setLotNum(String lotNum) {
        this.lotNum = lotNum;
    }

    public String getIsBulk() {
        return isBulk;
    }

    public void setIsBulk(String isBulk) {
        this.isBulk = DataBaseUtil.trim(isBulk);
    }

    public String getIsLotMaintained() {
        return isLotMaintained;
    }

    public void setIsLotMaintained(String isLotMaintained) {
        this.isLotMaintained = DataBaseUtil.trim(isLotMaintained);
    }

    public String getIsSerialMaintained() {
        return isSerialMaintained;
    }

    public void setIsSerialMaintained(String isSerialMaintained) {
        this.isSerialMaintained = DataBaseUtil.trim(isSerialMaintained);
    }

    public Integer getParentInventoryItemId() {
        return parentInventoryItemId;
    }

    public void setParentInventoryItemId(Integer parentInventoryItemId) {
        this.parentInventoryItemId = parentInventoryItemId;
    }
}
