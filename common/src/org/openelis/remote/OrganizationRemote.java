package org.openelis.remote;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;

@Remote
public interface OrganizationRemote {
	//method to return list of org ids and org names by what letter they start with
	public List getOrganizationNameListByLetter(String letter, int startPos, int maxResults);
	
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
