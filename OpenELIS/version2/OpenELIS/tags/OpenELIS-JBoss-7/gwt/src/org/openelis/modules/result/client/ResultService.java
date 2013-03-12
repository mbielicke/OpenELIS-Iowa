package org.openelis.modules.result.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AnalysisResultManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ResultService implements ResultServiceInt, ResultServiceIntAsync {
    
    static ResultService instance;
    
    ResultServiceIntAsync service;
    
    public static ResultService get() {
        if (instance == null)
            instance = new ResultService();
        
        return instance;
    }
    
    private ResultService() {
        service = (ResultServiceIntAsync)GWT.create(ResultServiceInt.class);
    }

    @Override
    public void fetchByAnalysisId(Integer analysisId, AsyncCallback<AnalysisResultManager> callback) {
        service.fetchByAnalysisId(analysisId, callback);
    }

    @Override
    public void fetchByAnalysisIdForDisplay(Integer analysisId,
                                            AsyncCallback<AnalysisResultManager> callback) {
        service.fetchByAnalysisIdForDisplay(analysisId, callback);
    }

    @Override
    public void fetchByTestId(AnalysisDO anDO, AsyncCallback<AnalysisResultManager> callback) {
        service.fetchByTestId(anDO, callback);
    }

    @Override
    public void fetchByTestIdForOrderImport(AnalysisDO anDO,
                                            AsyncCallback<AnalysisResultManager> callback) {
        service.fetchByTestIdForOrderImport(anDO, callback);
    }

    @Override
    public void getAliasList(Query query, AsyncCallback<ArrayList<AnalyteDO>> callback) {
        service.getAliasList(query, callback);
    }

    @Override
    public void merge(AnalysisResultManager manager, AsyncCallback<AnalysisResultManager> callback) {
        service.merge(manager, callback);
    }

    @Override
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        Callback<AnalysisResultManager> callback;
        
        callback = new Callback<AnalysisResultManager>();
        service.fetchByAnalysisIdForDisplay(analysisId, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        Callback<AnalysisResultManager> callback;
        
        callback = new Callback<AnalysisResultManager>();
        service.fetchByAnalysisId(analysisId, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisResultManager fetchByTestId(AnalysisDO anDO) throws Exception {
        Callback<AnalysisResultManager> callback;
        
        callback = new Callback<AnalysisResultManager>();
        service.fetchByTestId(anDO, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisResultManager fetchByTestIdForOrderImport(AnalysisDO anDO) throws Exception {
        Callback<AnalysisResultManager> callback;
        
        callback = new Callback<AnalysisResultManager>();
        service.fetchByTestIdForOrderImport(anDO, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisResultManager merge(AnalysisResultManager manager) throws Exception {
        Callback<AnalysisResultManager> callback;
        
        callback = new Callback<AnalysisResultManager>();
        service.merge(manager, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalyteDO> getAliasList(Query query) throws Exception {
        Callback<ArrayList<AnalyteDO>> callback;
        
        callback = new Callback<ArrayList<AnalyteDO>>();
        service.getAliasList(query, callback);
        return callback.getResult();
    }

}
