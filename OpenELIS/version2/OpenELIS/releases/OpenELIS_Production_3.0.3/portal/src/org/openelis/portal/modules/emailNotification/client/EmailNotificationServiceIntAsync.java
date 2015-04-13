package org.openelis.portal.modules.emailNotification.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EmailNotificationServiceIntAsync {

    void fetchByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<OrganizationViewDO>> callback);

    void fetchParametersByOrganizationId(Integer id,
                                         AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

    void fetchParametersByOrganizationIds(ArrayList<Integer> ids,
                                          AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

    void fetchParametersByDictionarySystemName(String systemName,
                                               AsyncCallback<ArrayList<OrganizationParameterDO>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void updateForNotify(ArrayList<OrganizationParameterDO> parameters,
                         AsyncCallback<ArrayList<OrganizationParameterDO>> callback);
}
