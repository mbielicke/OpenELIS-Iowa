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

public class InventoryAdjLocationAutoDO implements RPC {

    private static final long serialVersionUID = 1L;
    protected Integer         inventoryLocationId;
    protected Integer         inventoryItemId;
    protected String          inventoryItem;
    protected String          store;
    protected String          storageLocation;
    protected Integer         quantityOnHand;

    public InventoryAdjLocationAutoDO() {

    }

    public InventoryAdjLocationAutoDO(Integer inventoryLocationId,
                                      Integer inventoryItemId,
                                      String inventoryItem,
                                      String store,
                                      String storageLocationName,
                                      String storageLocation,
                                      String storageUnitDescription,
                                      Integer quantityOnHand) {

        setInventoryLocationId(inventoryLocationId);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setStore(store);
        setStorageLocation(DataBaseUtil.formatStorageLocation(storageLocationName, storageLocation, storageUnitDescription));
        setQuantityOnHand(quantityOnHand);
    }

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = DataBaseUtil.trim(inventoryItem);
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        this.inventoryLocationId = inventoryLocationId;
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

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = DataBaseUtil.trim(store);
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

}
