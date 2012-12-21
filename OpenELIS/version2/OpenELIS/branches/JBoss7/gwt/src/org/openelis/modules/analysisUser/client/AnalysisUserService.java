package org.openelis.modules.analysisUser.client;

import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AnalysisUserManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalysisUserService implements AnalysisUserServiceInt,AnalysisUserServiceIntAsync {
    
    static AnalysisUserService instance;
    
    AnalysisUserServiceIntAsync service;
    
    public static AnalysisUserService get() {
        if (instance == null)
            instance = new AnalysisUserService();
        
        return instance;
    }
    
    private AnalysisUserService() {
        service = (AnalysisUserServiceIntAsync)GWT.create(AnalysisUserServiceInt.class);
    }

    @Override
    public void fetchByAnalysisId(Integer analysisId, AsyncCallback<AnalysisUserManager> callback) {
        service.fetchByAnalysisId(analysisId, callback);
    }

    @Override
    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        Callback<AnalysisUserManager> callback;
        
        callback = new Callback<AnalysisUserManager>();
        service.fetchByAnalysisId(analysisId, callback);
        return callback.getResult();
    }

}
