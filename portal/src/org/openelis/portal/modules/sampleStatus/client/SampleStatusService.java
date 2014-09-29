package org.openelis.portal.modules.sampleStatus.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
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
                                                   AsyncCallback<ArrayList<SampleStatusWebReportVO>> callback) {
        service.getSampleListForSampleStatusReport(query, callback);
    }

    @Override
    public void getSampleStatusProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getSampleStatusProjectList(callback);
    }

    @Override
    public void getSampleQaEventsBySampleId(Integer id,
                                            AsyncCallback<ArrayList<SampleQaEventViewDO>> callback) {
        service.getSampleQaEventsBySampleId(id, callback);
    }

    @Override
    public void getAnalysisQaEventsByAnalysisId(Integer id,
                                                AsyncCallback<ArrayList<AnalysisQaEventViewDO>> callback) {
        service.getAnalysisQaEventsByAnalysisId(id, callback);
    }

    @Override
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception {
        Callback<ArrayList<SampleStatusWebReportVO>> callback;
        
        callback = new Callback<ArrayList<SampleStatusWebReportVO>>();
        service.getSampleListForSampleStatusReport(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getSampleStatusProjectList(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SampleQaEventViewDO> getSampleQaEventsBySampleId(Integer id) throws Exception {
        Callback<ArrayList<SampleQaEventViewDO>> callback;
        
        callback = new Callback<ArrayList<SampleQaEventViewDO>>();
        service.getSampleQaEventsBySampleId(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalysisQaEventViewDO> getAnalysisQaEventsByAnalysisId(Integer id) throws Exception {
        Callback<ArrayList<AnalysisQaEventViewDO>> callback;
        
        callback = new Callback<ArrayList<AnalysisQaEventViewDO>>();
        service.getAnalysisQaEventsByAnalysisId(id, callback);
        return callback.getResult();
    }

}
