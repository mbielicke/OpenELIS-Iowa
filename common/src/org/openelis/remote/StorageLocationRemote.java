package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.StorageLocationDO;

@Remote
public interface StorageLocationRemote {
//	commit a change to storage loc, or insert a new storage loc
	public Integer updateStorageLoc(StorageLocationDO storageDO, List storageLocationChildren) throws Exception;
	
	//method to return a whole storage loc
	public StorageLocationDO getStorageLoc(Integer StorageId);
	
	//method to return a whole storage loc
	public StorageLocationDO getStorageLocAndUnlock(Integer StorageId);
	
//	method to return a whole storage loc
	public StorageLocationDO getStorageLocAndLock(Integer StorageId) throws Exception;
	
	//method to return a child storage locs
	public List getStorageLocChildren(Integer StorageId);
	
	 //method to query for storage locs
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
//	auto complete lookup
	 public List autoCompleteLookupByName(String name, int maxResults);
	 
	 //auto complete lookup
	 public Object[] autoCompleteLookupById(Integer id);
	 
	 public void deleteStorageLoc(Integer StorageLocId) throws Exception;
	 
	 public Integer getStorageLocByName(String name);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(StorageLocationDO storageLocationDO, List childLocs);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(StorageLocationDO storageLocationDO, List childLocs);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer storageLocationId);
}

