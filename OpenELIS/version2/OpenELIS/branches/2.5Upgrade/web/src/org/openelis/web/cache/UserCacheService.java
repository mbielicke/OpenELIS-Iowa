package org.openelis.web.cache;

import java.util.ArrayList;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserCacheService implements UserCacheServiceInt, UserCacheServiceIntAsync{
    
    static UserCacheService instance;
    
    UserCacheServiceIntAsync service;
    
    public static UserCacheService get() {
        if(instance == null)
            instance = new UserCacheService();
        
        return instance;
    }
    
    private UserCacheService() {
        service = (UserCacheServiceIntAsync)GWT.create(UserCacheServiceInt.class);
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
    
    
}
