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

/**
 * The class extends inventory location DO and carries several commonly used
 * fields such as inventory item name and location name. The additional fields
 * are for read/display only and do not get committed to the database. Note:
 * isChanged will reflect any changes to read/display fields.
 */

public class InventoryLocationViewDO extends InventoryLocationDO {

    private static final long serialVersionUID = 1L;

    protected String          inventoryItemName, storageLocationName,
                              storageLocationUnitDescription, storageLocationLocation;
    protected Integer         inventoryItemStoreId;               

    public InventoryLocationViewDO() {
    }

    public InventoryLocationViewDO(Integer id, Integer inventoryItemId, String lotNumber,
                                   Integer storageLocationId, Integer quantityOnhand,
                                   Date expirationDate, String inventoryItemName,
                                   Integer inventoryItemStoreId,
                                   String storageLocationName,
                                   String storageLocationUnitDescription,
                                   String storageLocationLocation) {
        super(id, inventoryItemId, lotNumber, storageLocationId, quantityOnhand, expirationDate);
        setInventoryItemName(inventoryItemName);
        setInventoryItemStoreId(inventoryItemStoreId);
        setStorageLocationName(storageLocationName);
        setStorageLocationUnitDescription(storageLocationUnitDescription);
        setStorageLocationLocation(storageLocationLocation);
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = DataBaseUtil.trim(inventoryItemName);
    }
    
    public Integer getInventoryItemStoreId() {
        return inventoryItemStoreId;
    }

    public void setInventoryItemStoreId(Integer inventoryItemStoreId) {
        this.inventoryItemStoreId = inventoryItemStoreId;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = DataBaseUtil.trim(storageLocationName);
    }

    public String getStorageLocationUnitDescription() {
        return storageLocationUnitDescription;
    }

    public void setStorageLocationUnitDescription(String storageLocationUnitDescription) {
        this.storageLocationUnitDescription = DataBaseUtil.trim(storageLocationUnitDescription);
    }

    public String getStorageLocationLocation() {
        return storageLocationLocation;
    }

    public void setStorageLocationLocation(String storageLocationLocation) {
        this.storageLocationLocation = DataBaseUtil.trim(storageLocationLocation);
    }
}