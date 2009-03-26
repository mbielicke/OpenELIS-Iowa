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
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class InventoryReceiptDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer orderNumber;
    protected Integer orderItemId;
    protected Integer inventoryItemId;
    protected String inventoryItem;
    protected Integer organizationId;
    protected String organization;
    protected Datetime receivedDate;
    protected Integer quantityReceived;
    protected Double unitCost;
    protected String qcReference;
    protected String externalReference;
    protected String upc;
    protected Integer fromInventoryItemId;
    protected String fromInventoryItem;
    protected Integer fromStorageLocationId;
    protected String fromStorageLocation;
    protected String fromItemDesc;
    protected Integer fromQtyOnHand;
    protected Integer toParentRatio;
    protected AddressDO orgAddress = new AddressDO();
    protected String itemDesc;
    protected String itemStore;
    protected String itemDispensedUnits;
    protected Integer itemQtyRequested;
    protected Integer storageLocationId;
    protected String storageLocation;
    protected String lotNumber;
    protected Datetime expDate;
    protected boolean addToExisting;
    protected String isLotMaintained;
    protected String isSerialMaintained;
    protected String isBulk;
    protected Integer transLocationOrderId;
    protected Integer inventoryReceiptOrderItemId;
    
    protected Boolean delete = false;

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public InventoryReceiptDO(){
        
    }
    
    public InventoryReceiptDO(Integer id, Integer inventoryItemId, String inventoryItem, Integer organizationId, Date receivedDate,
                              Integer quantityReceived, Double unitCost, String qcReference, String externalReference,
                              String upc){
        setId(id);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrganizationId(organizationId);
        setReceivedDate(receivedDate);
        setQuantityReceived(quantityReceived);
        setUnitCost(unitCost);
        setQcReference(qcReference);
        setExternalReference(externalReference);   
        setUpc(upc);
    }
    
    public InventoryReceiptDO(Integer id, Integer orderNumber, Integer inventoryItemId, String inventoryItem, Integer organizationId,
                              String organization, Date receivedDate, Integer quantityReceived, Double unitCost, String qcReference, 
                              String externalReference, String upc){
        setId(id);
        setOrderNumber(orderNumber);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setReceivedDate(receivedDate);
        setQuantityReceived(quantityReceived);
        setUnitCost(unitCost);
        setQcReference(qcReference);
        setExternalReference(externalReference);   
        setUpc(upc);
    }

    /*used for query*/
    public InventoryReceiptDO(Integer id, Integer orderNumber, Date dateReceived, String upc, Integer inventoryItemId, String inventoryItem, Integer orderItemId, Integer organizationId,
                              String organization, Integer qty, Double cost, String qc, String extReference, String streetAddress, String multUnit, String city, String state, String zipCode,
                              String itemDesc, String itemStore, String itemDispensedUnits, Integer quantityRequested, String isBulk, String isLotMaintained, String isSerialMaintained, 
                              Integer storageLocationId, String storageLocName, String storageLocLocation, String storageUnitDescription, String lotNumber, Date expDate) {
        setId(id);
        setOrderNumber(orderNumber);
        setReceivedDate(dateReceived);
        setUpc(upc);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrderItemId(orderItemId);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setQuantityReceived(qty);
        setUnitCost(cost);
        setQcReference(qc);
        setExternalReference(extReference);
        
        //org address values
        orgAddress.setStreetAddress(streetAddress);
        orgAddress.setMultipleUnit(multUnit);
        orgAddress.setCity(city);
        orgAddress.setState(state);
        orgAddress.setZipCode(zipCode);
        
        //inv item values
        setItemDesc(itemDesc);
        setItemStore(itemStore);
        setItemDispensedUnits(itemDispensedUnits);
        setItemQtyRequested(quantityRequested);
        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
        
        setStorageLocationId(storageLocationId);
        setStorageLocation(storageLocName.trim()+", "+storageUnitDescription.trim()+" "+storageLocLocation.trim());
        setExpDate(expDate);
        setLotNumber(lotNumber);
    }
    
    /*used for transfer query*/
    public InventoryReceiptDO(Integer id, Integer orderNumber, Date dateReceived, String upc, Integer inventoryItemId, String inventoryItem, Integer orderItemId, Integer organizationId,
                              Integer qty, Double cost, String qc, String extReference, String itemDesc, String itemStore, String itemDispensedUnits, Integer quantityRequested, 
                              String isBulk, String isLotMaintained, String isSerialMaintained, Integer storageLocationId, String storageLocName, String storageLocLocation, 
                              String storageUnitDescription, String lotNumber, Date expDate, Integer fromItemId, String fromItem, String fromItemDesc, 
                              Integer fromStorageLocationId, String fromStorageLocName, String fromStorageUnitDescription, String fromStorageLocLocation, Integer fromQtyOnHand) {
        setId(id);
        setOrderNumber(orderNumber);
        setReceivedDate(dateReceived);
        setUpc(upc);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrderItemId(orderItemId);
        setOrganizationId(organizationId);
        setQuantityReceived(qty);
        setUnitCost(cost);
        setQcReference(qc);
        setExternalReference(extReference);
        
        //inv item values
        setItemDesc(itemDesc);
        setItemStore(itemStore);
        setItemDispensedUnits(itemDispensedUnits);
        setItemQtyRequested(quantityRequested);
        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
        
        setStorageLocationId(storageLocationId);
        setStorageLocation(storageLocName.trim()+", "+storageUnitDescription.trim()+" "+storageLocLocation.trim());
        setExpDate(expDate);
        setLotNumber(lotNumber);
        setFromInventoryItemId(fromItemId);
        setFromInventoryItem(fromItem);
        setFromItemDesc(fromItemDesc);
        setFromStorageLocationId(fromStorageLocationId);
        setFromStorageLocation(fromStorageLocName.trim()+", "+fromStorageUnitDescription.trim()+" "+fromStorageLocLocation.trim());
        setFromQtyOnHand(fromQtyOnHand);
    }
    
    /*used for order number entry*/
    public InventoryReceiptDO(Integer orderNumber, Integer inventoryItemId, String inventoryItem, Integer orderItemId, Double unitCost, Integer organizationId,
                              String organization, Integer itemQtyRequested, String streetAddress, String multUnit, String city, String state, String zipCode,
                              String itemDesc, String itemStore, String itemDispensedUnits, String isBulk, String isLotMaintained, String isSerialMaintained) {
        setOrderNumber(orderNumber);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrderItemId(orderItemId);
        setUnitCost(unitCost);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setItemQtyRequested(itemQtyRequested);
        
        //org address values
        orgAddress.setStreetAddress(streetAddress);
        orgAddress.setMultipleUnit(multUnit);
        orgAddress.setCity(city);
        orgAddress.setState(state);
        orgAddress.setZipCode(zipCode);
        
        //inv item values
        setItemDesc(itemDesc);
        setItemStore(itemStore);  
        setItemDispensedUnits(itemDispensedUnits);
        setIsBulk(isBulk);
        setIsLotMaintained(isLotMaintained);
        setIsSerialMaintained(isSerialMaintained);
    }
    
    public String getExternalReference() {
        return externalReference;
    }
    public void setExternalReference(String externalReference) {
        this.externalReference = DataBaseUtil.trim(externalReference);
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getInventoryItemId() {
        return inventoryItemId;
    }
    public void setInventoryItemId(Integer inventoryItem) {
        this.inventoryItemId = inventoryItem;
    }
    public Integer getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(Integer organization) {
        this.organizationId = organization;
    }
    public String getQcReference() {
        return qcReference;
    }
    public void setQcReference(String qcReference) {
        this.qcReference = DataBaseUtil.trim(qcReference);
    }
    public Integer getQuantityReceived() {
        return quantityReceived;
    }
    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }
    public Datetime getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = new Datetime(Datetime.YEAR,Datetime.DAY,receivedDate);
    }
    public Double getUnitCost() {
        return unitCost;
    }
    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
    public String getUpc() {
        return upc;
    }
    public void setUpc(String upc) {
        this.upc = DataBaseUtil.trim(upc);
    }

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = DataBaseUtil.trim(inventoryItem);
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = DataBaseUtil.trim(organization);
    }
    
    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = DataBaseUtil.trim(itemDesc);
    }

    public String getItemStore() {
        return itemStore;
    }

    public void setItemStore(String itemStore) {
        this.itemStore = DataBaseUtil.trim(itemStore);
    }

    public AddressDO getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(AddressDO orgAddress) {
        this.orgAddress = orgAddress;
    }

    public Datetime getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate =  new Datetime(Datetime.YEAR,Datetime.DAY,expDate);
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public boolean isAddToExisting() {
        return addToExisting;
    }

    public void setAddToExisting(boolean addToExisting) {
        this.addToExisting = addToExisting;
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

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = DataBaseUtil.trim(storageLocation);
    }

    public Integer getItemQtyRequested() {
        return itemQtyRequested;
    }

    public void setItemQtyRequested(Integer itemQtyRequested) {
        this.itemQtyRequested = itemQtyRequested;
    }

    public String getItemDispensedUnits() {
        return itemDispensedUnits;
    }

    public void setItemDispensedUnits(String itemDispensedUnits) {
        this.itemDispensedUnits = DataBaseUtil.trim(itemDispensedUnits);
    }

    public Integer getToParentRatio() {
        return toParentRatio;
    }

    public void setToParentRatio(Integer toParentRatio) {
        this.toParentRatio = toParentRatio;
    }

    public Integer getTransLocationOrderId() {
        return transLocationOrderId;
    }

    public void setTransLocationOrderId(Integer transLocationOrderId) {
        this.transLocationOrderId = transLocationOrderId;
    }

    public String getFromInventoryItem() {
        return fromInventoryItem;
    }

    public void setFromInventoryItem(String fromInventoryItem) {
        this.fromInventoryItem = DataBaseUtil.trim(fromInventoryItem);
    }

    public Integer getFromInventoryItemId() {
        return fromInventoryItemId;
    }

    public void setFromInventoryItemId(Integer fromInventoryItemId) {
        this.fromInventoryItemId = fromInventoryItemId;
    }

    public String getFromItemDesc() {
        return fromItemDesc;
    }

    public void setFromItemDesc(String fromItemDesc) {
        this.fromItemDesc = DataBaseUtil.trim(fromItemDesc);
    }

    public String getFromStorageLocation() {
        return fromStorageLocation;
    }

    public void setFromStorageLocation(String fromStorageLocation) {
        this.fromStorageLocation = fromStorageLocation;
    }

    public Integer getFromStorageLocationId() {
        return fromStorageLocationId;
    }

    public void setFromStorageLocationId(Integer fromStorageLocationId) {
        this.fromStorageLocationId = fromStorageLocationId;
    }

    public Integer getFromQtyOnHand() {
        return fromQtyOnHand;
    }

    public void setFromQtyOnHand(Integer fromQtyOnHand) {
        this.fromQtyOnHand = fromQtyOnHand;
    }

    public Integer getInventoryReceiptOrderItemId() {
        return inventoryReceiptOrderItemId;
    }

    public void setInventoryReceiptOrderItemId(Integer inventoryReceiptOrderItemId) {
        this.inventoryReceiptOrderItemId = inventoryReceiptOrderItemId;
    }
}
