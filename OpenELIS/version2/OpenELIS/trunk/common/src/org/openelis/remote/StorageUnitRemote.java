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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.StorageUnitDO;

@Remote
public interface StorageUnitRemote {
	//commit a change to storage unit, or insert a new storage unit
	public Integer updateStorageUnit(StorageUnitDO unitDO) throws Exception;
	
	//method to return a whole storage unit
	public StorageUnitDO getStorageUnit(Integer StorageUnitId);
	
	//method to return a whole storage unit and lock it
	public StorageUnitDO getStorageUnitAndLock(Integer StorageUnitId, String session) throws Exception;
	
	//method to return a whole storage unit and unlock it
	public StorageUnitDO getStorageUnitAndUnlock(Integer StorageUnitId, String session);
	
	 //method to query for storage units
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
//	auto complete lookup
	 public List autoCompleteLookupByDescription(String desc, int maxResults);
	 
	 public void deleteStorageUnit(Integer StorageUnitId) throws Exception;
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(StorageUnitDO storageUnitDO);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(StorageUnitDO storageUnitDO);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer storageUnitId);

}
