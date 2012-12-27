package org.openelis.web.modules.main.client;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpenELISWebService implements OpenELISWebServiceInt, OpenELISWebServiceIntAsync {
    
    static OpenELISWebService instance;
    
    OpenELISWebServiceIntAsync service;
    
    public static OpenELISWebService get() {
        if(instance == null)
            instance = new OpenELISWebService();
        
        return instance;
    }
    
    private OpenELISWebService() {
        service = (OpenELISWebServiceIntAsync)GWT.create(OpenELISWebServiceInt.class);
    }

    @Override
    public void initialData(AsyncCallback<OpenELISRPC> callback) {
        service.initialData(callback);
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
    public OpenELISRPC initialData() {
        Callback<OpenELISRPC> callback;
        
        callback = new Callback<OpenELISRPC>();
        service.initialData(callback);
        try {
            return callback.getResult();
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
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
