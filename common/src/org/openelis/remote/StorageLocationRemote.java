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

import org.openelis.domain.StorageLocationDO;

@Remote
public interface StorageLocationRemote {
//	commit a change to storage loc, or insert a new storage loc
	public Integer updateStorageLoc(StorageLocationDO storageDO, List storageLocationChildren) throws Exception;
	
	//method to return a whole storage loc
	public StorageLocationDO getStorageLoc(Integer StorageId);
	
	//method to return a whole storage loc
	public StorageLocationDO getStorageLocAndUnlock(Integer StorageId, String session);
	
//	method to return a whole storage loc
	public StorageLocationDO getStorageLocAndLock(Integer StorageId, String session) throws Exception;
	
	//method to return a child storage locs
	public List getStorageLocChildren(Integer StorageId);
	
	 //method to query for storage locs
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
//	auto complete lookup
	 public List autoCompleteLookupByName(String name, int maxResults);
	 
	 public void deleteStorageLoc(Integer StorageLocId) throws Exception;
	 
	 public Integer getStorageLocByName(String name);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(StorageLocationDO storageLocationDO, List childLocs);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(StorageLocationDO storageLocationDO, List childLocs);
	 
	 //method to validate the fields before the backend deletes it
	 public List validateForDelete(Integer storageLocationId);
}

