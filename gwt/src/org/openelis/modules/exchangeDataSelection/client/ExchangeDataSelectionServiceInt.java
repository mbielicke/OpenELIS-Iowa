package org.openelis.modules.exchangeDataSelection.client;

import java.util.ArrayList;

import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("exchangeData")
public interface ExchangeDataSelectionServiceInt extends RemoteService {

    ExchangeCriteriaManager fetchById(Integer id) throws Exception;

    ExchangeCriteriaManager fetchByName(String name) throws Exception;

    ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception;

    ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ArrayList<IdAccessionVO> dataExchangeQuery(Query query) throws Exception;

    ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception;

    ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception;

    void delete(ExchangeCriteriaManager man) throws Exception;

    ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception;

    ExchangeCriteriaManager abortUpdate(Integer id) throws Exception;

    ExchangeCriteriaManager duplicate(Integer id) throws Exception;

    //
    // support for ExchangeProfileManager
    //
    ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception;

}