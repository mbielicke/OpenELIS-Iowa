package org.openelis.modules.method.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MethodService implements MethodServiceInt, MethodServiceIntAsync {
    
    static MethodService instance;
    
    MethodServiceIntAsync service;
    
    public static MethodService get() {
        if(instance == null)
            instance = new MethodService();
        
        return instance;
    }
    
    private MethodService() {
        service = (MethodServiceIntAsync)GWT.create(MethodServiceInt.class);
    }
    
    @Override
    public void abortUpdate(Integer id, AsyncCallback<MethodDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(MethodDO data, AsyncCallback<MethodDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<MethodDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<MethodDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<MethodDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(MethodDO data, AsyncCallback<MethodDO> callback) {
        service.update(data, callback);
    }

    @Override
    public ArrayList<MethodDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<MethodDO>> callback;
        
        callback = new Callback<ArrayList<MethodDO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO fetchById(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.fetchById(id, callback);
        return callback.getResult();    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO add(MethodDO data) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO update(MethodDO data) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO fetchForUpdate(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO abortUpdate(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
