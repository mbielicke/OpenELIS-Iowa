package org.openelis.remote;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;

@Remote
public interface OrganizationRemote {

	//method to return list of org ids and org names
	public List getOrganizationNameList(int startPos, int maxResults);

	//method to return list of org ids and org names by what letter they start with
	public List getOrganizationNameListByLetter(String letter, int startPos, int maxResults);
	
	//method to return org name, address
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId, boolean unlock);
	
	//update initial call for org
	public OrganizationAddressDO getOrganizationAddressUpdate(Integer id) throws Exception;
	
	//commit a change to org, or insert a new org
	public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List contacts);
	
	//method to return just notes
	public List getOrganizationNotes(Integer organizationId, boolean topLevel);
	
	//method to return just contacts
	public List getOrganizationContacts(Integer organizationId);
	
	//update organization
	
	//delete things
}
