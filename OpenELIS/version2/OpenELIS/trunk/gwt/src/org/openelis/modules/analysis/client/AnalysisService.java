package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleAnalysisVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

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
    public void fetchByPatientId(Integer patientId, AsyncCallback<ArrayList<SampleAnalysisVO>> callback) {
        service.fetchByPatientId(patientId, callback);
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
}
