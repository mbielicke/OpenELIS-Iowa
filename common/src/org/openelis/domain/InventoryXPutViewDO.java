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
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryXPutViewDO extends InventoryXPutDO {

    private static final long         serialVersionUID = 1L;

    protected InventoryLocationViewDO inventoryLocation;
    
    protected Integer                 inventoryLocationInventoryItemId,
                                      inventoryLocationStorageLocationId,
                                      inventoryLocationQuantityOnhand,
                                      inventoryLocationInventoryItemStoreId;
    protected Double                  inventoryReceiptUnitCost;
    protected String                  inventoryLocationLotNumber, inventoryItemName,
                                      inventoryLocationStorageLocationName,
                                      inventoryLocationStorageLocationUnitDescription,
                                      inventoryLocationStorageLocationLocation,
                                      inventoryReceiptExternalReference;
    protected Datetime                inventoryLocationExpirationDate,
                                      inventoryReceiptReceivedDate;
    
    public InventoryXPutViewDO() {
        inventoryLocation = new InventoryLocationViewDO();
    }
    
    public InventoryXPutViewDO(Integer id, Integer inventoryReceiptId,
                               Integer inventoryLocationId, Integer quantity,
                               Integer inventoryItemId, String lotNumber,
                               Integer storageLocationId, Integer quantityOnhand,
                               Date expirationDate, String inventoryItemName,
                               Integer inventoryItemStoreId,
                               String storageLocationName,
                               String storageLocationUnitDescription,
                               String storageLocationLocation,Date receivedDate,
                               Double unitCost, String externalReference) {
        super(id, inventoryReceiptId, inventoryLocationId, quantity);
        inventoryLocation = new InventoryLocationViewDO(inventoryLocationId,inventoryItemId,
                                                        lotNumber, storageLocationId,
                                                        quantityOnhand, expirationDate,
                                                        inventoryItemName, inventoryItemStoreId,
                                                        storageLocationName,
                                                        storageLocationUnitDescription,
                                                        storageLocationLocation);
        setInventoryReceiptReceivedDate(DataBaseUtil.toYD(receivedDate));
        setInventoryReceiptUnitCost(unitCost);
        setInventoryReceiptExternalReference(externalReference);
        
    }

    public InventoryLocationViewDO getInventoryLocation() {
        return inventoryLocation;
    }

    public Double getInventoryReceiptUnitCost() {
        return inventoryReceiptUnitCost;
    }

    public void setInventoryReceiptUnitCost(Double unitCost) {
        this.inventoryReceiptUnitCost = unitCost;
    }

    public String getExternalReference() {
        return inventoryReceiptExternalReference;
    }

    public void setInventoryReceiptExternalReference(String externalReference) {
        this.inventoryReceiptExternalReference = externalReference;
    }

    public Datetime getInventoryReceiptReceivedDate() {
        return inventoryReceiptReceivedDate;
    }

    public void setInventoryReceiptReceivedDate(Datetime receivedDate) {
        this.inventoryReceiptReceivedDate = receivedDate;
    }
    

}
