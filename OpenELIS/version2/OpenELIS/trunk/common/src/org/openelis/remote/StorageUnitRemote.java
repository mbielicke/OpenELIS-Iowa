package org.openelis.remote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.StorageUnitDO;

@Remote
public interface StorageUnitRemote {
	//commit a change to org, or insert a new org
	public Integer updateStorageUnit(StorageUnitDO unitDO);
	
	//method to return a whole storage unit
	public StorageUnitDO getStorageUnit(Integer StorageUnitId, boolean unlock);
	
	 //method to query for storage units
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 public void deleteStorageUnit(Integer StorageUnitId) throws RemoteException;

}
