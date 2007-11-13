package org.openelis.bean;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationTableRowDO;
import org.openelis.remote.OrganizationRemote;

@Stateless
//@SecurityDomain("security")
//@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    private String className = this.getClass().getName();
    private Logger log = Logger.getLogger(className);
    
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId) {
		Query query = manager.createNamedQuery("getOrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressContacts = (OrganizationAddressDO) query.getResultList().get(0);// getting organization with address and contacts
		
		
        return orgAddressContacts;
	}

	public List<OrganizationTableRowDO> getOrganizationNameList(int startPos, int maxResults) {
		Query query = manager.createNamedQuery("getOrganizationNameRows");
		
		if(maxResults > 0){
			query.setFirstResult(startPos);
			query.setMaxResults(maxResults);
		}
		
		List<OrganizationTableRowDO>  orgList = query.getResultList();// getting a list of organizations
		
        return orgList;

	}

	public List<OrganizationContactDO> getOrganizationContacts(Integer organizationId) {
		Query query = manager.createNamedQuery("getOrganizationContacts");
		query.setParameter("id", organizationId);
		
		List<OrganizationContactDO> orgAddressContacts = query.getResultList();// getting list of contacts from the org id
	
        return orgAddressContacts;
	}
	
	public List<NoteDO> getOrganizationNotes(Integer organizationId) {
		Query query = manager.createNamedQuery("getOrganizationNotes");
		query.setParameter("id", organizationId);
		
		List<NoteDO> orgNotes = query.getResultList();// getting list of notes from the org id
		
        return orgNotes;
	}
	
	public List<OrganizationTableRowDO> getOrganizationNameListByLetter(String letter, int startPos, int maxResults){
		Query query = manager.createNamedQuery("getOrganizationNameRowsByLetter");
		query.setParameter("letter", letter);
		
		if(maxResults > 0){
			query.setFirstResult(startPos);
			query.setMaxResults(maxResults);
		}
		
		List<OrganizationTableRowDO>  orgList = query.getResultList();// getting a list of organizations
		
		return orgList;
	}

	
}
