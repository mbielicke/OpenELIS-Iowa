package org.openelis.modules.provider.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;
import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("provider")
public interface ProviderService extends XsrfProtectedService {

    ProviderManager fetchById(Integer id) throws Exception;
    
    ArrayList<ProviderDO> fetchByLastNameNpiExternalId(String search) throws Exception;

    ProviderManager fetchWithLocations(Integer id) throws Exception;

    ProviderManager fetchWithNotes(Integer id) throws Exception;

    ArrayList<IdFirstLastNameVO> query(Query query) throws Exception;

    ProviderManager add(ProviderManager man) throws Exception;

    ProviderManager update(ProviderManager man) throws Exception;

    ProviderManager fetchForUpdate(Integer id) throws Exception;

    ProviderManager abortUpdate(Integer id) throws Exception;

    //
    // support for ProviderContactManager and ProviderParameterManager
    //
    ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception;

}