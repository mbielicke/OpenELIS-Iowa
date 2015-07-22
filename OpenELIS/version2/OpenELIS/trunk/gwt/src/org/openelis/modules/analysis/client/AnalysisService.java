package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleAnalysisVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.ui.services.TokenService;

public class AnalysisService implements AnalysisServiceInt, AnalysisServiceIntAsync {
    
    static AnalysisService instance;
    
    AnalysisServiceIntAsync service;
    
    public static AnalysisService get() {
        if(instance == null)
           instance = new AnalysisService();
        
        return instance;
    }
    
    private AnalysisService() {
        service = (AnalysisServiceIntAsync)GWT.create(AnalysisServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchById(Integer analysisId, AsyncCallback<AnalysisViewDO> callback) {
        service.fetchById(analysisId, callback);
    }

    @Override
    public void fetchBySampleItemId(Integer sampleItemId, AsyncCallback<AnalysisManager> callback) {
        service.fetchBySampleItemId(sampleItemId, callback);
    }

    @Override
    public void fetchByPatientId(Integer patientId, AsyncCallback<ArrayList<SampleAnalysisVO>> callback) {
        service.fetchByPatientId(patientId, callback);
    }

    @Override
    public void fetchQaByAnalysisId(Integer analysisId,
                                    AsyncCallback<AnalysisQaEventManager> callback) {
        service.fetchQaByAnalysisId(analysisId, callback);
    }

    @Override
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        Callback<AnalysisManager> callback;
        
        callback = new Callback<AnalysisManager>();
        service.fetchBySampleItemId(sampleItemId, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisViewDO fetchById(Integer analysisId) throws Exception {
        Callback<AnalysisViewDO> callback;
        
        callback = new Callback<AnalysisViewDO>();
        service.fetchById(analysisId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SampleAnalysisVO> fetchByPatientId(Integer patientId) throws Exception {
        Callback<ArrayList<SampleAnalysisVO>> callback;
        
        callback = new Callback<ArrayList<SampleAnalysisVO>>();
        service.fetchByPatientId(patientId, callback);
        return callback.getResult();
    }

    @Override
    public AnalysisQaEventManager fetchQaByAnalysisId(Integer analysisId) throws Exception {
        Callback<AnalysisQaEventManager> callback;
        
        callback = new Callback<AnalysisQaEventManager>();
        service.fetchQaByAnalysisId(analysisId, callback);
        return callback.getResult();
    }
    
    

}
