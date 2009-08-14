package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;

@Remote
public interface OrganizationManagerRemote {
    public OrganizationManager update(OrganizationManager man) throws Exception;
    public OrganizationManager add(OrganizationManager man) throws Exception;
    public OrganizationManager fetch(Integer orgId) throws Exception;
    public OrganizationManager fetchWithContacts(Integer orgId) throws Exception;
    public OrganizationManager fetchWithIdentifiers(Integer orgId) throws Exception;
    public OrganizationManager fetchWithNotes(Integer orgId) throws Exception;
    
    public OrganizationManager fetchForUpdate(Integer orgId) throws Exception;
    public OrganizationManager abortUpdate(Integer orgId) throws Exception;
    
    public OrganizationContactManager fetchContactById(Integer id) throws Exception;
    public OrganizationContactManager fetchContactByOrgId(Integer orgId) throws Exception;
}
