package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.OrganizationContactsManager;
import org.openelis.manager.OrganizationsManager;

@Remote
public interface OrganizationManagerRemote {
    public OrganizationsManager update(OrganizationsManager man) throws Exception;
    public OrganizationsManager add(OrganizationsManager man) throws Exception;
    public OrganizationsManager fetch(Integer orgId) throws Exception;
    public OrganizationsManager fetchWithContacts(Integer orgId) throws Exception;
    public OrganizationsManager fetchWithIdentifiers(Integer orgId) throws Exception;
    public OrganizationsManager fetchWithNotes(Integer orgId) throws Exception;
    
    public OrganizationsManager fetchForUpdate(Integer orgId);
    public OrganizationsManager abortUpdate(Integer orgId);
    
    public OrganizationContactsManager fetchContactById(Integer id) throws Exception;
    public OrganizationContactsManager fetchContactByOrgId(Integer orgId) throws Exception;
}
