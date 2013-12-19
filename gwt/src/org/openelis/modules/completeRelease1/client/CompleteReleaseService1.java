package org.openelis.modules.completeRelease1.client;

import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class CompleteReleaseService1 implements CompleteReleaseServiceInt1,CompleteReleaseServiceInt1Async {
    
    static CompleteReleaseService1 instance;
    
   CompleteReleaseServiceInt1Async service;
    
    public static CompleteReleaseService1 get() {
        if (instance == null)
            instance = new CompleteReleaseService1();
        
        return instance;
    }
    
    private CompleteReleaseService1() {
        service = (CompleteReleaseServiceInt1Async)GWT.create(CompleteReleaseServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }
    
    
    @Override
    public ArrayList<SampleDataBundle> query(Query query) throws Exception {
        Callback<ArrayList<SampleDataBundle>> callback;
        
        callback = new Callback<ArrayList<SampleDataBundle>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<SampleDataBundle>> callback) {
        service.query(query, callback);
    }
   

}
