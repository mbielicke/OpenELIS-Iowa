package org.openelis.portal.client;

import org.openelis.ui.screen.Callback;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class TimerService implements TimerServiceInt, TimerServiceIntAsync {
    
    static TimerService instance;
    
    TimerServiceIntAsync service;
    
    public static TimerService get() {
        if(instance == null)
            instance = new TimerService();
        
        return instance;
    }
    
    private TimerService() {
        service = (TimerServiceIntAsync)GWT.create(TimerServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void keepAlive(AsyncCallback<Void> callback) {
        service.keepAlive(callback);
    }

    @Override
    public void keepAlive() {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.keepAlive(callback);
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
