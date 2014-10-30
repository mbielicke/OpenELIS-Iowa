package org.openelis.portal.modules.sampleStatus.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class SampleStatusService implements SampleStatusServiceInt, SampleStatusServiceIntAsync {

    private static SampleStatusService  instance;

    private SampleStatusServiceIntAsync service;

    public static SampleStatusService get() {
        if (instance == null)
            instance = new SampleStatusService();

        return instance;
    }

    private SampleStatusService() {
        service = (SampleStatusServiceIntAsync)GWT.create(SampleStatusServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getSampleListForSampleStatusReport(Query query,
                                                   AsyncCallback<ArrayList<SampleViewVO>> callback) {
        service.getSampleListForSampleStatusReport(query, callback);
    }

    @Override
    public void getProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getProjectList(callback);
    }

    @Override
    public void getSampleQaEvents(ArrayList<Integer> sampleIds,
                                  AsyncCallback<HashMap<Integer, ArrayList<String>>> callback) {
        service.getSampleQaEvents(sampleIds, callback);
    }

    @Override
    public void getAnalysisQaEvents(ArrayList<Integer> analysisIds,
                                    AsyncCallback<HashMap<Integer, ArrayList<String>>> callback) {
        service.getAnalysisQaEvents(analysisIds, callback);
    }

    @Override
    public ArrayList<SampleViewVO> getSampleListForSampleStatusReport(Query query) throws Exception {
        Callback<ArrayList<SampleViewVO>> callback;

        callback = new Callback<ArrayList<SampleViewVO>>();
        service.getSampleListForSampleStatusReport(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.getProjectList(callback);
        return callback.getResult();
    }

    @Override
    public HashMap<Integer, ArrayList<String>> getSampleQaEvents(ArrayList<Integer> sampleIds) throws Exception {
        Callback<HashMap<Integer, ArrayList<String>>> callback;

        callback = new Callback<HashMap<Integer, ArrayList<String>>>();
        service.getSampleQaEvents(sampleIds, callback);
        return callback.getResult();
    }

    @Override
    public HashMap<Integer, ArrayList<String>> getAnalysisQaEvents(ArrayList<Integer> analysisIds) throws Exception {
        Callback<HashMap<Integer, ArrayList<String>>> callback;

        callback = new Callback<HashMap<Integer, ArrayList<String>>>();
        service.getAnalysisQaEvents(analysisIds, callback);
        return callback.getResult();
    }
}
