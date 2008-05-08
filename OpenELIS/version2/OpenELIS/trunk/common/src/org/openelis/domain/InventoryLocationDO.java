package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

public class InventoryLocationDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer inventoryItem;
	protected String lotNumber;
	protected Integer storageLocationId;
	protected String storageLocation;
	protected Integer quantityOnHand;
	protected Date expirationDate;
	
    public InventoryLocationDO(){
        
    }
    
	public InventoryLocationDO(Integer id, Integer inventoryItem, String lotNumber, Integer storageLocationId, String storageLocation,
									Integer quantityOnHand, Date expirationDate){
		this.id = id;
		this.inventoryItem = inventoryItem;
		this.lotNumber = lotNumber;
		this.storageLocationId = storageLocationId;
		this.storageLocation = storageLocation;
		this.quantityOnHand = quantityOnHand;
		this.expirationDate = expirationDate;		
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
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
		this.lotNumber = lotNumber;
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
		this.storageLocation = storageLocation;
	}

	public Integer getStorageLocationId() {
		return storageLocationId;
	}

	public void setStorageLocationId(Integer storageLocationId) {
		this.storageLocationId = storageLocationId;
	}

}
