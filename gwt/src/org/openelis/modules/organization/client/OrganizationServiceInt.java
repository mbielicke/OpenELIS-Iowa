package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("organization")
public interface OrganizationServiceInt extends RemoteService {

    OrganizationManager fetchById(Integer id) throws Exception;

    ArrayList<OrganizationManager> fetchByIdList(Query query) throws Exception;

    ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception;

    ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception;

    OrganizationManager fetchWithContacts(Integer id) throws Exception;

    OrganizationManager fetchWithNotes(Integer id) throws Exception;

    OrganizationManager fetchWithParameters(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    OrganizationManager add(OrganizationManager man) throws Exception;

    OrganizationManager update(OrganizationManager man) throws Exception;

    OrganizationManager updateForNotify(OrganizationManager man) throws Exception;

    OrganizationManager fetchForUpdate(Integer id) throws Exception;

    OrganizationManager abortUpdate(Integer id) throws Exception;

    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception;

    OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception;

}