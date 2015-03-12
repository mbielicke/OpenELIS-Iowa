package org.openelis.cache;

import java.util.ArrayList;

import javax.inject.Inject;

import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class UserCacheService implements UserCacheServiceInt, UserCacheServiceIntAsync{
    
    static UserCacheService instance;
    
    UserCacheServiceIntAsync service;
    
    public static UserCacheService get() {
        if(instance == null)
            instance = new UserCacheService();
        
        return instance;
    }
    
    @Inject
    public UserCacheService() {
        service = (UserCacheServiceIntAsync)GWT.create(UserCacheServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getEmployees(String name, AsyncCallback<ArrayList<SystemUserVO>> callback) {
        service.getEmployees(name, callback);
    }

    @Override
    public void getPermission(AsyncCallback<SystemUserPermission> callback) {
        service.getPermission(callback);
    }

    @Override
    public void getSystemUser(Integer id, AsyncCallback<SystemUserVO> callback) {
        service.getSystemUser(id, callback);
    }

    @Override
    public void getSystemUsers(String name, AsyncCallback<ArrayList<SystemUserVO>> callback) {
        service.getSystemUsers(name, callback);
    }

    @Override
    public SystemUserVO getSystemUser(Integer id) throws Exception {
        Callback<SystemUserVO> callback;
        
        callback = new Callback<SystemUserVO>();
        service.getSystemUser(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SystemUserVO> getSystemUsers(String name) throws Exception {
        Callback<ArrayList<SystemUserVO>> callback;
        
        callback = new Callback<ArrayList<SystemUserVO>>();
        service.getSystemUsers(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SystemUserVO> getEmployees(String name) throws Exception {
        Callback<ArrayList<SystemUserVO>> callback;
        
        callback = new Callback<ArrayList<SystemUserVO>>();
        service.getEmployees(name, callback);
        return callback.getResult();
    }

    @Override
    public SystemUserPermission getPermission() throws Exception {
        Callback<SystemUserPermission> callback;
        
        callback = new Callback<SystemUserPermission>();
        service.getPermission(callback);
        return callback.getResult();
    }

    @Override
    public void validateSystemUsers(ArrayList<String> names, AsyncCallback<ArrayList<SystemUserVO>> callback) {
        service.validateSystemUsers(names, callback);
    }

    @Override
    public ArrayList<SystemUserVO> validateSystemUsers(ArrayList<String> names) throws Exception {
        Callback<ArrayList<SystemUserVO>> callback;
        
        callback = new Callback<ArrayList<SystemUserVO>>();
        service.validateSystemUsers(names, callback);
        return callback.getResult();
    }
}