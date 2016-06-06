package org.openelis.modules.organization1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.manager.OrganizationManager1;
import org.openelis.manager.OrganizationManager1.Load;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("organization1")
public interface OrganizationService1 extends XsrfProtectedService {

    OrganizationManager1 fetchById(Integer id, Load... elements) throws Exception;

    ArrayList<OrganizationManager1> fetchByIds(ArrayList<Integer> ids, Load... elements) throws Exception;

    ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception;

    ArrayList<OrganizationParameterDO> fetchParametersByOrganizationId(Integer id) throws Exception;

    ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    OrganizationManager1 fetchForUpdate(Integer id) throws Exception;

    OrganizationManager1 fetchForUpdate(Integer id, Load... elements) throws Exception;

    ArrayList<OrganizationManager1> fetchForUpdate(ArrayList<Integer> ids, Load... elements) throws Exception;

    OrganizationManager1 unlock(Integer id, Load... elements) throws Exception;

    ArrayList<OrganizationManager1> unlock(ArrayList<Integer> id, Load... elements) throws Exception;

    OrganizationManager1 update(OrganizationManager1 man) throws Exception;

    ArrayList<OrganizationParameterDO> updateForNotify(ArrayList<OrganizationParameterDO> parameters) throws Exception;
}