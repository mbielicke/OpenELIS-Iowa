package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleTrackingService implements SampleTrackingServiceInt,
                                              SampleTrackingServiceIntAsync {
    
    static SampleTrackingService instance;
    
    SampleTrackingServiceIntAsync service;
    
    public static SampleTrackingService get() {
        if(instance == null)
            instance = new SampleTrackingService();
        
        return instance;
    }
    
    private SampleTrackingService() {
        service = (SampleTrackingServiceIntAsync)GWT.create(SampleTrackingServiceInt.class);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<SampleManager>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<SampleManager> query(Query query) throws Exception {
        Callback<ArrayList<SampleManager>> callback;
        
        callback = new Callback<ArrayList<SampleManager>>();
        service.query(query, callback);
        return callback.getResult();
    }

}
