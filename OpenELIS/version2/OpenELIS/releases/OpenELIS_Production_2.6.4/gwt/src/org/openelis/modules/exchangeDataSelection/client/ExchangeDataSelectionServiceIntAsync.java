package org.openelis.modules.exchangeDataSelection.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExchangeDataSelectionServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback);

    void add(ExchangeCriteriaManager man, AsyncCallback<ExchangeCriteriaManager> callback);

    void delete(ExchangeCriteriaManager man, AsyncCallback<Void> callback);

    void duplicate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback);

    void export(ArrayList<Integer> accessions, ExchangeCriteriaManager cm, AsyncCallback<ReportStatus> callback);
    
    void fetchById(Integer id, AsyncCallback<ExchangeCriteriaManager> callback);

    void fetchByName(String name, AsyncCallback<ExchangeCriteriaManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback);

    void fetchProfileByExchangeCriteriaId(Integer id, AsyncCallback<ExchangeProfileManager> callback);

    void fetchWithProfiles(Integer id, AsyncCallback<ExchangeCriteriaManager> callback);

    void fetchWithProfilesByName(String name, AsyncCallback<ExchangeCriteriaManager> callback);

    void getSamples(ExchangeCriteriaManager man, AsyncCallback<ArrayList<Integer>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(ExchangeCriteriaManager man, AsyncCallback<ExchangeCriteriaManager> callback);

}