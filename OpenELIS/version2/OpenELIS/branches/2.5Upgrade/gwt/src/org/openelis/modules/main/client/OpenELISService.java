package org.openelis.modules.main.client;

import org.openelis.ui.common.Datetime;
import org.openelis.domain.Constants;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpenELISService implements OpenELISServiceInt, OpenELISServiceIntAsync {
    
    static OpenELISService instance;
    
    OpenELISServiceIntAsync service;
    
    public static OpenELISService get() {
        if(instance == null)
            instance = new OpenELISService();
        
        return instance;
    }
    
    private OpenELISService() {
        service = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    }

    @Override
    public void getConstants(AsyncCallback<Constants> callback) {
        service.getConstants(callback);
    }

    @Override
    public void keepAlive(AsyncCallback<Void> callback) {
        service.keepAlive(callback);
    }

    @Override
    public void logout(AsyncCallback<Void> callback) {
        service.logout(callback);
    }

    @Override
    public Constants getConstants() throws Exception {
        Callback<Constants> callback;
        
        callback = new Callback<Constants>();
        service.getConstants(callback);
        return callback.getResult();
    }

    @Override
    public void keepAlive() {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.keepAlive(callback);
    }

    @Override
    public void logout() {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.logout(callback);
    }

    @Override
    public void getLastAccess(AsyncCallback<Datetime> callback) {
        service.getLastAccess(callback);
    }

    @Override
    public Datetime getLastAccess() {
        Callback<Datetime> callback;
        
        callback = new Callback<Datetime>();
        service.getLastAccess(callback);
        try {
            return callback.getResult();
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
