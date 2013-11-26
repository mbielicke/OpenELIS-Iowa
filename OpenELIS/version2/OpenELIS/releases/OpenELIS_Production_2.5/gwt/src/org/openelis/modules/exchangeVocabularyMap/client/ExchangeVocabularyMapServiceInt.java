package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("exchangeVocabulary")
public interface ExchangeVocabularyMapServiceInt extends XsrfProtectedService {

    ExchangeLocalTermManager fetchById(Integer id) throws Exception;

    ExchangeLocalTermManager fetchWithExternalTerms(Integer id) throws Exception;

    ArrayList<ExchangeLocalTermViewDO> query(Query query) throws Exception;

    ExchangeLocalTermManager add(ExchangeLocalTermManager man) throws Exception;

    ExchangeLocalTermManager update(ExchangeLocalTermManager man) throws Exception;

    ExchangeLocalTermManager fetchForUpdate(Integer id) throws Exception;

    ExchangeLocalTermManager abortUpdate(Integer id) throws Exception;

    ExchangeExternalTermManager fetchExternalTermByExchangeLocalTermId(Integer id) throws Exception;

}