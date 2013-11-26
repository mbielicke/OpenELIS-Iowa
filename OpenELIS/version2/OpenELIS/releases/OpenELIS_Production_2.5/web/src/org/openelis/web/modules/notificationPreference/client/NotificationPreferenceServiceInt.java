package org.openelis.web.modules.notificationPreference.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("notificationPreference")
public interface NotificationPreferenceServiceInt extends XsrfProtectedService {

    ArrayList<OrganizationViewDO> fetchByIds(ArrayList<Integer> ids) throws Exception;

    ArrayList<OrganizationParameterDO> fetchParametersByOrganizationId(Integer id) throws Exception;
    
    ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ArrayList<OrganizationParameterDO> updateForNotify(ArrayList<OrganizationParameterDO> parameters) throws Exception;
}