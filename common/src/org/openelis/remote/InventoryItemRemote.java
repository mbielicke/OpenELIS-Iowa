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
package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface InventoryItemRemote {
    public InventoryItemViewDO fetchById(Integer id) throws Exception;

    public ArrayList<InventoryItemDO> fetchActiveByName(String name, int max) throws Exception;

    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(String name, Integer storeId, int max) throws Exception;
    
    public ArrayList<InventoryItemDO> fetchActiveByNameStoreAndParentInventoryItem(String name, Integer parentInventoryItemId, int max) throws Exception;

    public ArrayList<IdNameStoreVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception;
}
/*
    //method to return parent inventory item
	public InventoryItemDO getInventoryItem(Integer inventoryItemId);
	
	//method to unlock entity and return parent inventory item
	public InventoryItemDO getInventoryItemAndUnlock(Integer inventoryItemId, String session);
	
	//method to lock entity and return parent inventory item
	public InventoryItemDO getInventoryItemAndLock(Integer inventoryItemId, String session) throws Exception;
	
	//commit a change to inventory, or insert a new inventory
	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, NoteViewDO noteDO, NoteViewDO manufacturingNote) throws Exception;
	
	//method to return just notes
	public List getInventoryNotes(Integer inventoryItemId);
	
	//method to return just notes
    public NoteViewDO getInventoryMaunfacturingRecipe(Integer inventoryItemId);
	
	//method to return just components
	public List getInventoryComponents(Integer inventoryItemId);
	
	//method to return just locations
	public List getInventoryLocations(Integer inventoryItemId);
    
    //method to return item description by component id
    public String getInventoryDescription(Integer inventoryItemId);
	
	//method to query for inventories
	 public List query(ArrayList fields, int first, int max) throws Exception;
	 
	 //auto complete  component lookup
	 public List inventoryComponentAutoCompleteLookupByName(String itemName, Integer storeId, String currentName, int maxResults);
     
	 //auto complete inv item lookup
     public List inventoryItemStoreAutoCompleteLookupByName(String itemName, int maxResults, boolean limitToMainStore, boolean allowSubAssembly);
     
     //auto complete inv item lookup
     public List inventoryItemStoreLocAutoCompleteLookupByName(String itemName, int maxResults, boolean limitToMainStore, boolean allowSubAssembly);
     
     //auto complete inv item lookup for inventory adjustment screen
     public List inventoryAdjItemAutoCompleteLookupByName(String itemName, Integer storeId, int maxResults);
     
     //auto complete inv item lookup for inventory items that have components
     public List inventoryItemWithComponentsAutoCompleteLookupByName(String itemName, int maxResults);	  
     
     //auto complete inv item lookup for inventory items that are children of the invId param
     public List inventoryItemStoreChildAutoCompleteLookupByName(String itemName, Integer parentId, Integer id, int maxResults);    
*/
