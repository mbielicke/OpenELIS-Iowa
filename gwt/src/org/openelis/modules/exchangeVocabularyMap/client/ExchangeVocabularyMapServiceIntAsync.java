package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.domain.TestAnalyteViewVO;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExchangeVocabularyMapServiceIntAsync {

    void fetchById(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);
    
    void fetchTestAnalytes(ArrayList<QueryData> fields,
                           AsyncCallback<ArrayList<TestAnalyteViewVO>> callback);

    void fetchWithExternalTerms(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);

    void query(Query query, AsyncCallback<ArrayList<ExchangeLocalTermViewDO>> callback);

    void add(ExchangeLocalTermManager man, AsyncCallback<ExchangeLocalTermManager> callback);
    
    void update(ExchangeLocalTermManager man, AsyncCallback<ExchangeLocalTermManager> callback);
    
    void fetchForUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);
    
    void abortUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);
    
    void fetchExternalTermByExchangeLocalTermId(Integer id,
                                                AsyncCallback<ExchangeExternalTermManager> callback);
}
