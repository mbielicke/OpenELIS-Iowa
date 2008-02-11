package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.StorageUnitDO;

@Remote
public interface StorageUnitRemote {
	//commit a change to storage unit, or insert a new storage unit
	public Integer updateStorageUnit(StorageUnitDO unitDO);
	
	//method to return a whole storage unit
	public StorageUnitDO getStorageUnit(Integer StorageUnitId);
	
	//method to return a whole storage unit and lock it
	public StorageUnitDO getStorageUnitAndLock(Integer StorageUnitId) throws Exception;
	
	//method to return a whole storage unit and unlock it
	public StorageUnitDO getStorageUnitAndUnlock(Integer StorageUnitId);
	
	 //method to query for storage units
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
//	auto complete lookup
	 public List autoCompleteLookupByDescription(String desc, int maxResults);
	 
	 //auto complete lookup
	 public Object[] autoCompleteLookupById(Integer id);
	 
	 public void deleteStorageUnit(Integer StorageUnitId) throws Exception;

}
