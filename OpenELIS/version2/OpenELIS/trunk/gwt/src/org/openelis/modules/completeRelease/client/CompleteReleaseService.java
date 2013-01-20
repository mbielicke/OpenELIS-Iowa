package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleDataBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CompleteReleaseService implements CompleteReleaseServiceInt, CompleteReleaseServiceIntAsync {
    
    static CompleteReleaseService instance;
    
    CompleteReleaseServiceIntAsync service;
    
    public static CompleteReleaseService get() {
        if (instance == null)
            instance = new CompleteReleaseService();
        
        return instance;
    }
    
    private CompleteReleaseService() {
        service = (CompleteReleaseServiceIntAsync)GWT.create(CompleteReleaseServiceInt.class);
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
