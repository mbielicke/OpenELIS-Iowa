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

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class InventoryAdjustmentChildDO implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Integer id;  //<--this is the TRANS_ADJUSTMENT_LOCATION id
    protected Integer adjustmentId;
    protected Integer locationId;
    protected Integer inventoryItemId;
    protected String inventoryItem;
    protected String storageLocation;
    protected Integer quantityOnHand;
    protected Integer physicalCount;
    protected Integer adjustedQuantity;
    protected Boolean delete = false;

    public InventoryAdjustmentChildDO(){
        
    }
    
    public InventoryAdjustmentChildDO(Integer locationId, Integer inventoryItemId, String inventoryItem,
                                      String storageLocation, Integer quantityOnHand, Integer physicalCount, 
                                      Integer adjustedQuantity){
        
        setLocationId(locationId);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setStorageLocation(storageLocation);
        setQuantityOnHand(quantityOnHand);
        setPhysicalCount(physicalCount);
        setAdjustedQuantity(adjustedQuantity);
    }

    public InventoryAdjustmentChildDO(Integer id, Integer locationId, Integer inventoryItemId, String inventoryItem,
                                      String storageLocName, String storageUnitDescription, String storageLocLocation, 
                                      Integer physicalCount, Integer adjustedQuantity){

        setId(id);
        setLocationId(locationId);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setStorageLocation(storageLocName.trim()+", "+storageUnitDescription.trim()+" "+storageLocLocation.trim());
        setPhysicalCount(physicalCount);
        setAdjustedQuantity(adjustedQuantity);
    }

    public Integer getAdjustedQuantity() {
        return adjustedQuantity;
    }

    public void setAdjustedQuantity(Integer adjustedQuantity) {
        this.adjustedQuantity = adjustedQuantity;
    }

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = DataBaseUtil.trim(inventoryItem);
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getPhysicalCount() {
        return physicalCount;
    }

    public void setPhysicalCount(Integer physicalCount) {
        this.physicalCount = physicalCount;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = DataBaseUtil.trim(storageLocation);
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Integer adjustmentId) {
        this.adjustmentId = adjustmentId;
    }
}
