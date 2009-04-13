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
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.data.AbstractField;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface OrganizationRemote {
	//method to return org name, address
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId);
	
	//method to unlock entity and return org name, address
	public OrganizationAddressDO getOrganizationAddressAndUnlock(Integer organizationId, String session);
	
	//update initial call for org
	public OrganizationAddressDO getOrganizationAddressAndLock(Integer organizationId, String session) throws Exception;
	
	//commit a change to org, or insert a new org
	public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List<OrganizationContactDO> contacts) throws Exception;
	
	//method to return just notes
	public List getOrganizationNotes(Integer organizationId);
	
	//method to return just contacts
	public List getOrganizationContacts(Integer organizationId);
	
	//method to query for organizations
	 public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
	 
	 //auto complete lookup
	 public List autoCompleteLookupByName(String orgName, int maxResults);
	 
	 //auto complete lookup
	 public List autoCompleteLookupById(Integer id);
}
