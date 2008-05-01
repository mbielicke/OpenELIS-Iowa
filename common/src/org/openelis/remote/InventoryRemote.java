package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.InventoryItemDO;

@Remote
public interface InventoryRemote {
	//method to return parent inventory item
	public InventoryItemDO getInventoryItem(Integer inventoryItemId);
	
	//method to unlock entity and return parent inventory item
	public InventoryItemDO getInventoryItemAndUnlock(Integer inventoryItemId);
	
	//method to lock entity and return parent inventory item
	public InventoryItemDO getInventoryItemAndLock(Integer inventoryItemId) throws Exception;
	
	//commit a change to inventory, or insert a new inventory
	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, List locations) throws Exception;
	
	//method to return just notes
	public List getInventoryNotes(Integer inventoryItemId);
	
	//method to return just components
	public List getInventoryComponents(Integer inventoryItemId);
	
	//method to return just locations
	public List getInventoryLocations(Integer inventoryItemId);
	
	//method to query for inventories
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //auto complete lookup
	 //public List autoCompleteLookupByName(String orgName, int maxResults);
	 
	 //auto complete lookup
	 //public List autoCompleteLookupById(Integer id);
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(InventoryItemDO inventoryItemDO, List components, List locations);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(InventoryItemDO inventoryItemDO, List components, List locations);
}
