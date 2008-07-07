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
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class InventoryLocationDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer inventoryItem;
	protected String lotNumber;
	//protected Integer storageLocationId;
	protected String storageLocation;
	protected Integer quantityOnHand;
	protected Datetime expirationDate;
	
    public InventoryLocationDO(){
        
    }
    
	public InventoryLocationDO(Integer id, Integer inventoryItem, String lotNumber, String storageLocation,
									Integer quantityOnHand, Date expirationDate){
		setId(id);
		setInventoryItem(inventoryItem);
		setLotNumber(lotNumber);
		setStorageLocation(storageLocation);
		setQuantityOnHand(quantityOnHand);
		setExpirationDate(new Datetime(Datetime.YEAR,Datetime.DAY,expirationDate));		
	}

    public InventoryLocationDO(Integer id, Integer inventoryItem, String lotNumber, String childStorageLocName, String childStorageLocLocation,  
                               String parentStorageLocName, String childStorageUnit, Integer quantityOnHand, Date expirationDate){
    setId(id);
    setInventoryItem(inventoryItem);
    setLotNumber(lotNumber);
    setQuantityOnHand(quantityOnHand);
    setExpirationDate(new Datetime(Datetime.YEAR,Datetime.DAY,expirationDate));
    
    //build the storage location string
    String storageLocation = "";
    if(parentStorageLocName != null)
        storageLocation += parentStorageLocName.trim()+", "+childStorageUnit.trim()+" "+childStorageLocLocation.trim();
    else
        storageLocation += childStorageLocName.trim();    
    
    setStorageLocation(storageLocation);
}
    
	public Datetime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Datetime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(Integer inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = DataBaseUtil.trim(lotNumber);
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

	/*public Integer getChildStorageLocationId() {
		return childStorageLocationId;
	}

	public void setChildStorageLocationId(Integer childStorageLocationId) {
		this.childStorageLocationId = childStorageLocationId;
	}*/
}
