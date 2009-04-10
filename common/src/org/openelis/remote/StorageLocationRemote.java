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

import org.openelis.domain.StorageLocationDO;
import org.openelis.gwt.common.data.AbstractField;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

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
	 public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
	 
//	auto complete lookup
	 public List autoCompleteLookupByName(String name, int maxResults);
	 
	 public void deleteStorageLoc(Integer StorageLocId) throws Exception;
	 
	 public Integer getStorageLocByName(String name);
}

