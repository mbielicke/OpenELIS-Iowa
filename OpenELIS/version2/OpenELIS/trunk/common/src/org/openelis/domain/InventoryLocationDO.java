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
