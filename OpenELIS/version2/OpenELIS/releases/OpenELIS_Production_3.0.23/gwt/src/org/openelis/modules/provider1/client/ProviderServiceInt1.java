package org.openelis.modules.provider1.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("provider1")
public interface ProviderServiceInt1 extends XsrfProtectedService {

    ProviderManager1 getInstance() throws Exception;

    ProviderManager1 fetchById(Integer id) throws Exception;

    ArrayList<ProviderManager1> fetchByIds(ArrayList<Integer> ids) throws Exception;

    ArrayList<ProviderDO> fetchByLastNameNpiExternalId(String search) throws Exception;

    ArrayList<IdFirstLastNameVO> query(Query query) throws Exception;

    ArrayList<ProviderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception;

    ProviderManager1 fetchForUpdate(Integer id) throws Exception;

    ArrayList<ProviderManager1> fetchForUpdate(ArrayList<Integer> ids) throws Exception;

    ProviderManager1 unlock(Integer providerId) throws Exception;

    ArrayList<ProviderManager1> unlock(ArrayList<Integer> providerIds) throws Exception;

    ProviderManager1 update(ProviderManager1 man) throws Exception;

    ProviderManager1 update(ProviderManager1 man, boolean ignoreWarnings) throws Exception;
}