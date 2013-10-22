package org.openelis.modules.exchangeDataSelection.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("exchangeData")
public interface ExchangeDataSelectionServiceInt extends XsrfProtectedService {

    ExchangeCriteriaManager fetchById(Integer id) throws Exception;

    ExchangeCriteriaManager fetchByName(String name) throws Exception;

    ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception;

    ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception;

    ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception;

    void delete(ExchangeCriteriaManager man) throws Exception;

    ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception;

    ExchangeCriteriaManager abortUpdate(Integer id) throws Exception;

    ExchangeCriteriaManager duplicate(Integer id) throws Exception;

    ArrayList<Integer> getAccessions(ExchangeCriteriaManager man) throws Exception;
    
    ReportStatus export(ArrayList<Integer> accessions, ExchangeCriteriaManager cm) throws Exception;

    ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception;

}