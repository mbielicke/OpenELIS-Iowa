package org.openelis.modules.main.client;

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
    public OpenELISRPC initialData() throws Exception {
        Callback<OpenELISRPC> callback;
        
        callback = new Callback<OpenELISRPC>();
        service.initialData(callback);
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
    
    
}
