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

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class extends the label DO and carries an additional scriptlet name
 * field. This additional fields is for read/display only and does not get
 * committed to the database. Note: isChanged will reflect any changes to
 * read/display fields.
 */

public class InventoryXUseViewDO extends InventoryXUseDO {

    private static final long serialVersionUID = 1L;

    protected String          storageLocationName, storageLocationUnitDescription,
                              storageLocationLocation, inventoryItemName, inventoryLocationLotNumber,
                              inventoryReceiptExternalReference;
    protected Datetime        inventoryLocationExpirationDate, inventoryReceiptReceivedDate;
    protected Double          inventoryReceiptUnitCost;
    protected Integer         storageLocationId,inventoryItemId, inventoryLocationQuantityOnhand, orderItemOrderId;  

    public InventoryXUseViewDO() {
    }

    public InventoryXUseViewDO(Integer id, Integer inventoryLocationId, Integer orderItemId,
                               Integer quantity, String inventoryLocationLotNumber,
                               Date inventoryLocationExpirationDate, Integer inventoryLocationQuantityOnhand,
                               Integer storageLocationId,String storageLocationName,
                               String storageLocationUnitDescription, String storageLocationLocation,
                               Integer inventoryItemId, String inventoryItemName,
                               Date inventoryReceiptReceivedDate, Double inventoryReceiptUnitCost,
                               String inventoryReceiptExternalReference, Integer orderItemOrderId) {
        super(id, inventoryLocationId, orderItemId, quantity);
        setInventoryLocationLotNumber(inventoryLocationLotNumber);
        setInventoryLocationExpirationDate(DataBaseUtil.toYD(inventoryLocationExpirationDate));
        setInventoryLocationQuantityOnhand(inventoryLocationQuantityOnhand);
        setStorageLocationId(storageLocationId);
        setStorageLocationName(storageLocationName);
        setStorageLocationUnitDescription(storageLocationUnitDescription);
        setStorageLocationLocation(storageLocationLocation);
        setInventoryItemId(inventoryItemId);
        setInventoryItemName(inventoryItemName);
        setInventoryReceiptReceivedDate(DataBaseUtil.toYM(inventoryReceiptReceivedDate));
        setInventoryReceiptUnitCost(inventoryReceiptUnitCost);
        setInventoryReceiptExternalReference(inventoryReceiptExternalReference);
        setOrderItemOrderId(orderItemOrderId);
    }

    public String getInventoryLocationLotNumber() {
        return inventoryLocationLotNumber;
    }

    public void setInventoryLocationLotNumber(String inventoryLocationLotNumber) {
        this.inventoryLocationLotNumber = DataBaseUtil.trim(inventoryLocationLotNumber);
    }

    public Datetime getInventoryLocationExpirationDate() {
        return inventoryLocationExpirationDate;
    }

    public void setInventoryLocationExpirationDate(Datetime inventoryLocationExpirationDate) {
        this.inventoryLocationExpirationDate = DataBaseUtil.toYD(inventoryLocationExpirationDate);
    }
    
    public Integer getInventoryLocationQuantityOnhand() {
        return inventoryLocationQuantityOnhand;
    }

    public void setInventoryLocationQuantityOnhand(Integer inventoryLocationQuantityOnhand) {
        this.inventoryLocationQuantityOnhand = inventoryLocationQuantityOnhand;
    }
    
    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
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

    public String getInventoryItemName() {
        return inventoryItemName;
    }
    
    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = DataBaseUtil.trim(inventoryItemName);
    }
    
    public Datetime getInventoryReceiptReceivedDate() {
        return inventoryReceiptReceivedDate;
    }

    public void setInventoryReceiptReceivedDate(Datetime receivedDate) {
        this.inventoryReceiptReceivedDate = DataBaseUtil.toYM(receivedDate);
    }
    
    public Double getInventoryReceiptUnitCost() {
        return inventoryReceiptUnitCost;
    }

    public void setInventoryReceiptUnitCost(Double unitCost) {
        this.inventoryReceiptUnitCost = unitCost;
    }
    
    public String getInventoryReceiptExternalReference() {
        return inventoryReceiptExternalReference;
    }

    public void setInventoryReceiptExternalReference(String externalReference) {
        this.inventoryReceiptExternalReference = DataBaseUtil.trim(externalReference);
    }

    public Integer getOrderItemOrderId() {
        return orderItemOrderId;
    }

    public void setOrderItemOrderId(Integer orderItemOrderId) {
        this.orderItemOrderId = orderItemOrderId;
    }
}
