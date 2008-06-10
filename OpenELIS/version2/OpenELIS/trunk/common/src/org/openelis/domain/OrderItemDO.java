package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class OrderItemDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer order;
    protected Integer inventoryItemId;
    protected Integer quantityRequested;
    protected String inventoryItem;
    protected String store;
    protected Integer locationId;
    protected String location;
    protected Integer transactionId;
    
    protected Boolean delete = false;
    
    public OrderItemDO(){
        
    }
    
    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, Integer quantityRequested){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setQuantityRequested(quantityRequested);
     }
    
    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, String inventoryItem, Integer quantityRequested, String store, Integer locationId,
                       String childStorageLocName, String childStorageLocLocation, String parentStorageLocName, String childStorageUnit, Integer transactionId){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setQuantityRequested(quantityRequested);
        setStore(store);
        setLocationId(locationId);
        setTransactionId(transactionId);
        
        //build the storage location string
        String storageLocation = "";
        if(parentStorageLocName != null)
            storageLocation += parentStorageLocName.trim()+", "+childStorageUnit.trim()+" "+childStorageLocLocation.trim();
        else
            storageLocation += childStorageLocName.trim(); 
        
        setLocation(storageLocation);
     }
    
    public OrderItemDO(Integer id, Integer order, Integer inventoryItemId, String inventoryItem, Integer quantityRequested, String store){
        setId(id);
        setOrder(order);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setQuantityRequested(quantityRequested);
        setStore(store);
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

    public Integer getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
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
}
