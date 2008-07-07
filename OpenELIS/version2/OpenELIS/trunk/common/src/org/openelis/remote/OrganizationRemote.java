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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.remote;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;

@Remote
public interface OrganizationRemote {
	//method to return org name, address
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId);
	
	//method to unlock entity and return org name, address
	public OrganizationAddressDO getOrganizationAddressAndUnlock(Integer organizationId);
	
	//update initial call for org
	public OrganizationAddressDO getOrganizationAddressAndLock(Integer organizationId) throws Exception;
	
	//commit a change to org, or insert a new org
	public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List contacts) throws Exception;
	
	//method to return just notes
	public List getOrganizationNotes(Integer organizationId);
	
	//method to return just contacts
	public List getOrganizationContacts(Integer organizationId);
	
	//method to query for organizations
	 public List query(HashMap fields, int first, int max) throws Exception;
	 
	 //auto complete lookup
	 public List autoCompleteLookupByName(String orgName, int maxResults);
	 
	 //auto complete lookup
	 public List autoCompleteLookupById(Integer id);
	 
	 //a way for the servlet to get the system user id
	 public Integer getSystemUserId();
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForUpdate(OrganizationAddressDO organizationDO, List contacts);
	 
	 //method to validate the fields before the backend updates it in the database
	 public List validateForAdd(OrganizationAddressDO organizationDO, List contacts);
}
