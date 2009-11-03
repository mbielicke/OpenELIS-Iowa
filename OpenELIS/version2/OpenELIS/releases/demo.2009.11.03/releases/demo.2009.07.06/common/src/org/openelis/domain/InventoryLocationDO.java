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

import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryLocationDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer inventoryItemId;
    protected String inventoryItem;
	protected String lotNumber;
	//protected Integer storageLocationId;
	protected String storageLocation;
	protected Integer quantityOnHand;
	protected Datetime expirationDate;
	
    public InventoryLocationDO(){
        
    }
    
	public InventoryLocationDO(Integer id, Integer inventoryItemId, String inventoryItem, String lotNumber, String storageLocation,
									Integer quantityOnHand, Date expirationDate){
		setId(id);
		setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
		setLotNumber(lotNumber);
		setStorageLocation(storageLocation);
		setQuantityOnHand(quantityOnHand);
		setExpirationDate(new Datetime(Datetime.YEAR,Datetime.DAY,expirationDate));		
	}

    public InventoryLocationDO(Integer id, Integer inventoryItemId, String inventoryItem, String lotNumber, String childStorageLocName, String childStorageLocLocation,  
                               String parentStorageLocName, String childStorageUnit, Integer quantityOnHand, Date expirationDate){
    setId(id);
    setInventoryItemId(inventoryItemId);
    setInventoryItem(inventoryItem);
    setLotNumber(lotNumber);
    setQuantityOnHand(quantityOnHand);
    setExpirationDate(new Datetime(Datetime.YEAR,Datetime.DAY,expirationDate));
    
    //build the storage location string
    String storageLocation = "";
    if(parentStorageLocName != null)
        storageLocation += parentStorageLocName.trim()+", "+childStorageUnit.trim()+" "+childStorageLocLocation.trim();
    else
        storageLocation += childStorageUnit.trim()+" "+childStorageLocLocation.trim();
    
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

	public Integer getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Integer inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
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

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = DataBaseUtil.trim(inventoryItem);
    }

	/*public Integer getChildStorageLocationId() {
		return childStorageLocationId;
	}

	public void setChildStorageLocationId(Integer childStorageLocationId) {
		this.childStorageLocationId = childStorageLocationId;
	}*/
}
