package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrganizationServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<OrganizationManager> callback);

    void add(OrganizationManager man, AsyncCallback<OrganizationManager> callback);

    void fetchById(Integer id, AsyncCallback<OrganizationManager> callback);

    void fetchByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<OrganizationViewDO>> callback);

    void fetchByIdOrName(String search, AsyncCallback<ArrayList<OrganizationDO>> callback);

    void fetchContactByOrganizationId(Integer id, AsyncCallback<OrganizationContactManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<OrganizationManager> callback);
    
    void fetchParameterByOrganizationId(Integer id,
                                        AsyncCallback<OrganizationParameterManager> callback);
    
    void fetchParametersByOrganizationId(Integer id,
                                         AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

    void fetchParametersByDictionarySystemName(String systemName,
                                               AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

    void fetchWithContacts(Integer id, AsyncCallback<OrganizationManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<OrganizationManager> callback);

    void fetchWithParameters(Integer id, AsyncCallback<OrganizationManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(OrganizationManager man, AsyncCallback<OrganizationManager> callback);

    void updateForNotify(ArrayList<OrganizationParameterDO> parameters, AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

}