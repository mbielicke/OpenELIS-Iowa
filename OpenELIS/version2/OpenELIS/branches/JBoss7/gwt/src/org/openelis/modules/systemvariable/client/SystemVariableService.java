package org.openelis.modules.systemvariable.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemVariableService implements SystemVariableServiceInt,
                                  SystemVariableServiceIntAsync {
    
    static SystemVariableService instance;
    
    SystemVariableServiceIntAsync service;
    
    public static SystemVariableService get() {
        if(instance == null)
            instance = new SystemVariableService();
        
        return instance;
    }
    
    private SystemVariableService() {
        service = (SystemVariableServiceIntAsync)GWT.create(SystemVariableServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<SystemVariableDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(SystemVariableDO data, AsyncCallback<SystemVariableDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(SystemVariableDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);

    }

    @Override
    public void fetchById(Integer id, AsyncCallback<SystemVariableDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String name, AsyncCallback<ArrayList<SystemVariableDO>> callback) {
        service.fetchByName(name, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<SystemVariableDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(SystemVariableDO data, AsyncCallback<SystemVariableDO> callback) {
        service.update(data, callback);
    }

    @Override
    public SystemVariableDO fetchById(Integer id) throws Exception {
        Callback<SystemVariableDO> callback;
        
        callback = new Callback<SystemVariableDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SystemVariableDO> fetchByName(String name) throws Exception {
        Callback<ArrayList<SystemVariableDO>> callback;
        
        callback = new Callback<ArrayList<SystemVariableDO>>();
        service.fetchByName(name, callback);
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
    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        Callback<SystemVariableDO> callback;
        
        callback = new Callback<SystemVariableDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        Callback<SystemVariableDO> callback;
        
        callback = new Callback<SystemVariableDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        Callback<SystemVariableDO> callback;
        
        callback = new Callback<SystemVariableDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(SystemVariableDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
    }

    @Override
    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        Callback<SystemVariableDO> callback;
        
        callback = new Callback<SystemVariableDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
