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

public class OrderItemDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer order;
    protected Integer inventoryItemId;
    protected Integer quantity;
    protected String inventoryItem;
    protected String store;
    protected Integer locationId;
    protected String location;
    protected Integer transactionId;
    protected String catalogNumber;
    protected Double unitCost;
    protected Integer quantityOnHand;
    protected String lotNumber;
    
    protected Boolean delete = false;
    
    public OrderItemDO(){
        
    }

    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, Integer quantity){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setQuantity(quantity);
     }
    
    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, String inventoryItem, Integer quantity, String store, Integer locationId,
                       String childStorageLocName, String childStorageLocLocation, String parentStorageLocName, String childStorageUnit, Integer transactionId, 
                       Integer quantityOnHand, String lotNumber){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setQuantity(quantity);
        setStore(store);
        setLocationId(locationId);
        setTransactionId(transactionId);
        setQuantityOnHand(quantityOnHand);
        setLotNumber(lotNumber);
        
        //build the storage location string
        String storageLocation = "";
        if(parentStorageLocName != null)
            storageLocation += parentStorageLocName.trim()+", "+childStorageUnit.trim()+" "+childStorageLocLocation.trim();
        else
            storageLocation += childStorageUnit.trim()+" "+childStorageLocLocation.trim();
        
        setLocation(storageLocation);
     }
    
    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, String inventoryItem, Integer quantity, String store, String catalogNumber, Double unitCost){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setQuantity(quantity);
        setStore(store);
        setCatalogNumber(catalogNumber);
        setUnitCost(unitCost);
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = DataBaseUtil.trim(store);
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = DataBaseUtil.trim(catalogNumber);
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
    }
}
