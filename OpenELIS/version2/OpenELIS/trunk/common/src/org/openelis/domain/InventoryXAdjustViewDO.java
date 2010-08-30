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

import org.openelis.gwt.common.DataBaseUtil;

public class InventoryXAdjustViewDO extends InventoryXAdjustDO {
    
    private static final long serialVersionUID = 1L;

    protected String          inventoryLocationLotNumber, inventoryLocationStorageLocationName,
                              inventoryLocationStorageLocationUnitDescription,
                              inventoryLocationStorageLocationLocation;
    
    protected Integer         inventoryLocationQuantityOnhand, inventoryLocationInventoryItemId;
    protected String          inventoryLocationInventoryItemName;

    public InventoryXAdjustViewDO() {
    }
    
    public InventoryXAdjustViewDO(Integer id, Integer inventoryAdjustmentId,
                                  Integer inventoryLocationId, Integer quantity,
                                  Integer physicalCount,String inventoryLocationLotNumber,
                                  Integer inventoryLocationQuantityOnhand,
                                  String inventoryLocationStorageLocationName,
                                  String inventoryLocationStorageLocationUnitDescription,
                                  String inventoryLocationStorageLocationLocation,
                                  Integer inventoryLocationInventoryItemId,
                                  String inventoryLocationInventoryItemName) {        
        super(id, inventoryAdjustmentId, inventoryLocationId, quantity, physicalCount);
        setInventoryLocationLotNumber(inventoryLocationLotNumber);
        setInventoryLocationQuantityOnhand(inventoryLocationQuantityOnhand);
        setInventoryLocationStorageLocationName(inventoryLocationStorageLocationName);
        setInventoryLocationStorageLocationUnitDescription(inventoryLocationStorageLocationUnitDescription);
        setInventoryLocationStorageLocationLocation(inventoryLocationStorageLocationLocation);  
        setInventoryLocationInventoryItemId(inventoryLocationInventoryItemId);
        setInventoryLocationInventoryItemName(inventoryLocationInventoryItemName);
    }
    
    public String getInventoryLocationLotNumber() {
        return inventoryLocationLotNumber;
    }

    public void setInventoryLocationLotNumber(String inventoryLocationLotNumber) {
        this.inventoryLocationLotNumber = DataBaseUtil.trim(inventoryLocationLotNumber);
    }

    public String getInventoryLocationStorageLocationName() {
        return inventoryLocationStorageLocationName;
    }

    public void setInventoryLocationStorageLocationName(String inventoryLocationStorageLocationName) {
        this.inventoryLocationStorageLocationName = DataBaseUtil.trim(inventoryLocationStorageLocationName);
    }

    public String getInventoryLocationStorageLocationUnitDescription() {
        return inventoryLocationStorageLocationUnitDescription;
    }

    public void setInventoryLocationStorageLocationUnitDescription(String inventoryLocationStorageLocationUnitDescription) {
        this.inventoryLocationStorageLocationUnitDescription = DataBaseUtil.trim(inventoryLocationStorageLocationUnitDescription);
    }

    public String getInventoryLocationStorageLocationLocation() {
        return inventoryLocationStorageLocationLocation;
    }

    public void setInventoryLocationStorageLocationLocation(String inventoryLocationStorageLocationLocation) {
        this.inventoryLocationStorageLocationLocation = DataBaseUtil.trim(inventoryLocationStorageLocationLocation);
    }

    public Integer getInventoryLocationQuantityOnhand() {
        return inventoryLocationQuantityOnhand;
    }

    public void setInventoryLocationQuantityOnhand(Integer inventoryLocationQuantityOnhand) {
        this.inventoryLocationQuantityOnhand = inventoryLocationQuantityOnhand;
    }

    public Integer getInventoryLocationInventoryItemId() {
        return inventoryLocationInventoryItemId;
    }

    public void setInventoryLocationInventoryItemId(Integer inventoryLocationInventoryItemId) {
        this.inventoryLocationInventoryItemId = inventoryLocationInventoryItemId;
    }

    public String getInventoryLocationInventoryItemName() {
        return inventoryLocationInventoryItemName;
    }

    public void setInventoryLocationInventoryItemName(String inventoryLocationInventoryItemName) {
        this.inventoryLocationInventoryItemName = DataBaseUtil.trim(inventoryLocationInventoryItemName);
    }
    
}
