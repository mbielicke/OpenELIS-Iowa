package org.openelis.modules.analyteParameter.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AnalyteParameterManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalyteParameterService implements AnalyteParameterServiceInt, AnalyteParameterServiceIntAsync {
    
    static AnalyteParameterService instance;
    
    AnalyteParameterServiceIntAsync service;
    
    public static AnalyteParameterService get() {
        if (instance == null)
            instance = new AnalyteParameterService();
        
        return instance;
    }
    
    private AnalyteParameterService() {
        service = (AnalyteParameterServiceIntAsync)GWT.create(AnalyteParameterServiceInt.class);
    }

    @Override
    public void abortUpdate(AnalyteParameterManager man,
                            AsyncCallback<AnalyteParameterManager> callback) {
        service.abortUpdate(man, callback);
    }

    @Override
    public void add(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActiveByReferenceIdReferenceTableId(Query query,
                                                         AsyncCallback<AnalyteParameterManager> callback) {
        service.fetchActiveByReferenceIdReferenceTableId(query, callback);
    }

    @Override
    public void fetchByAnalyteIdReferenceIdReferenceTableId(Query query,
                                                            AsyncCallback<ArrayList<AnalyteParameterViewDO>> callback) {
        service.fetchByAnalyteIdReferenceIdReferenceTableId(query, callback);
    }

    @Override
    public void fetchForUpdate(AnalyteParameterManager man,
                               AsyncCallback<AnalyteParameterManager> callback) {
        service.fetchForUpdate(man, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<ReferenceIdTableIdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(AnalyteParameterManager man, AsyncCallback<AnalyteParameterManager> callback) {
        service.update(man, callback);
    }

    @Override
    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Query query) throws Exception {
        Callback<AnalyteParameterManager> callback;
        
        callback = new Callback<AnalyteParameterManager>();
        service.fetchActiveByReferenceIdReferenceTableId(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Query query) throws Exception {
        Callback<ArrayList<AnalyteParameterViewDO>> callback;
        
        callback = new Callback<ArrayList<AnalyteParameterViewDO>>();
        service.fetchByAnalyteIdReferenceIdReferenceTableId(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<ReferenceIdTableIdNameVO>> callback;
        
        callback = new Callback<ArrayList<ReferenceIdTableIdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        Callback<AnalyteParameterManager> callback;
        
        callback = new Callback<AnalyteParameterManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        Callback<AnalyteParameterManager> callback;
        
        callback = new Callback<AnalyteParameterManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        Callback<AnalyteParameterManager> callback;
        
        callback = new Callback<AnalyteParameterManager>();
        service.fetchForUpdate(man, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        Callback<AnalyteParameterManager> callback;
        
        callback = new Callback<AnalyteParameterManager>();
        service.abortUpdate(man, callback);
        return callback.getResult();
    }

}
