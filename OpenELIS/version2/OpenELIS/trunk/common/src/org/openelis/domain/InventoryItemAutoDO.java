/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
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
