package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;

import org.openelis.domain.ExchangeLocalTermViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ExchangeExternalTermManager;
import org.openelis.manager.ExchangeLocalTermManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExchangeVocabularyMapService implements ExchangeVocabularyMapServiceInt, ExchangeVocabularyMapServiceIntAsync {
    
    static ExchangeVocabularyMapService instance;
    
    ExchangeVocabularyMapServiceIntAsync service;
    
    public static ExchangeVocabularyMapService get() {
        if (instance == null)
            instance = new ExchangeVocabularyMapService();
        
        return instance;
    }
    
    private ExchangeVocabularyMapService() {
        service = (ExchangeVocabularyMapServiceIntAsync)GWT.create(ExchangeVocabularyMapServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(ExchangeLocalTermManager man, AsyncCallback<ExchangeLocalTermManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ExchangeLocalTermManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchExternalTermByExchangeLocalTermId(Integer id,
                                                       AsyncCallback<ExchangeExternalTermManager> callback) {
        service.fetchExternalTermByExchangeLocalTermId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ExchangeLocalTermManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchWithExternalTerms(Integer id, AsyncCallback<ExchangeLocalTermManager> callback) {
        service.fetchWithExternalTerms(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<ExchangeLocalTermViewDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(ExchangeLocalTermManager man,
                       AsyncCallback<ExchangeLocalTermManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ExchangeLocalTermManager fetchById(Integer id) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeLocalTermManager fetchWithExternalTerms(Integer id) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.fetchWithExternalTerms(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<ExchangeLocalTermViewDO> query(Query query) throws Exception {
        Callback<ArrayList<ExchangeLocalTermViewDO>> callback;
        
        callback = new Callback<ArrayList<ExchangeLocalTermViewDO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeLocalTermManager add(ExchangeLocalTermManager man) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeLocalTermManager update(ExchangeLocalTermManager man) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeLocalTermManager fetchForUpdate(Integer id) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeLocalTermManager abortUpdate(Integer id) throws Exception {
        Callback<ExchangeLocalTermManager> callback;
        
        callback = new Callback<ExchangeLocalTermManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeExternalTermManager fetchExternalTermByExchangeLocalTermId(Integer id) throws Exception {
        Callback<ExchangeExternalTermManager> callback;
        
        callback = new Callback<ExchangeExternalTermManager>();
        service.fetchExternalTermByExchangeLocalTermId(id, callback);
        return callback.getResult();
    }
    
    

}
