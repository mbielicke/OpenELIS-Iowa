package org.openelis.modules.exchangeDataSelection.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class ExchangeDataSelectionService implements ExchangeDataSelectionServiceInt,  ExchangeDataSelectionServiceIntAsync{
    
    static ExchangeDataSelectionService instance;
    
    ExchangeDataSelectionServiceIntAsync service;
    
    public static ExchangeDataSelectionService get() {
        if (instance == null)
            instance = new ExchangeDataSelectionService();
        
        return instance;
    }
    
    private ExchangeDataSelectionService() {
        service = (ExchangeDataSelectionServiceIntAsync)GWT.create(ExchangeDataSelectionServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(ExchangeCriteriaManager man, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void getSamples(ExchangeCriteriaManager man, AsyncCallback<ArrayList<Integer>> callback) {
        service.getSamples(man, callback);
    }

    @Override
    public void delete(ExchangeCriteriaManager man, AsyncCallback<Void> callback) {
        service.delete(man, callback);
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public void export(ArrayList<Integer> accessions, ExchangeCriteriaManager man, AsyncCallback<ReportStatus> callback) {
        service.export(accessions, man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String name, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.fetchByName(name, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchProfileByExchangeCriteriaId(Integer id,
                                                 AsyncCallback<ExchangeProfileManager> callback) {
        service.fetchProfileByExchangeCriteriaId(id, callback);
    }

    @Override
    public void fetchWithProfiles(Integer id, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.fetchWithProfiles(id, callback);
    }

    @Override
    public void fetchWithProfilesByName(String name, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.fetchWithProfilesByName(name, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(ExchangeCriteriaManager man, AsyncCallback<ExchangeCriteriaManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.fetchByName(name, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.fetchWithProfiles(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.fetchWithProfilesByName(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<Integer> getSamples(ExchangeCriteriaManager man) throws Exception {
        Callback<ArrayList<Integer>> callback;
        
        callback = new Callback<ArrayList<Integer>>();
        service.getSamples(man, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public void delete(ExchangeCriteriaManager man) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(man, callback);
        callback.getResult();
    }

    @Override
    public ReportStatus export(ArrayList<Integer> accessions, ExchangeCriteriaManager man) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.export(accessions, man, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeCriteriaManager duplicate(Integer id) throws Exception {
        Callback<ExchangeCriteriaManager> callback;
        
        callback = new Callback<ExchangeCriteriaManager>();
        service.duplicate(id, callback);
        return callback.getResult();
    }

    @Override
    public ExchangeProfileManager fetchProfileByExchangeCriteriaId(Integer id) throws Exception {
        Callback<ExchangeProfileManager> callback;
        
        callback = new Callback<ExchangeProfileManager>();
        service.fetchProfileByExchangeCriteriaId(id, callback);
        return callback.getResult();
    }
    
    

}
