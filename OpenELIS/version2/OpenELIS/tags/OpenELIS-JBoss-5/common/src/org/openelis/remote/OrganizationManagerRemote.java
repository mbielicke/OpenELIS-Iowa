package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;

@Remote
public interface OrganizationManagerRemote {

    public OrganizationManager fetchById(Integer id) throws Exception;
    
    public ArrayList<OrganizationManager> fetchByIdList(ArrayList<Integer> ids) throws Exception;
    
    public OrganizationManager fetchWithContacts(Integer id) throws Exception;

    public OrganizationManager fetchWithParameters(Integer id) throws Exception;

    public OrganizationManager fetchWithNotes(Integer id) throws Exception;

    public OrganizationManager add(OrganizationManager man) throws Exception;

    public OrganizationManager update(OrganizationManager man) throws Exception;

    public OrganizationManager updateForNotify(OrganizationManager man) throws Exception;

    public OrganizationManager fetchForUpdate(Integer id) throws Exception;
    
    public OrganizationManager abortUpdate(Integer id) throws Exception;
    
    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception;

    public OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception;

}
