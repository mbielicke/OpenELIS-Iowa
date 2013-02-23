package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExchangeVocabularyMapServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);

    void add(ExchangeLocalTermManager man, AsyncCallback<ExchangeLocalTermManager> callback);

    void fetchById(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);

    void fetchExternalTermByExchangeLocalTermId(Integer id,
                                                AsyncCallback<ExchangeExternalTermManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);

    void fetchWithExternalTerms(Integer id, AsyncCallback<ExchangeLocalTermManager> callback);

    void query(Query query, AsyncCallback<ArrayList<ExchangeLocalTermViewDO>> callback);

    void update(ExchangeLocalTermManager man, AsyncCallback<ExchangeLocalTermManager> callback);

}
