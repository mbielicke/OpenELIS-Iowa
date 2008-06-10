package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class InventoryItemAutoDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String store;
    protected Integer locationId;
    protected String location;
    protected Integer quantityOnHand;
    
    public InventoryItemAutoDO(){
        
    }
    
    public InventoryItemAutoDO(Integer id, String name, String store){
        setId(id);
        setName(name);
        setStore(store);
    }
    
    public InventoryItemAutoDO(Integer id, String name, String store, Integer locationId, String childStorageLocName, String childStorageLocLocation,  
                               String parentStorageLocName, String childStorageUnit, Integer quantityOnHand){
        setId(id);
        setName(name);
        setStore(store);
        setLocationId(locationId);
        
        //build the storage location string
        String storageLocation = "";
        if(parentStorageLocName != null)
            storageLocation += parentStorageLocName.trim()+", "+childStorageUnit.trim()+" "+childStorageLocLocation.trim();
        else
            storageLocation += childStorageLocName.trim();    
        
        setLocation(storageLocation);
        
        setQuantityOnHand(quantityOnHand);
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

}
