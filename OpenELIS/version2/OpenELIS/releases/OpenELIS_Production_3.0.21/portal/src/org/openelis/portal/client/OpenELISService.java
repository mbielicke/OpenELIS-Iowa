package org.openelis.portal.client;

import org.openelis.domain.Constants;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

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
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getConstants(AsyncCallback<Constants> callback) {
        service.getConstants(callback);
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
    public void logout() {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.logout(callback);
    }    
}
