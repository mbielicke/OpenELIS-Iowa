package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;

@Remote
public interface OrganizationManagerRemote {

    public OrganizationManager fetchById(Integer id) throws Exception;

    public OrganizationManager fetchWithContacts(Integer id) throws Exception;

    public OrganizationManager fetchWithIdentifiers(Integer id) throws Exception;

    public OrganizationManager fetchWithNotes(Integer id) throws Exception;

    public OrganizationManager add(OrganizationManager man) throws Exception;

    public OrganizationManager update(OrganizationManager man) throws Exception;

    public OrganizationManager fetchForUpdate(Integer id) throws Exception;

    public OrganizationManager abortUpdate(Integer id) throws Exception;

    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception;
}
