package org.openelis.modules.analyteParameter1.client;

import java.util.ArrayList;

import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class AnalyteParameterService1 implements AnalyteParameterServiceInt1, AnalyteParameterServiceInt1Async {
    
    private static AnalyteParameterService1 instance;
    
    private AnalyteParameterServiceInt1Async service;
    
    public static AnalyteParameterService1 get() {
        if (instance == null)
            instance = new AnalyteParameterService1();
        
        return instance;
    }
    
    private AnalyteParameterService1() {
        service = (AnalyteParameterServiceInt1Async)GWT.create(AnalyteParameterServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public AnalyteParameterManager1 getInstance(Integer referenceId, Integer referenceTableId, String referenceName) throws Exception {
        Callback<AnalyteParameterManager1> callback;
        
        callback = new Callback<AnalyteParameterManager1>();
        service.getInstance(referenceId, referenceTableId, referenceName, callback);
        return callback.getResult();
    }
    
    @Override
    public void getInstance(Integer referenceId, Integer referenceTableId, String referenceName, AsyncCallback<AnalyteParameterManager1> callback) {
        service.getInstance(referenceId, referenceTableId, referenceName, callback);
    }

    @Override
    public AnalyteParameterManager1 fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                       Integer referenceTableId) throws Exception {
        Callback<AnalyteParameterManager1> callback;
        
        callback = new Callback<AnalyteParameterManager1>();
        service.fetchByReferenceIdReferenceTableId(referenceId, referenceTableId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId,
                                                   AsyncCallback<AnalyteParameterManager1> callback) {
        service.fetchByReferenceIdReferenceTableId(referenceId, referenceTableId, callback);
    }

    @Override
    public ArrayList<ReferenceIdTableIdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<ReferenceIdTableIdNameVO>> callback;
        
        callback = new Callback<ArrayList<ReferenceIdTableIdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<ReferenceIdTableIdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public AnalyteParameterManager1 fetchForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        Callback<AnalyteParameterManager1> callback;
        
        callback = new Callback<AnalyteParameterManager1>();
        service.fetchForUpdate(referenceId, referenceTableId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer referenceId, Integer referenceTableId,
                               AsyncCallback<AnalyteParameterManager1> callback) {
        service.fetchForUpdate(referenceId, referenceTableId, callback);
    }

    @Override
    public AnalyteParameterManager1 unlock(Integer referenceId, Integer referenceTableId) throws Exception {
        Callback<AnalyteParameterManager1> callback;
        
        callback = new Callback<AnalyteParameterManager1>();
        service.unlock(referenceId, referenceTableId, callback);
        return callback.getResult();
    }
    
    @Override
    public void unlock(Integer referenceId, Integer referenceTableId,
                       AsyncCallback<AnalyteParameterManager1> callback) {
        service.unlock(referenceId, referenceTableId, callback);
    }
}